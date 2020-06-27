package p013io.virtualapp.home;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.p001v4.app.FragmentActivity;
import android.support.p004v7.widget.DividerItemDecoration;
import android.support.p004v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.lody.virtual.client.core.VirtualCore;
import io.va.exposed.R;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import p013io.virtualapp.XApp;
import p013io.virtualapp.abs.p014ui.VFragment;
import p013io.virtualapp.home.adapters.CloneAppListAdapter;
import p013io.virtualapp.home.adapters.CloneAppListAdapter.ItemEventListener;
import p013io.virtualapp.home.models.AppInfo;
import p013io.virtualapp.home.models.AppInfoLite;
import p013io.virtualapp.sys.Installd;
import p013io.virtualapp.widgets.DragSelectRecyclerView;
import p013io.virtualapp.widgets.DragSelectRecyclerViewAdapter;
import p013io.virtualapp.widgets.DragSelectRecyclerViewAdapter.SelectionListener;

/* renamed from: io.virtualapp.home.ListAppFragment */
public class ListAppFragment extends VFragment<ListAppPresenter> implements ListAppView {
    private static final String KEY_SELECT_FROM = "key_select_from";
    private static final int REQUEST_GET_FILE = 1;
    /* access modifiers changed from: private */
    public CloneAppListAdapter mAdapter;
    private Button mInstallButton;
    private ProgressBar mProgressBar;
    private DragSelectRecyclerView mRecyclerView;
    private View mSelectFromExternal;

    @Nullable
    public /* bridge */ /* synthetic */ Activity getActivity() {
        return super.getActivity();
    }

    public static ListAppFragment newInstance(File file) {
        Bundle bundle = new Bundle();
        if (file != null) {
            bundle.putString(KEY_SELECT_FROM, file.getPath());
        }
        ListAppFragment listAppFragment = new ListAppFragment();
        listAppFragment.setArguments(bundle);
        return listAppFragment;
    }

