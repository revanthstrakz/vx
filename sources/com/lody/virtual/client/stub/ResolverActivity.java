package com.lody.virtual.client.stub;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.AuthorityEntry;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ResolveInfo.DisplayNameComparator;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.lody.virtual.C0966R;
import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.p007os.VUserHandle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ResolverActivity extends Activity implements OnItemClickListener {
    private static final boolean DEBUG = false;
    private static final String TAG = "ResolverActivity";
    private AlertDialog dialog;
    /* access modifiers changed from: private */
    public ResolveListAdapter mAdapter;
    private Button mAlwaysButton;
    /* access modifiers changed from: private */
    public boolean mAlwaysUseOption;
    private int mIconDpi;
    /* access modifiers changed from: private */
    public int mIconSize;
    private int mLastSelected = -1;
    private int mLaunchedFromUid;
    private ListView mListView;
    private int mMaxColumns;
    private Button mOnceButton;
    protected Bundle mOptions;
    /* access modifiers changed from: private */
    public PackageManager mPm;
    private boolean mRegistered;
    protected int mRequestCode;
    protected String mResultWho;
    /* access modifiers changed from: private */
    public boolean mShowExtended;

    private final class DisplayResolveInfo {
        Drawable displayIcon;
        CharSequence displayLabel;
        CharSequence extendedInfo;
        Intent origIntent;

        /* renamed from: ri */
        ResolveInfo f179ri;

        DisplayResolveInfo(ResolveInfo resolveInfo, CharSequence charSequence, CharSequence charSequence2, Intent intent) {
            this.f179ri = resolveInfo;
            this.displayLabel = charSequence;
            this.extendedInfo = charSequence2;
            this.origIntent = intent;
        }
    }

    class ItemLongClickListener implements OnItemLongClickListener {
        ItemLongClickListener() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long j) {
            ResolverActivity.this.showAppDetails(ResolverActivity.this.mAdapter.resolveInfoForPosition(i));
            return true;
        }
    }

    class LoadIconTask extends AsyncTask<DisplayResolveInfo, Void, DisplayResolveInfo> {
        LoadIconTask() {
        }

        /* access modifiers changed from: protected */
        public DisplayResolveInfo doInBackground(DisplayResolveInfo... displayResolveInfoArr) {
            DisplayResolveInfo displayResolveInfo = displayResolveInfoArr[0];
            if (displayResolveInfo.displayIcon == null) {
                displayResolveInfo.displayIcon = ResolverActivity.this.loadIconForResolveInfo(displayResolveInfo.f179ri);
            }
            return displayResolveInfo;
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(DisplayResolveInfo displayResolveInfo) {
            ResolverActivity.this.mAdapter.notifyDataSetChanged();
        }
    }

    private final class ResolveListAdapter extends BaseAdapter {
        private final List<ResolveInfo> mBaseResolveList;
        private final LayoutInflater mInflater;
        private int mInitialHighlight = -1;
        private final Intent[] mInitialIntents;
        private final Intent mIntent;
        private ResolveInfo mLastChosen;
        private final int mLaunchedFromUid;
        List<DisplayResolveInfo> mList;
        List<ResolveInfo> mOrigResolveList;

        public long getItemId(int i) {
            return (long) i;
        }

        public ResolveListAdapter(Context context, Intent intent, Intent[] intentArr, List<ResolveInfo> list, int i) {
            this.mIntent = new Intent(intent);
            this.mInitialIntents = intentArr;
            this.mBaseResolveList = list;
            this.mLaunchedFromUid = i;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            this.mList = new ArrayList();
            rebuildList();
        }

        public void handlePackagesChanged() {
            getCount();
            rebuildList();
            notifyDataSetChanged();
            if (getCount() == 0) {
                ResolverActivity.this.finish();
            }
        }

        public int getInitialHighlight() {
            return this.mInitialHighlight;
        }

        private void rebuildList() {
            List<ResolveInfo> queryIntentActivities;
            this.mList.clear();
            if (this.mBaseResolveList != null) {
                queryIntentActivities = this.mBaseResolveList;
                this.mOrigResolveList = null;
            } else {
                queryIntentActivities = ResolverActivity.this.mPm.queryIntentActivities(this.mIntent, 65536 | (ResolverActivity.this.mAlwaysUseOption ? 64 : 0));
                this.mOrigResolveList = queryIntentActivities;
            }
            List<ResolveInfo> list = queryIntentActivities;
            if (list != null) {
                int size = list.size();
                if (size > 0) {
                    ResolveInfo resolveInfo = (ResolveInfo) list.get(0);
                    int i = size;
                    for (int i2 = 1; i2 < i; i2++) {
                        ResolveInfo resolveInfo2 = (ResolveInfo) list.get(i2);
                        if (resolveInfo.priority != resolveInfo2.priority || resolveInfo.isDefault != resolveInfo2.isDefault) {
                            while (i2 < i) {
                                if (this.mOrigResolveList == list) {
                                    this.mOrigResolveList = new ArrayList(this.mOrigResolveList);
                                }
                                list.remove(i2);
                                i--;
                            }
                        }
                    }
                    if (i > 1) {
                        Collections.sort(list, new DisplayNameComparator(ResolverActivity.this.mPm));
                    }
                    if (this.mInitialIntents != null) {
                        for (Intent intent : this.mInitialIntents) {
                            if (intent != null) {
                                ActivityInfo resolveActivityInfo = intent.resolveActivityInfo(ResolverActivity.this.getPackageManager(), 0);
                                if (resolveActivityInfo == null) {
                                    String str = ResolverActivity.TAG;
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("No activity found for ");
                                    sb.append(intent);
                                    VLog.m91w(str, sb.toString(), new Object[0]);
                                } else {
                                    ResolveInfo resolveInfo3 = new ResolveInfo();
                                    resolveInfo3.activityInfo = resolveActivityInfo;
                                    if (intent instanceof LabeledIntent) {
                                        LabeledIntent labeledIntent = (LabeledIntent) intent;
                                        resolveInfo3.resolvePackageName = labeledIntent.getSourcePackage();
                                        resolveInfo3.labelRes = labeledIntent.getLabelResource();
                                        resolveInfo3.nonLocalizedLabel = labeledIntent.getNonLocalizedLabel();
                                        resolveInfo3.icon = labeledIntent.getIconResource();
                                    }
                                    List<DisplayResolveInfo> list2 = this.mList;
                                    DisplayResolveInfo displayResolveInfo = new DisplayResolveInfo(resolveInfo3, resolveInfo3.loadLabel(ResolverActivity.this.getPackageManager()), null, intent);
                                    list2.add(displayResolveInfo);
                                }
                            }
                        }
                    }
                    ResolveInfo resolveInfo4 = (ResolveInfo) list.get(0);
                    CharSequence loadLabel = resolveInfo4.loadLabel(ResolverActivity.this.mPm);
                    ResolverActivity.this.mShowExtended = false;
                    ResolveInfo resolveInfo5 = resolveInfo4;
                    CharSequence charSequence = loadLabel;
                    int i3 = 0;
                    for (int i4 = 1; i4 < i; i4++) {
                        if (charSequence == null) {
                            charSequence = resolveInfo5.activityInfo.packageName;
                        }
                        ResolveInfo resolveInfo6 = (ResolveInfo) list.get(i4);
                        CharSequence loadLabel2 = resolveInfo6.loadLabel(ResolverActivity.this.mPm);
                        if (loadLabel2 == null) {
                            loadLabel2 = resolveInfo6.activityInfo.packageName;
                        }
                        CharSequence charSequence2 = loadLabel2;
                        if (!charSequence2.equals(charSequence)) {
                            processGroup(list, i3, i4 - 1, resolveInfo5, charSequence);
                            i3 = i4;
                            resolveInfo5 = resolveInfo6;
                            charSequence = charSequence2;
                        }
                    }
                    processGroup(list, i3, i - 1, resolveInfo5, charSequence);
                }
            }
        }

        private void processGroup(List<ResolveInfo> list, int i, int i2, ResolveInfo resolveInfo, CharSequence charSequence) {
            if ((i2 - i) + 1 == 1) {
                if (this.mLastChosen != null && this.mLastChosen.activityInfo.packageName.equals(resolveInfo.activityInfo.packageName) && this.mLastChosen.activityInfo.name.equals(resolveInfo.activityInfo.name)) {
                    this.mInitialHighlight = this.mList.size();
                }
                List<DisplayResolveInfo> list2 = this.mList;
                DisplayResolveInfo displayResolveInfo = new DisplayResolveInfo(resolveInfo, charSequence, null, null);
                list2.add(displayResolveInfo);
                return;
            }
            ResolverActivity.this.mShowExtended = true;
            boolean z = false;
            CharSequence loadLabel = resolveInfo.activityInfo.applicationInfo.loadLabel(ResolverActivity.this.mPm);
            if (loadLabel == null) {
                z = true;
            }
            if (!z) {
                HashSet hashSet = new HashSet();
                hashSet.add(loadLabel);
                int i3 = i + 1;
                while (true) {
                    if (i3 > i2) {
                        break;
                    }
                    CharSequence loadLabel2 = ((ResolveInfo) list.get(i3)).activityInfo.applicationInfo.loadLabel(ResolverActivity.this.mPm);
                    if (loadLabel2 == null || hashSet.contains(loadLabel2)) {
                        z = true;
                    } else {
                        hashSet.add(loadLabel2);
                        i3++;
                    }
                }
                z = true;
                hashSet.clear();
            }
            while (i <= i2) {
                ResolveInfo resolveInfo2 = (ResolveInfo) list.get(i);
                if (this.mLastChosen != null && this.mLastChosen.activityInfo.packageName.equals(resolveInfo2.activityInfo.packageName) && this.mLastChosen.activityInfo.name.equals(resolveInfo2.activityInfo.name)) {
                    this.mInitialHighlight = this.mList.size();
                }
                if (z) {
                    List<DisplayResolveInfo> list3 = this.mList;
                    DisplayResolveInfo displayResolveInfo2 = new DisplayResolveInfo(resolveInfo2, charSequence, resolveInfo2.activityInfo.packageName, null);
                    list3.add(displayResolveInfo2);
                } else {
                    List<DisplayResolveInfo> list4 = this.mList;
                    DisplayResolveInfo displayResolveInfo3 = new DisplayResolveInfo(resolveInfo2, charSequence, resolveInfo2.activityInfo.applicationInfo.loadLabel(ResolverActivity.this.mPm), null);
                    list4.add(displayResolveInfo3);
                }
                i++;
            }
        }

        public ResolveInfo resolveInfoForPosition(int i) {
            return ((DisplayResolveInfo) this.mList.get(i)).f179ri;
        }

        public Intent intentForPosition(int i) {
            DisplayResolveInfo displayResolveInfo = (DisplayResolveInfo) this.mList.get(i);
            Intent intent = new Intent(displayResolveInfo.origIntent != null ? displayResolveInfo.origIntent : this.mIntent);
            intent.addFlags(50331648);
            ActivityInfo activityInfo = displayResolveInfo.f179ri.activityInfo;
            intent.setComponent(new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name));
            return intent;
        }

        public int getCount() {
            return this.mList.size();
        }

        public Object getItem(int i) {
            return this.mList.get(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = this.mInflater.inflate(C0966R.layout.resolve_list_item, viewGroup, false);
                ViewHolder viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
                LayoutParams layoutParams = viewHolder.icon.getLayoutParams();
                int access$300 = ResolverActivity.this.mIconSize;
                layoutParams.height = access$300;
                layoutParams.width = access$300;
            }
            bindView(view, (DisplayResolveInfo) this.mList.get(i));
            return view;
        }

        private final void bindView(View view, DisplayResolveInfo displayResolveInfo) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            viewHolder.text.setText(displayResolveInfo.displayLabel);
            if (ResolverActivity.this.mShowExtended) {
                viewHolder.text2.setVisibility(0);
                viewHolder.text2.setText(displayResolveInfo.extendedInfo);
            } else {
                viewHolder.text2.setVisibility(8);
            }
            if (displayResolveInfo.displayIcon == null) {
                new LoadIconTask().execute(new DisplayResolveInfo[]{displayResolveInfo});
            }
            viewHolder.icon.setImageDrawable(displayResolveInfo.displayIcon);
        }
    }

    static class ViewHolder {
        public ImageView icon;
        public TextView text;
        public TextView text2;

        public ViewHolder(View view) {
            this.text = (TextView) view.findViewById(C0966R.C0967id.text1);
            this.text2 = (TextView) view.findViewById(C0966R.C0967id.text2);
            this.icon = (ImageView) view.findViewById(C0966R.C0967id.icon);
        }
    }

    private Intent makeMyIntent() {
        Intent intent = new Intent(getIntent());
        intent.setComponent(null);
        intent.setFlags(intent.getFlags() & -8388609);
        return intent;
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"MissingSuperCall"})
    public void onCreate(Bundle bundle) {
        int i;
        Intent makeMyIntent = makeMyIntent();
        Set categories = makeMyIntent.getCategories();
        if (!"android.intent.action.MAIN".equals(makeMyIntent.getAction()) || categories == null || categories.size() != 1 || !categories.contains("android.intent.category.HOME")) {
            i = C0966R.string.choose;
        } else {
            i = C0966R.string.choose;
        }
        onCreate(bundle, makeMyIntent, getResources().getText(i), null, null, true, VUserHandle.getCallingUserId());
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle, Intent intent, CharSequence charSequence, Intent[] intentArr, List<ResolveInfo> list, boolean z, int i) {
        super.onCreate(bundle);
        this.mLaunchedFromUid = i;
        this.mPm = getPackageManager();
        this.mAlwaysUseOption = z;
        this.mMaxColumns = getResources().getInteger(C0966R.integer.config_maxResolverActivityColumns);
        this.mRegistered = true;
        ActivityManager activityManager = (ActivityManager) getSystemService(ServiceManagerNative.ACTIVITY);
        this.mIconDpi = activityManager.getLauncherLargeIconDensity();
        this.mIconSize = activityManager.getLauncherLargeIconSize();
        ResolveListAdapter resolveListAdapter = new ResolveListAdapter(this, intent, intentArr, list, this.mLaunchedFromUid);
        this.mAdapter = resolveListAdapter;
        int count = this.mAdapter.getCount();
        if (VERSION.SDK_INT >= 17 && this.mLaunchedFromUid < 0) {
            finish();
        } else if (count == 1) {
            startSelected(0, false);
            this.mRegistered = false;
            finish();
        } else {
            Builder builder = new Builder(this);
            builder.setTitle(charSequence);
            if (count > 1) {
                this.mListView = new ListView(this);
                this.mListView.setAdapter(this.mAdapter);
                this.mListView.setOnItemClickListener(this);
                this.mListView.setOnItemLongClickListener(new ItemLongClickListener());
                builder.setView(this.mListView);
                if (z) {
                    this.mListView.setChoiceMode(1);
                }
            } else {
                builder.setMessage(C0966R.string.noApplications);
            }
            builder.setOnCancelListener(new OnCancelListener() {
                public void onCancel(DialogInterface dialogInterface) {
                    ResolverActivity.this.finish();
                }
            });
            this.dialog = builder.show();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
        }
        super.onDestroy();
    }

    /* access modifiers changed from: 0000 */
    @TargetApi(15)
    public Drawable getIcon(Resources resources, int i) {
        try {
            return resources.getDrawableForDensity(i, this.mIconDpi);
        } catch (NotFoundException unused) {
            return null;
        }
    }

    /* access modifiers changed from: 0000 */
    public Drawable loadIconForResolveInfo(ResolveInfo resolveInfo) {
        try {
            if (!(resolveInfo.resolvePackageName == null || resolveInfo.icon == 0)) {
                Drawable icon = getIcon(this.mPm.getResourcesForApplication(resolveInfo.resolvePackageName), resolveInfo.icon);
                if (icon != null) {
                    return icon;
                }
            }
            int iconResource = resolveInfo.getIconResource();
            if (iconResource != 0) {
                Drawable icon2 = getIcon(this.mPm.getResourcesForApplication(resolveInfo.activityInfo.packageName), iconResource);
                if (icon2 != null) {
                    return icon2;
                }
            }
        } catch (NameNotFoundException e) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Couldn't find resources for package\n");
            sb.append(VLog.getStackTraceString(e));
            VLog.m87e(str, sb.toString(), new Object[0]);
        }
        return resolveInfo.loadIcon(this.mPm);
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        super.onRestart();
        if (!this.mRegistered) {
            this.mRegistered = true;
        }
        this.mAdapter.handlePackagesChanged();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        if (this.mRegistered) {
            this.mRegistered = false;
        }
        if ((getIntent().getFlags() & 268435456) != 0 && !isChangingConfigurations()) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        if (this.mAlwaysUseOption) {
            int checkedItemPosition = this.mListView.getCheckedItemPosition();
            boolean z = checkedItemPosition != -1;
            this.mLastSelected = checkedItemPosition;
            this.mAlwaysButton.setEnabled(z);
            this.mOnceButton.setEnabled(z);
            if (z) {
                this.mListView.setSelection(checkedItemPosition);
            }
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        int checkedItemPosition = this.mListView.getCheckedItemPosition();
        boolean z = checkedItemPosition != -1;
        if (!this.mAlwaysUseOption || (z && this.mLastSelected == checkedItemPosition)) {
            startSelected(i, false);
            return;
        }
        this.mAlwaysButton.setEnabled(z);
        this.mOnceButton.setEnabled(z);
        if (z) {
            this.mListView.smoothScrollToPosition(checkedItemPosition);
        }
        this.mLastSelected = checkedItemPosition;
    }

    /* access modifiers changed from: 0000 */
    public void startSelected(int i, boolean z) {
        if (!isFinishing()) {
            onIntentSelected(this.mAdapter.resolveInfoForPosition(i), this.mAdapter.intentForPosition(i), z);
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onIntentSelected(ResolveInfo resolveInfo, Intent intent, boolean z) {
        if (this.mAlwaysUseOption && this.mAdapter.mOrigResolveList != null) {
            IntentFilter intentFilter = new IntentFilter();
            if (intent.getAction() != null) {
                intentFilter.addAction(intent.getAction());
            }
            Set<String> categories = intent.getCategories();
            if (categories != null) {
                for (String addCategory : categories) {
                    intentFilter.addCategory(addCategory);
                }
            }
            intentFilter.addCategory("android.intent.category.DEFAULT");
            int i = resolveInfo.match & 268369920;
            Uri data = intent.getData();
            String str = null;
            if (i == 6291456) {
                String resolveType = intent.resolveType(this);
                if (resolveType != null) {
                    try {
                        intentFilter.addDataType(resolveType);
                    } catch (MalformedMimeTypeException e) {
                        String str2 = TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("mimeType\n");
                        sb.append(VLog.getStackTraceString(e));
                        VLog.m91w(str2, sb.toString(), new Object[0]);
                        intentFilter = null;
                    }
                }
            }
            if (data != null && data.getScheme() != null && (i != 6291456 || (!"file".equals(data.getScheme()) && !"content".equals(data.getScheme())))) {
                intentFilter.addDataScheme(data.getScheme());
                if (VERSION.SDK_INT >= 19) {
                    Iterator schemeSpecificPartsIterator = resolveInfo.filter.schemeSpecificPartsIterator();
                    if (schemeSpecificPartsIterator != null) {
                        String schemeSpecificPart = data.getSchemeSpecificPart();
                        while (true) {
                            if (schemeSpecificPart == null || !schemeSpecificPartsIterator.hasNext()) {
                                break;
                            }
                            PatternMatcher patternMatcher = (PatternMatcher) schemeSpecificPartsIterator.next();
                            if (patternMatcher.match(schemeSpecificPart)) {
                                intentFilter.addDataSchemeSpecificPart(patternMatcher.getPath(), patternMatcher.getType());
                                break;
                            }
                        }
                    }
                    Iterator authoritiesIterator = resolveInfo.filter.authoritiesIterator();
                    if (authoritiesIterator != null) {
                        while (true) {
                            if (!authoritiesIterator.hasNext()) {
                                break;
                            }
                            AuthorityEntry authorityEntry = (AuthorityEntry) authoritiesIterator.next();
                            if (authorityEntry.match(data) >= 0) {
                                int port = authorityEntry.getPort();
                                String host = authorityEntry.getHost();
                                if (port >= 0) {
                                    str = Integer.toString(port);
                                }
                                intentFilter.addDataAuthority(host, str);
                            }
                        }
                    }
                    Iterator pathsIterator = resolveInfo.filter.pathsIterator();
                    if (pathsIterator != null) {
                        String path = data.getPath();
                        while (true) {
                            if (path == null || !pathsIterator.hasNext()) {
                                break;
                            }
                            PatternMatcher patternMatcher2 = (PatternMatcher) pathsIterator.next();
                            if (patternMatcher2.match(path)) {
                                intentFilter.addDataPath(patternMatcher2.getPath(), patternMatcher2.getType());
                                break;
                            }
                        }
                    }
                }
            }
            if (intentFilter != null) {
                int size = this.mAdapter.mOrigResolveList.size();
                ComponentName[] componentNameArr = new ComponentName[size];
                int i2 = 0;
                for (int i3 = 0; i3 < size; i3++) {
                    ResolveInfo resolveInfo2 = (ResolveInfo) this.mAdapter.mOrigResolveList.get(i3);
                    componentNameArr[i3] = new ComponentName(resolveInfo2.activityInfo.packageName, resolveInfo2.activityInfo.name);
                    if (resolveInfo2.match > i2) {
                        i2 = resolveInfo2.match;
                    }
                }
                if (z) {
                    getPackageManager().addPreferredActivity(intentFilter, i2, componentNameArr, intent.getComponent());
                } else {
                    try {
                        Reflect.m80on((Object) VClientImpl.get().getCurrentApplication().getPackageManager()).call("setLastChosenActivity", intent, intent.resolveTypeIfNeeded(getContentResolver()), Integer.valueOf(65536), intentFilter, Integer.valueOf(i2), intent.getComponent());
                    } catch (Exception e2) {
                        String str3 = TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Error calling setLastChosenActivity\n");
                        sb2.append(VLog.getStackTraceString(e2));
                        VLog.m86d(str3, sb2.toString(), new Object[0]);
                    }
                }
            }
        }
        if (intent != null) {
            ActivityInfo resolveActivityInfo = VirtualCore.get().resolveActivityInfo(intent, this.mLaunchedFromUid);
            if (resolveActivityInfo == null) {
                startActivity(intent);
                return;
            }
            VActivityManager.get().startActivity(intent, resolveActivityInfo, null, this.mOptions, this.mResultWho, this.mRequestCode, this.mLaunchedFromUid);
        }
    }

    /* access modifiers changed from: 0000 */
    public void showAppDetails(ResolveInfo resolveInfo) {
        startActivity(new Intent().setAction("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.fromParts(ServiceManagerNative.PACKAGE, resolveInfo.activityInfo.packageName, null)).addFlags(524288));
    }
}
