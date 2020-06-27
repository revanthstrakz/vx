package com.lody.virtual.server.notification;

import android.app.PendingIntent;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.helper.utils.VLog;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class PendIntentCompat {
    private Map<Integer, PendingIntent> clickIntents;
    private RemoteViews mRemoteViews;

    class RectInfo {
        int index;
        PendingIntent mPendingIntent;
        Rect rect;

        public RectInfo(Rect rect2, PendingIntent pendingIntent, int i) {
            this.rect = rect2;
            this.mPendingIntent = pendingIntent;
            this.index = i;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("RectInfo{rect=");
            sb.append(this.rect);
            sb.append('}');
            return sb.toString();
        }
    }

    PendIntentCompat(RemoteViews remoteViews) {
        this.mRemoteViews = remoteViews;
    }

    public int findPendIntents() {
        if (this.clickIntents == null) {
            this.clickIntents = getClickIntents(this.mRemoteViews);
        }
        return this.clickIntents.size();
    }

    public void setPendIntent(RemoteViews remoteViews, View view, View view2) {
        if (findPendIntents() > 0) {
            ArrayList arrayList = new ArrayList();
            VLog.m90v(NotificationCompat.TAG, "start find intent", new Object[0]);
            int i = 0;
            for (Entry entry : this.clickIntents.entrySet()) {
                View findViewById = view2.findViewById(((Integer) entry.getKey()).intValue());
                if (findViewById != null) {
                    arrayList.add(new RectInfo(getRect(findViewById), (PendingIntent) entry.getValue(), i));
                    i++;
                }
            }
            String str = NotificationCompat.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("find:");
            sb.append(arrayList);
            VLog.m90v(str, sb.toString(), new Object[0]);
            if (view instanceof ViewGroup) {
                setIntentByViewGroup(remoteViews, (ViewGroup) view, arrayList);
            }
        }
    }

    private Rect getRect(View view) {
        Rect rect = new Rect();
        rect.top = view.getTop();
        rect.left = view.getLeft();
        rect.right = view.getRight();
        rect.bottom = view.getBottom();
        ViewParent parent = view.getParent();
        if (parent != null && (parent instanceof ViewGroup)) {
            Rect rect2 = getRect((ViewGroup) parent);
            rect.top += rect2.top;
            rect.left += rect2.left;
            rect.right += rect2.left;
            rect.bottom += rect2.top;
        }
        return rect;
    }

    private void setIntentByViewGroup(RemoteViews remoteViews, ViewGroup viewGroup, List<RectInfo> list) {
        int childCount = viewGroup.getChildCount();
        viewGroup.getHitRect(new Rect());
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                setIntentByViewGroup(remoteViews, (ViewGroup) childAt, list);
            } else if ((childAt instanceof TextView) || (childAt instanceof ImageView)) {
                RectInfo findIntent = findIntent(getRect(childAt), list);
                if (findIntent != null) {
                    remoteViews.setOnClickPendingIntent(childAt.getId(), findIntent.mPendingIntent);
                }
            }
        }
    }

    private RectInfo findIntent(Rect rect, List<RectInfo> list) {
        int i = 0;
        RectInfo rectInfo = null;
        for (RectInfo rectInfo2 : list) {
            int overlapArea = getOverlapArea(rect, rectInfo2.rect);
            if (overlapArea > i) {
                if (overlapArea == 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("find two:");
                    sb.append(rectInfo2.rect);
                    Log.w("PendingIntentCompat", sb.toString());
                }
                rectInfo = rectInfo2;
                i = overlapArea;
            }
        }
        return rectInfo;
    }

    private int getOverlapArea(Rect rect, Rect rect2) {
        Rect rect3 = new Rect();
        rect3.left = Math.max(rect.left, rect2.left);
        rect3.top = Math.max(rect.top, rect2.top);
        rect3.right = Math.min(rect.right, rect2.right);
        rect3.bottom = Math.min(rect.bottom, rect2.bottom);
        if (rect3.left >= rect3.right || rect3.top >= rect3.bottom) {
            return 0;
        }
        return (rect3.right - rect3.left) * (rect3.bottom - rect3.top);
    }

    private Map<Integer, PendingIntent> getClickIntents(RemoteViews remoteViews) {
        Object obj;
        String str;
        HashMap hashMap = new HashMap();
        if (remoteViews == null) {
            return hashMap;
        }
        try {
            obj = Reflect.m80on((Object) remoteViews).get("mActions");
        } catch (Exception e) {
            e.printStackTrace();
            obj = null;
        }
        if (obj != null && (obj instanceof Collection)) {
            for (Object next : (Collection) obj) {
                if (next != null) {
                    try {
                        str = (String) Reflect.m80on(next).call("getActionName").get();
                    } catch (Exception unused) {
                        str = next.getClass().getSimpleName();
                    }
                    if ("SetOnClickPendingIntent".equalsIgnoreCase(str)) {
                        int intValue = ((Integer) Reflect.m80on(next).get("viewId")).intValue();
                        hashMap.put(Integer.valueOf(intValue), (PendingIntent) Reflect.m80on(next).get("pendingIntent"));
                    }
                }
            }
        }
        return hashMap;
    }
}
