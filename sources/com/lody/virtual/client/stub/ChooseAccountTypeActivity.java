package com.lody.virtual.client.stub;

import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.lody.virtual.C0966R;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VAccountManager;
import com.lody.virtual.helper.utils.VLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class ChooseAccountTypeActivity extends Activity {
    private static final String TAG = "AccountChooser";
    /* access modifiers changed from: private */
    public ArrayList<AuthInfo> mAuthenticatorInfosToDisplay;
    private HashMap<String, AuthInfo> mTypeToAuthenticatorInfo = new HashMap<>();

    private static class AccountArrayAdapter extends ArrayAdapter<AuthInfo> {
        private ArrayList<AuthInfo> mInfos;
        private LayoutInflater mLayoutInflater;

        AccountArrayAdapter(Context context, int i, ArrayList<AuthInfo> arrayList) {
            super(context, i, arrayList);
            this.mInfos = arrayList;
            this.mLayoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = this.mLayoutInflater.inflate(C0966R.layout.choose_account_row, null);
                viewHolder = new ViewHolder();
                viewHolder.text = (TextView) view.findViewById(C0966R.C0967id.account_row_text);
                viewHolder.icon = (ImageView) view.findViewById(C0966R.C0967id.account_row_icon);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.text.setText(((AuthInfo) this.mInfos.get(i)).name);
            viewHolder.icon.setImageDrawable(((AuthInfo) this.mInfos.get(i)).drawable);
            return view;
        }
    }

    private static class AuthInfo {
        final AuthenticatorDescription desc;
        final Drawable drawable;
        final String name;

        AuthInfo(AuthenticatorDescription authenticatorDescription, String str, Drawable drawable2) {
            this.desc = authenticatorDescription;
            this.name = str;
            this.drawable = drawable2;
        }
    }

    private static class ViewHolder {
        ImageView icon;
        TextView text;

        private ViewHolder() {
        }
    }

    public void onCreate(Bundle bundle) {
        HashSet hashSet;
        super.onCreate(bundle);
        String[] stringArrayExtra = getIntent().getStringArrayExtra(ChooseTypeAndAccountActivity.EXTRA_ALLOWABLE_ACCOUNT_TYPES_STRING_ARRAY);
        if (stringArrayExtra != null) {
            hashSet = new HashSet(stringArrayExtra.length);
            Collections.addAll(hashSet, stringArrayExtra);
        } else {
            hashSet = null;
        }
        buildTypeToAuthDescriptionMap();
        this.mAuthenticatorInfosToDisplay = new ArrayList<>(this.mTypeToAuthenticatorInfo.size());
        for (Entry entry : this.mTypeToAuthenticatorInfo.entrySet()) {
            String str = (String) entry.getKey();
            AuthInfo authInfo = (AuthInfo) entry.getValue();
            if (hashSet == null || hashSet.contains(str)) {
                this.mAuthenticatorInfosToDisplay.add(authInfo);
            }
        }
        if (this.mAuthenticatorInfosToDisplay.isEmpty()) {
            Bundle bundle2 = new Bundle();
            bundle2.putString("errorMessage", "no allowable account types");
            setResult(-1, new Intent().putExtras(bundle2));
            finish();
        } else if (this.mAuthenticatorInfosToDisplay.size() == 1) {
            setResultAndFinish(((AuthInfo) this.mAuthenticatorInfosToDisplay.get(0)).desc.type);
        } else {
            setContentView(C0966R.layout.choose_account_type);
            ListView listView = (ListView) findViewById(16908298);
            listView.setAdapter(new AccountArrayAdapter(this, 17367043, this.mAuthenticatorInfosToDisplay));
            listView.setChoiceMode(0);
            listView.setTextFilterEnabled(false);
            listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                    ChooseAccountTypeActivity.this.setResultAndFinish(((AuthInfo) ChooseAccountTypeActivity.this.mAuthenticatorInfosToDisplay.get(i)).desc.type);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void setResultAndFinish(String str) {
        Bundle bundle = new Bundle();
        bundle.putString("accountType", str);
        setResult(-1, new Intent().putExtras(bundle));
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("ChooseAccountTypeActivity.setResultAndFinish: selected account type ");
        sb.append(str);
        VLog.m90v(str2, sb.toString(), new Object[0]);
        finish();
    }

    private void buildTypeToAuthDescriptionMap() {
        AuthenticatorDescription[] authenticatorTypes;
        Drawable drawable;
        for (AuthenticatorDescription authenticatorDescription : VAccountManager.get().getAuthenticatorTypes()) {
            String str = null;
            try {
                Resources resources = VirtualCore.get().getResources(authenticatorDescription.packageName);
                drawable = resources.getDrawable(authenticatorDescription.iconId);
                try {
                    CharSequence text = resources.getText(authenticatorDescription.labelId);
                    try {
                        str = text.toString();
                    } catch (NotFoundException unused) {
                        str = text.toString();
                        String str2 = TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("No icon resource for account type ");
                        sb.append(authenticatorDescription.type);
                        VLog.m91w(str2, sb.toString(), new Object[0]);
                        this.mTypeToAuthenticatorInfo.put(authenticatorDescription.type, new AuthInfo(authenticatorDescription, str, drawable));
                    }
                } catch (NotFoundException unused2) {
                    String str22 = TAG;
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("No icon resource for account type ");
                    sb2.append(authenticatorDescription.type);
                    VLog.m91w(str22, sb2.toString(), new Object[0]);
                    this.mTypeToAuthenticatorInfo.put(authenticatorDescription.type, new AuthInfo(authenticatorDescription, str, drawable));
                }
            } catch (NotFoundException unused3) {
                drawable = null;
                String str222 = TAG;
                StringBuilder sb22 = new StringBuilder();
                sb22.append("No icon resource for account type ");
                sb22.append(authenticatorDescription.type);
                VLog.m91w(str222, sb22.toString(), new Object[0]);
                this.mTypeToAuthenticatorInfo.put(authenticatorDescription.type, new AuthInfo(authenticatorDescription, str, drawable));
            }
            this.mTypeToAuthenticatorInfo.put(authenticatorDescription.type, new AuthInfo(authenticatorDescription, str, drawable));
        }
    }
}