    private File getSelectFrom() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            String string = arguments.getString(KEY_SELECT_FROM);
            if (string != null) {
                return new File(string);
            }
        }
        return null;
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_list_app, null);
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mAdapter.saveInstanceState(bundle);
    }

    private void whatIsTaiChi() {
        try {
            new Builder(getContext()).setTitle(R.string.what_is_exp).setMessage(R.string.exp_tips).setPositiveButton(R.string.exp_introduce_title, new OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ListAppFragment.lambda$whatIsTaiChi$56(ListAppFragment.this, dialogInterface, i);
                }
            }).setNegativeButton(R.string.about_donate_title, new OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ListAppFragment.lambda$whatIsTaiChi$57(ListAppFragment.this, dialogInterface, i);
                }
            }).create().show();
        } catch (Throwable unused) {
        }
    }

    public static /* synthetic */ void lambda$whatIsTaiChi$56(ListAppFragment listAppFragment, DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("https://www.coolapk.com/apk/me.weishu.exp"));
        listAppFragment.startActivity(intent);
    }

    public static /* synthetic */ void lambda$whatIsTaiChi$57(ListAppFragment listAppFragment, DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("https://vxposed.com/donate.html"));
        listAppFragment.startActivity(intent);
    }

    private void chooseInstallWay(Runnable runnable, String str) {
        try {
            new Builder(getContext()).setTitle(R.string.install_choose_way).setMessage(R.string.install_choose_content).setPositiveButton(R.string.install_choose_taichi, new OnClickListener(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ListAppFragment.lambda$chooseInstallWay$60(ListAppFragment.this, this.f$1, dialogInterface, i);
                }
            }).setNegativeButton("VirtualXposed", new OnClickListener(runnable) {
                private final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ListAppFragment.lambda$chooseInstallWay$61(ListAppFragment.this, this.f$1, dialogInterface, i);
                }
            }).setNeutralButton(R.string.what_is_exp, new OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ListAppFragment.lambda$chooseInstallWay$62(ListAppFragment.this, dialogInterface, i);
                }
            }).create().show();
        } catch (Throwable unused) {
        }
    }

    public static /* synthetic */ void lambda$chooseInstallWay$60(ListAppFragment listAppFragment, String str, DialogInterface dialogInterface, int i) {
        try {
            listAppFragment.getActivity().getPackageManager().getPackageInfo(VirtualCore.TAICHI_PACKAGE, 0);
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(VirtualCore.TAICHI_PACKAGE, "me.weishu.exp.ui.MainActivity"));
            intent.putExtra("path", str);
            listAppFragment.startActivity(intent);
        } catch (NameNotFoundException unused) {
            new Builder(listAppFragment.getContext()).setTitle(17039380).setMessage(R.string.install_taichi_not_exist).setPositiveButton(R.string.install_go_to_install_exp, new OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ListAppFragment.lambda$null$58(ListAppFragment.this, dialogInterface, i);
                }
            }).create().show();
        } catch (Throwable unused2) {
            new Builder(listAppFragment.getContext()).setTitle(17039380).setMessage(R.string.install_taichi_while_old_version).setPositiveButton(R.string.install_go_latest_exp, new OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ListAppFragment.lambda$null$59(ListAppFragment.this, dialogInterface, i);
                }
            }).create().show();
        }
        listAppFragment.finishActivity();
    }

    public static /* synthetic */ void lambda$null$58(ListAppFragment listAppFragment, DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("https://www.coolapk.com/apk/me.weishu.exp"));
        listAppFragment.startActivity(intent);
    }

    public static /* synthetic */ void lambda$null$59(ListAppFragment listAppFragment, DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("https://taichi.cool"));
        listAppFragment.startActivity(intent);
    }

    public static /* synthetic */ void lambda$chooseInstallWay$61(ListAppFragment listAppFragment, Runnable runnable, DialogInterface dialogInterface, int i) {
        if (runnable != null) {
            runnable.run();
        }
        listAppFragment.finishActivity();
    }

    public static /* synthetic */ void lambda$chooseInstallWay$62(ListAppFragment listAppFragment, DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("https://taichi.cool"));
        listAppFragment.startActivity(intent);
    }

    public void onViewCreated(View view, Bundle bundle) {
        this.mRecyclerView = (DragSelectRecyclerView) view.findViewById(R.id.select_app_recycler_view);
        this.mProgressBar = (ProgressBar) view.findViewById(R.id.select_app_progress_bar);
        this.mInstallButton = (Button) view.findViewById(R.id.select_app_install_btn);
        this.mSelectFromExternal = view.findViewById(R.id.select_app_from_external);
        this.mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, 1));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), 1);
        dividerItemDecoration.setDrawable(new ColorDrawable(520093696));
        this.mRecyclerView.addItemDecoration(dividerItemDecoration);
        this.mAdapter = new CloneAppListAdapter(getActivity(), getSelectFrom());
        this.mRecyclerView.setAdapter((DragSelectRecyclerViewAdapter<?>) this.mAdapter);
        this.mAdapter.setOnItemClickListener(new ItemEventListener() {
            public void onItemClick(AppInfo appInfo, int i) {
                int selectedCount = ListAppFragment.this.mAdapter.getSelectedCount();
                if (ListAppFragment.this.mAdapter.isIndexSelected(i) || selectedCount < 9) {
                    ListAppFragment.this.mAdapter.toggleSelected(i);
                } else {
                    Toast.makeText(ListAppFragment.this.getContext(), R.string.install_too_much_once_time, 0).show();
                }
            }

            public boolean isSelectable(int i) {
                return ListAppFragment.this.mAdapter.isIndexSelected(i) || ListAppFragment.this.mAdapter.getSelectedCount() < 9;
            }
        });
        this.mAdapter.setSelectionListener(new SelectionListener() {
            public final void onDragSelectionChanged(int i) {
                ListAppFragment.lambda$onViewCreated$63(ListAppFragment.this, i);
            }
        });
        this.mInstallButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ListAppFragment.lambda$onViewCreated$65(ListAppFragment.this, view);
            }
        });
        this.mSelectFromExternal.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ListAppFragment.lambda$onViewCreated$66(ListAppFragment.this, view);
            }
        });
        new ListAppPresenterImpl(getActivity(), this, getSelectFrom()).start();
    }

    public static /* synthetic */ void lambda$onViewCreated$63(ListAppFragment listAppFragment, int i) {
        listAppFragment.mInstallButton.setEnabled(i > 0);
        listAppFragment.mInstallButton.setText(String.format(Locale.ENGLISH, XApp.getApp().getResources().getString(R.string.install_d), new Object[]{Integer.valueOf(i)}));
    }

    public static /* synthetic */ void lambda$onViewCreated$65(ListAppFragment listAppFragment, View view) {
        Integer[] selectedIndices = listAppFragment.mAdapter.getSelectedIndices();
        ArrayList arrayList = new ArrayList(selectedIndices.length);
        for (Integer intValue : selectedIndices) {
            AppInfo item = listAppFragment.mAdapter.getItem(intValue.intValue());
            arrayList.add(new AppInfoLite(item.packageName, item.path, item.fastOpen, item.disableMultiVersion));
        }
        if (arrayList.size() > 0) {
            listAppFragment.chooseInstallWay(new Runnable(arrayList) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ListAppFragment.lambda$null$64(ListAppFragment.this, this.f$1);
                }
            }, ((AppInfoLite) arrayList.get(0)).path);
        }
    }

    public static /* synthetic */ void lambda$null$64(ListAppFragment listAppFragment, ArrayList arrayList) {
        FragmentActivity activity = listAppFragment.getActivity();
        if (activity != null) {
            Installd.startInstallerActivity(activity, arrayList);
            activity.setResult(-1);
        }
    }

    public static /* synthetic */ void lambda$onViewCreated$66(ListAppFragment listAppFragment, View view) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("application/vnd.android.package-archive");
        intent.addCategory("android.intent.category.OPENABLE");
        try {
            listAppFragment.startActivityForResult(intent, 1);
        } catch (Throwable unused) {
            Toast.makeText(listAppFragment.getActivity(), "Error", 0).show();
        }
    }

    public void startLoading() {
        this.mProgressBar.setVisibility(0);
        this.mRecyclerView.setVisibility(8);
    }

    public void loadFinish(List<AppInfo> list) {
        this.mAdapter.setList(list);
        this.mRecyclerView.setDragSelectActive(false, 0);
        this.mAdapter.setSelected(0, false);
        this.mProgressBar.setVisibility(8);
        this.mRecyclerView.setVisibility(0);
    }

    public void setPresenter(ListAppPresenter listAppPresenter) {
        this.mPresenter = listAppPresenter;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1 && i2 == -1 && getActivity() != null) {
            String path = getPath(getActivity(), intent.getData());
            if (path != null) {
                chooseInstallWay(new Runnable(path) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ListAppFragment.lambda$onActivityResult$67(ListAppFragment.this, this.f$1);
                    }
                }, path);
            }
        }
    }

    public static /* synthetic */ void lambda$onActivityResult$67(ListAppFragment listAppFragment, String str) {
        Installd.handleRequestFromFile(listAppFragment.getActivity(), str);
        listAppFragment.getActivity().setResult(-1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0038, code lost:
        if (r8 != null) goto L_0x004a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0048, code lost:
        if (r8 != null) goto L_0x004a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x004a, code lost:
        r8.close();
     */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0043  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getPath(android.content.Context r8, android.net.Uri r9) {
        /*
            r0 = 0
            if (r9 != 0) goto L_0x0004
            return r0
        L_0x0004:
            java.lang.String r1 = "content"
            java.lang.String r2 = r9.getScheme()
            boolean r1 = r1.equalsIgnoreCase(r2)
            if (r1 == 0) goto L_0x004e
            java.lang.String r1 = "_data"
            java.lang.String[] r4 = new java.lang.String[]{r1}
            android.content.ContentResolver r2 = r8.getContentResolver()     // Catch:{ Exception -> 0x0047, all -> 0x003f }
            r5 = 0
            r6 = 0
            r7 = 0
            r3 = r9
            android.database.Cursor r8 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0047, all -> 0x003f }
            java.lang.String r9 = "_data"
            int r9 = r8.getColumnIndexOrThrow(r9)     // Catch:{ Exception -> 0x003d, all -> 0x003b }
            boolean r1 = r8.moveToFirst()     // Catch:{ Exception -> 0x003d, all -> 0x003b }
            if (r1 == 0) goto L_0x0038
            java.lang.String r9 = r8.getString(r9)     // Catch:{ Exception -> 0x003d, all -> 0x003b }
            if (r8 == 0) goto L_0x0037
            r8.close()
        L_0x0037:
            return r9
        L_0x0038:
            if (r8 == 0) goto L_0x005f
            goto L_0x004a
        L_0x003b:
            r9 = move-exception
            goto L_0x0041
        L_0x003d:
            goto L_0x0048
        L_0x003f:
            r9 = move-exception
            r8 = r0
        L_0x0041:
            if (r8 == 0) goto L_0x0046
            r8.close()
        L_0x0046:
            throw r9
        L_0x0047:
            r8 = r0
        L_0x0048:
            if (r8 == 0) goto L_0x005f
        L_0x004a:
            r8.close()
            goto L_0x005f
        L_0x004e:
            java.lang.String r8 = "file"
            java.lang.String r1 = r9.getScheme()
            boolean r8 = r8.equalsIgnoreCase(r1)
            if (r8 == 0) goto L_0x005f
            java.lang.String r8 = r9.getPath()
            return r8
        L_0x005f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: p013io.virtualapp.home.ListAppFragment.getPath(android.content.Context, android.net.Uri):java.lang.String");
    }
}
