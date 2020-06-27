package com.google.android.apps.nexuslauncher.search;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.support.p004v7.widget.helper.ItemTouchHelper;
import com.android.launcher3.allapps.search.AllAppsSearchBarController.Callbacks;
import com.android.launcher3.allapps.search.SearchAlgorithm;

public class SearchThread implements SearchAlgorithm, Callback {
    private static HandlerThread handlerThread;
    private final Context mContext;
    private final Handler mHandler;
    private boolean mInterruptActiveRequests;
    private final Handler mUiHandler = new Handler(this);

    public SearchThread(Context context) {
        this.mContext = context;
        if (handlerThread == null) {
            handlerThread = new HandlerThread("search-thread", -2);
            handlerThread.start();
        }
        this.mHandler = new Handler(handlerThread.getLooper(), this);
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0063  */
    /* renamed from: dj */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void m26dj(com.google.android.apps.nexuslauncher.search.SearchResult r8) {
        /*
            r7 = this;
            android.net.Uri$Builder r0 = new android.net.Uri$Builder
            r0.<init>()
            java.lang.String r1 = "content"
            android.net.Uri$Builder r0 = r0.scheme(r1)
            java.lang.String r1 = com.google.android.apps.nexuslauncher.search.AppSearchProvider.AUTHORITY
            android.net.Uri$Builder r0 = r0.authority(r1)
            java.lang.String r1 = r8.mQuery
            android.net.Uri$Builder r0 = r0.appendPath(r1)
            android.net.Uri r2 = r0.build()
            r0 = 0
            android.content.Context r1 = r7.mContext     // Catch:{ all -> 0x005f }
            android.content.ContentResolver r1 = r1.getContentResolver()     // Catch:{ all -> 0x005f }
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            android.database.Cursor r1 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x005f }
            if (r1 == 0) goto L_0x004e
            java.lang.String r0 = "suggest_intent_data"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x004c }
        L_0x0032:
            boolean r2 = r1.moveToNext()     // Catch:{ all -> 0x004c }
            if (r2 == 0) goto L_0x004e
            java.util.ArrayList<com.android.launcher3.util.ComponentKey> r2 = r8.mApps     // Catch:{ all -> 0x004c }
            java.lang.String r3 = r1.getString(r0)     // Catch:{ all -> 0x004c }
            android.net.Uri r3 = android.net.Uri.parse(r3)     // Catch:{ all -> 0x004c }
            android.content.Context r4 = r7.mContext     // Catch:{ all -> 0x004c }
            com.android.launcher3.util.ComponentKey r3 = com.google.android.apps.nexuslauncher.search.AppSearchProvider.uriToComponent(r3, r4)     // Catch:{ all -> 0x004c }
            r2.add(r3)     // Catch:{ all -> 0x004c }
            goto L_0x0032
        L_0x004c:
            r8 = move-exception
            goto L_0x0061
        L_0x004e:
            if (r1 == 0) goto L_0x0053
            r1.close()
        L_0x0053:
            android.os.Handler r0 = r7.mUiHandler
            r1 = 200(0xc8, float:2.8E-43)
            android.os.Message r8 = android.os.Message.obtain(r0, r1, r8)
            r8.sendToTarget()
            return
        L_0x005f:
            r8 = move-exception
            r1 = r0
        L_0x0061:
            if (r1 == 0) goto L_0x0066
            r1.close()
        L_0x0066:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.apps.nexuslauncher.search.SearchThread.m26dj(com.google.android.apps.nexuslauncher.search.SearchResult):void");
    }

    public void cancel(boolean z) {
        this.mInterruptActiveRequests = z;
        this.mHandler.removeMessages(100);
        if (z) {
            this.mUiHandler.removeMessages(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        }
    }

    public void doSearch(String str, Callbacks callbacks) {
        this.mHandler.removeMessages(100);
        Message.obtain(this.mHandler, 100, new SearchResult(str, callbacks)).sendToTarget();
    }

    public boolean handleMessage(Message message) {
        int i = message.what;
        if (i == 100) {
            m26dj((SearchResult) message.obj);
        } else if (i != 200) {
            return false;
        } else {
            if (!this.mInterruptActiveRequests) {
                SearchResult searchResult = (SearchResult) message.obj;
                searchResult.mCallbacks.onSearchResult(searchResult.mQuery, searchResult.mApps);
            }
        }
        return true;
    }
}
