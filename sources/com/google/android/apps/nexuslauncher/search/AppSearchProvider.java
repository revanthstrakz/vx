package com.google.android.apps.nexuslauncher.search;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentProvider.PipeDataWriter;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.AppInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherModel.ModelUpdateTask;
import com.android.launcher3.allapps.AppInfoComparator;
import com.android.launcher3.allapps.search.DefaultAppSearchAlgorithm;
import com.android.launcher3.allapps.search.DefaultAppSearchAlgorithm.StringMatcher;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.model.BgDataModel;
import com.android.launcher3.model.LoaderResults;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.LooperExecutor;
import com.google.android.apps.nexuslauncher.utils.BuildUtil;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AppSearchProvider extends ContentProvider {
    public static String AUTHORITY;

    /* renamed from: eK */
    private static final String[] f89eK = {"_id", "suggest_text_1", "suggest_icon_1", "suggest_intent_action", "suggest_intent_data"};
    /* access modifiers changed from: private */
    public LauncherAppState mApp;
    private LooperExecutor mLooper;
    private final PipeDataWriter<Future> mPipeDataWriter = new PipeDataWriter<Future>() {
        /* JADX WARNING: Removed duplicated region for block: B:10:0x0021 A[SYNTHETIC, Splitter:B:10:0x0021] */
        /* JADX WARNING: Removed duplicated region for block: B:15:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void writeDataToPipe(@android.support.annotation.NonNull android.os.ParcelFileDescriptor r1, @android.support.annotation.NonNull android.net.Uri r2, @android.support.annotation.NonNull java.lang.String r3, @android.support.annotation.Nullable android.os.Bundle r4, @android.support.annotation.Nullable java.util.concurrent.Future r5) {
            /*
                r0 = this;
                r2 = 0
                android.os.ParcelFileDescriptor$AutoCloseOutputStream r3 = new android.os.ParcelFileDescriptor$AutoCloseOutputStream     // Catch:{ Throwable -> 0x0016 }
                r3.<init>(r1)     // Catch:{ Throwable -> 0x0016 }
                java.lang.Object r1 = r5.get()     // Catch:{ Throwable -> 0x0014 }
                android.graphics.Bitmap r1 = (android.graphics.Bitmap) r1     // Catch:{ Throwable -> 0x0014 }
                android.graphics.Bitmap$CompressFormat r2 = android.graphics.Bitmap.CompressFormat.PNG     // Catch:{ Throwable -> 0x0014 }
                r4 = 100
                r1.compress(r2, r4, r3)     // Catch:{ Throwable -> 0x0014 }
                goto L_0x001f
            L_0x0014:
                r1 = move-exception
                goto L_0x0018
            L_0x0016:
                r1 = move-exception
                r3 = r2
            L_0x0018:
                java.lang.String r2 = "AppSearchProvider"
                java.lang.String r4 = "fail to write to pipe"
                android.util.Log.w(r2, r4, r1)
            L_0x001f:
                if (r3 == 0) goto L_0x0024
                r3.close()     // Catch:{ Throwable -> 0x0024 }
            L_0x0024:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.apps.nexuslauncher.search.AppSearchProvider.C09361.writeDataToPipe(android.os.ParcelFileDescriptor, android.net.Uri, java.lang.String, android.os.Bundle, java.util.concurrent.Future):void");
        }
    };

    /* renamed from: com.google.android.apps.nexuslauncher.search.AppSearchProvider$f */
    class C0939f implements Callable<List<AppInfo>>, ModelUpdateTask {
        /* access modifiers changed from: private */

        /* renamed from: eN */
        public final FutureTask<List<AppInfo>> f90eN = new FutureTask<>(this);
        private AllAppsList mAllAppsList;
        private LauncherAppState mApp;
        private BgDataModel mBgDataModel;
        private LauncherModel mModel;
        private final String mQuery;

        C0939f(String str) {
            this.mQuery = str.toLowerCase();
        }

        public List<AppInfo> call() {
            if (!this.mModel.isModelLoaded()) {
                Log.d("AppSearchProvider", "Workspace not loaded, loading now");
                LauncherModel launcherModel = this.mModel;
                LoaderResults loaderResults = new LoaderResults(this.mApp, this.mBgDataModel, this.mAllAppsList, 0, null);
                launcherModel.startLoaderForResults(loaderResults);
            }
            if (!this.mModel.isModelLoaded()) {
                Log.d("AppSearchProvider", "Loading workspace failed");
                return Collections.emptyList();
            }
            ArrayList arrayList = new ArrayList();
            List<AppInfo> apps = DefaultAppSearchAlgorithm.getApps(this.mApp.getContext(), this.mAllAppsList.data);
            StringMatcher instance = StringMatcher.getInstance();
            for (AppInfo appInfo : apps) {
                if (DefaultAppSearchAlgorithm.matches(appInfo, this.mQuery, instance)) {
                    arrayList.add(appInfo);
                    if (appInfo.usingLowResIcon) {
                        this.mApp.getIconCache().getTitleAndIcon(appInfo, false);
                    }
                }
            }
            Collections.sort(arrayList, new AppInfoComparator(this.mApp.getContext()));
            return arrayList;
        }

        public void init(LauncherAppState launcherAppState, LauncherModel launcherModel, BgDataModel bgDataModel, AllAppsList allAppsList, Executor executor) {
            this.mApp = launcherAppState;
            this.mModel = launcherModel;
            this.mBgDataModel = bgDataModel;
            this.mAllAppsList = allAppsList;
        }

        public void run() {
            this.f90eN.run();
        }
    }

    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/vnd.android.search.suggest";
    }

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(BuildUtil.getApplicationId());
        sb.append(".appssearch");
        AUTHORITY = sb.toString();
    }

    public static ComponentKey uriToComponent(Uri uri, Context context) {
        return new ComponentKey(ComponentName.unflattenFromString(uri.getQueryParameter("component")), UserManagerCompat.getInstance(context).getUserForSerialNumber(Long.parseLong(uri.getQueryParameter(ServiceManagerNative.USER))));
    }

    public static Uri buildUri(AppInfo appInfo, UserManagerCompat userManagerCompat) {
        return new Builder().scheme("content").authority(AUTHORITY).appendQueryParameter("component", appInfo.componentName.flattenToShortString()).appendQueryParameter(ServiceManagerNative.USER, Long.toString(userManagerCompat.getSerialNumberForUser(appInfo.user))).build();
    }

    private Cursor listToCursor(List<AppInfo> list) {
        MatrixCursor matrixCursor = new MatrixCursor(f89eK, list.size());
        UserManagerCompat instance = UserManagerCompat.getInstance(getContext());
        int i = 0;
        for (AppInfo appInfo : list) {
            String uri = buildUri(appInfo, instance).toString();
            int i2 = i + 1;
            matrixCursor.newRow().add(Integer.valueOf(i)).add(appInfo.title.toString()).add(uri).add("com.google.android.apps.nexuslauncher.search.APP_LAUNCH").add(uri);
            i = i2;
        }
        return matrixCursor;
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.d("AppSearchProvider", "Content provider accessed on main thread");
            return null;
        } else if (!"loadIcon".equals(str)) {
            return super.call(str, str2, bundle);
        } else {
            try {
                final ComponentKey uriToComponent = uriToComponent(Uri.parse(str2), getContext());
                C09372 r5 = new Callable<Bitmap>() {
                    public Bitmap call() {
                        AppItemInfoWithIcon appItemInfoWithIcon = new AppItemInfoWithIcon(uriToComponent);
                        AppSearchProvider.this.mApp.getIconCache().getTitleAndIcon(appItemInfoWithIcon, false);
                        return appItemInfoWithIcon.iconBitmap;
                    }
                };
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable("suggest_icon_1", (Parcelable) this.mLooper.submit(r5).get());
                return bundle2;
            } catch (Exception e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Unable to load icon ");
                sb.append(e);
                Log.e("AppSearchProvider", sb.toString());
                return null;
            }
        }
    }

    public int delete(Uri uri, String str, String[] strArr) {
        throw new UnsupportedOperationException();
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException();
    }

    public boolean onCreate() {
        this.mLooper = new LooperExecutor(LauncherModel.getWorkerLooper());
        this.mApp = LauncherAppState.getInstance(getContext());
        return true;
    }

    public ParcelFileDescriptor openFile(Uri uri, String str) throws FileNotFoundException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.e("AppSearchProvider", "Content provider accessed on main thread");
            return null;
        }
        try {
            final ComponentKey uriToComponent = uriToComponent(uri, getContext());
            return openPipeHelper(uri, "image/png", null, this.mLooper.submit(new Callable<Bitmap>() {
                public Bitmap call() {
                    AppItemInfoWithIcon appItemInfoWithIcon = new AppItemInfoWithIcon(uriToComponent);
                    AppSearchProvider.this.mApp.getIconCache().getTitleAndIcon(appItemInfoWithIcon, false);
                    return appItemInfoWithIcon.iconBitmap;
                }
            }), this.mPipeDataWriter);
        } catch (Exception e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    public Cursor query(@NonNull Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        List list;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.e("AppSearchProvider", "Content provider accessed on main thread");
            return new MatrixCursor(f89eK, 0);
        }
        try {
            C0939f fVar = new C0939f(uri.getLastPathSegment());
            this.mApp.getModel().enqueueModelUpdateTask(fVar);
            list = (List) fVar.f90eN.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Log.d("AppSearchProvider", "Error searching apps", e);
            list = new ArrayList();
        }
        return listToCursor(list);
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException();
    }
}
