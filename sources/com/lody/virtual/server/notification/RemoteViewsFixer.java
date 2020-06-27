package com.lody.virtual.server.notification;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.lody.virtual.C0966R;
import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.helper.utils.VLog;
import java.util.HashMap;

class RemoteViewsFixer {
    private static final String TAG = NotificationCompat.TAG;
    private boolean init = false;
    private final HashMap<String, Bitmap> mImages = new HashMap<>();
    private NotificationCompat mNotificationCompat;
    private final WidthCompat mWidthCompat = new WidthCompat();
    private int notification_max_height;
    private int notification_mid_height;
    private int notification_min_height;
    private int notification_padding;
    private int notification_panel_width;
    private int notification_side_padding;

    RemoteViewsFixer(NotificationCompat notificationCompat) {
        this.mNotificationCompat = notificationCompat;
    }

    /* access modifiers changed from: 0000 */
    public View toView(Context context, RemoteViews remoteViews, boolean z, boolean z2) {
        try {
            return createView(context, remoteViews, z, z2);
        } catch (Throwable th) {
            VLog.m91w(TAG, "toView 2", th);
            return null;
        }
    }

    /* access modifiers changed from: 0000 */
    public Bitmap createBitmap(View view) {
        if (view == null) {
            return null;
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0052  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00aa  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.view.View apply(android.content.Context r10, android.widget.RemoteViews r11) {
        /*
            r9 = this;
            r0 = 2
            r1 = 0
            r2 = 1
            r3 = 0
            android.view.LayoutInflater r10 = android.view.LayoutInflater.from(r10)     // Catch:{ Exception -> 0x0043 }
            int r4 = r11.getLayoutId()     // Catch:{ Exception -> 0x0043 }
            android.view.View r10 = r10.inflate(r4, r1, r3)     // Catch:{ Exception -> 0x0043 }
            com.lody.virtual.helper.utils.Reflect r4 = com.lody.virtual.helper.utils.Reflect.m80on(r10)     // Catch:{ Exception -> 0x0034 }
            java.lang.String r5 = "setTagInternal"
            java.lang.Object[] r6 = new java.lang.Object[r0]     // Catch:{ Exception -> 0x0034 }
            java.lang.String r7 = "com.android.internal.R$id"
            com.lody.virtual.helper.utils.Reflect r7 = com.lody.virtual.helper.utils.Reflect.m81on(r7)     // Catch:{ Exception -> 0x0034 }
            java.lang.String r8 = "widget_frame"
            java.lang.Object r7 = r7.get(r8)     // Catch:{ Exception -> 0x0034 }
            r6[r3] = r7     // Catch:{ Exception -> 0x0034 }
            int r7 = r11.getLayoutId()     // Catch:{ Exception -> 0x0034 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x0034 }
            r6[r2] = r7     // Catch:{ Exception -> 0x0034 }
            r4.call(r5, r6)     // Catch:{ Exception -> 0x0034 }
            goto L_0x0050
        L_0x0034:
            r4 = move-exception
            java.lang.String r5 = TAG     // Catch:{ Exception -> 0x0041 }
            java.lang.String r6 = "setTagInternal"
            java.lang.Object[] r7 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x0041 }
            r7[r3] = r4     // Catch:{ Exception -> 0x0041 }
            com.lody.virtual.helper.utils.VLog.m91w(r5, r6, r7)     // Catch:{ Exception -> 0x0041 }
            goto L_0x0050
        L_0x0041:
            r4 = move-exception
            goto L_0x0045
        L_0x0043:
            r4 = move-exception
            r10 = r1
        L_0x0045:
            java.lang.String r5 = TAG
            java.lang.String r6 = "inflate"
            java.lang.Object[] r7 = new java.lang.Object[r2]
            r7[r3] = r4
            com.lody.virtual.helper.utils.VLog.m91w(r5, r6, r7)
        L_0x0050:
            if (r10 == 0) goto L_0x00aa
            com.lody.virtual.helper.utils.Reflect r11 = com.lody.virtual.helper.utils.Reflect.m80on(r11)
            java.lang.String r4 = "mActions"
            java.lang.Object r11 = r11.get(r4)
            java.util.ArrayList r11 = (java.util.ArrayList) r11
            if (r11 == 0) goto L_0x00b3
            java.lang.String r4 = TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "apply actions:"
            r5.append(r6)
            int r6 = r11.size()
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.lang.Object[] r6 = new java.lang.Object[r3]
            com.lody.virtual.helper.utils.VLog.m86d(r4, r5, r6)
            java.util.Iterator r11 = r11.iterator()
        L_0x0080:
            boolean r4 = r11.hasNext()
            if (r4 == 0) goto L_0x00b3
            java.lang.Object r4 = r11.next()
            com.lody.virtual.helper.utils.Reflect r4 = com.lody.virtual.helper.utils.Reflect.m80on(r4)     // Catch:{ Exception -> 0x009d }
            java.lang.String r5 = "apply"
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x009d }
            r6[r3] = r10     // Catch:{ Exception -> 0x009d }
            r6[r2] = r1     // Catch:{ Exception -> 0x009d }
            r6[r0] = r1     // Catch:{ Exception -> 0x009d }
            r4.call(r5, r6)     // Catch:{ Exception -> 0x009d }
            goto L_0x0080
        L_0x009d:
            r4 = move-exception
            java.lang.String r5 = TAG
            java.lang.String r6 = "apply action"
            java.lang.Object[] r7 = new java.lang.Object[r2]
            r7[r3] = r4
            com.lody.virtual.helper.utils.VLog.m91w(r5, r6, r7)
            goto L_0x0080
        L_0x00aa:
            java.lang.String r11 = TAG
            java.lang.String r0 = "create views"
            java.lang.Object[] r1 = new java.lang.Object[r3]
            com.lody.virtual.helper.utils.VLog.m87e(r11, r0, r1)
        L_0x00b3:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.notification.RemoteViewsFixer.apply(android.content.Context, android.widget.RemoteViews):android.view.View");
    }

    private View createView(Context context, RemoteViews remoteViews, boolean z, boolean z2) {
        if (remoteViews == null) {
            return null;
        }
        Context hostContext = this.mNotificationCompat.getHostContext();
        init(hostContext);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("createView:big=");
        sb.append(z);
        sb.append(",system=");
        sb.append(z2);
        VLog.m90v(str, sb.toString(), new Object[0]);
        int i = z ? this.notification_max_height : this.notification_min_height;
        int notificationWidth = this.mWidthCompat.getNotificationWidth(hostContext, this.notification_panel_width, i, this.notification_side_padding);
        String str2 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("createView:getNotificationWidth=");
        sb2.append(notificationWidth);
        VLog.m90v(str2, sb2.toString(), new Object[0]);
        FrameLayout frameLayout = new FrameLayout(context);
        VLog.m90v(TAG, "createView:apply", new Object[0]);
        View apply = apply(context, remoteViews);
        LayoutParams layoutParams = new LayoutParams(-1, -1);
        layoutParams.gravity = 16;
        frameLayout.addView(apply, layoutParams);
        if (apply instanceof ViewGroup) {
            VLog.m90v(TAG, "createView:fixTextView", new Object[0]);
            fixTextView((ViewGroup) apply);
        }
        int i2 = (!z2 && z) ? Integer.MIN_VALUE : 1073741824;
        VLog.m90v(TAG, "createView:layout", new Object[0]);
        frameLayout.layout(0, 0, notificationWidth, i);
        frameLayout.measure(MeasureSpec.makeMeasureSpec(notificationWidth, 1073741824), MeasureSpec.makeMeasureSpec(i, i2));
        frameLayout.layout(0, 0, notificationWidth, frameLayout.getMeasuredHeight());
        String str3 = TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("notification:systemId=");
        sb3.append(z2);
        sb3.append(",max=%d/%d, szie=%d/%d");
        VLog.m90v(str3, sb3.toString(), Integer.valueOf(notificationWidth), Integer.valueOf(i), Integer.valueOf(frameLayout.getMeasuredWidth()), Integer.valueOf(frameLayout.getMeasuredHeight()));
        return frameLayout;
    }

    private void fixTextView(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof TextView) {
                TextView textView = (TextView) childAt;
                if (isSingleLine(textView)) {
                    textView.setSingleLine(false);
                    textView.setMaxLines(1);
                }
            } else if (childAt instanceof ViewGroup) {
                fixTextView((ViewGroup) childAt);
            }
        }
    }

    private boolean isSingleLine(TextView textView) {
        try {
            return ((Boolean) Reflect.m80on((Object) textView).get("mSingleLine")).booleanValue();
        } catch (Exception unused) {
            return (textView.getInputType() & 131072) != 0;
        }
    }

    public RemoteViews makeRemoteViews(String str, Context context, RemoteViews remoteViews, boolean z, boolean z2) {
        int i;
        Bitmap bitmap;
        if (remoteViews == null) {
            return null;
        }
        PendIntentCompat pendIntentCompat = new PendIntentCompat(remoteViews);
        if (!z2 || pendIntentCompat.findPendIntents() <= 0) {
            i = C0966R.layout.custom_notification_lite;
        } else {
            i = C0966R.layout.custom_notification;
        }
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("createviews id = ");
        sb.append(i);
        VLog.m90v(str2, sb.toString(), new Object[0]);
        RemoteViews remoteViews2 = new RemoteViews(this.mNotificationCompat.getHostContext().getPackageName(), i);
        VLog.m90v(TAG, "remoteViews to view", new Object[0]);
        View view = toView(context, remoteViews, z, false);
        VLog.m90v(TAG, "start createBitmap", new Object[0]);
        Bitmap createBitmap = createBitmap(view);
        if (createBitmap == null) {
            String str3 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("bmp is null,contentView=");
            sb2.append(remoteViews);
            VLog.m87e(str3, sb2.toString(), new Object[0]);
        } else {
            String str4 = TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("bmp w=");
            sb3.append(createBitmap.getWidth());
            sb3.append(",h=");
            sb3.append(createBitmap.getHeight());
            VLog.m90v(str4, sb3.toString(), new Object[0]);
        }
        synchronized (this.mImages) {
            bitmap = (Bitmap) this.mImages.get(str);
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            String str5 = TAG;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("recycle ");
            sb4.append(str);
            VLog.m90v(str5, sb4.toString(), new Object[0]);
            bitmap.recycle();
        }
        remoteViews2.setImageViewBitmap(C0966R.C0967id.im_main, createBitmap);
        String str6 = TAG;
        StringBuilder sb5 = new StringBuilder();
        sb5.append("createview ");
        sb5.append(str);
        VLog.m90v(str6, sb5.toString(), new Object[0]);
        synchronized (this.mImages) {
            this.mImages.put(str, createBitmap);
        }
        if (z2 && i == C0966R.layout.custom_notification) {
            VLog.m90v(TAG, "start setPendIntent", new Object[0]);
            try {
                pendIntentCompat.setPendIntent(remoteViews2, toView(this.mNotificationCompat.getHostContext(), remoteViews2, z, false), view);
            } catch (Exception e) {
                VLog.m87e(TAG, "setPendIntent error", e);
            }
        }
        return remoteViews2;
    }

    private void init(Context context) {
        if (!this.init) {
            this.init = true;
            if (this.notification_panel_width == 0) {
                Context context2 = null;
                try {
                    context2 = context.createPackageContext("com.android.systemui", 2);
                } catch (NameNotFoundException unused) {
                }
                if (VERSION.SDK_INT <= 19) {
                    this.notification_side_padding = 0;
                } else {
                    this.notification_side_padding = getDimem(context, context2, "notification_side_padding", C0966R.dimen.notification_side_padding);
                }
                this.notification_panel_width = getDimem(context, context2, "notification_panel_width", C0966R.dimen.notification_panel_width);
                if (this.notification_panel_width <= 0) {
                    this.notification_panel_width = context.getResources().getDisplayMetrics().widthPixels;
                }
                this.notification_min_height = getDimem(context, context2, "notification_min_height", C0966R.dimen.notification_min_height);
                this.notification_max_height = getDimem(context, context2, "notification_max_height", C0966R.dimen.notification_max_height);
                this.notification_mid_height = getDimem(context, context2, "notification_mid_height", C0966R.dimen.notification_mid_height);
                this.notification_padding = getDimem(context, context2, "notification_padding", C0966R.dimen.notification_padding);
            }
        }
    }

    private int getDimem(Context context, Context context2, String str, int i) {
        int i2;
        if (context2 != null) {
            int identifier = context2.getResources().getIdentifier(str, "dimen", "com.android.systemui");
            if (identifier != 0) {
                try {
                    return Math.round(context2.getResources().getDimension(identifier));
                } catch (Exception unused) {
                }
            }
        }
        if (i == 0) {
            i2 = 0;
        } else {
            i2 = Math.round(context.getResources().getDimension(i));
        }
        return i2;
    }
}
