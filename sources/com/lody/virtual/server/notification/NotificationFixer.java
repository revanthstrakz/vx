package com.lody.virtual.server.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Notification.Builder;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build.VERSION;
import android.widget.RemoteViews;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.utils.OSUtils;
import com.lody.virtual.helper.utils.Reflect;
import com.lody.virtual.helper.utils.VLog;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.ArrayList;
import mirror.com.android.internal.R_Hide.C1314id;

class NotificationFixer {
    private static final String TAG = NotificationCompat.TAG;
    private NotificationCompat mNotificationCompat;

    private static class BitmapReflectionAction {
        Bitmap bitmap;
        String methodName;
        int viewId;

        BitmapReflectionAction(int i, String str, Bitmap bitmap2) {
            this.viewId = i;
            this.methodName = str;
            this.bitmap = bitmap2;
        }
    }

    NotificationFixer(NotificationCompat notificationCompat) {
        this.mNotificationCompat = notificationCompat;
    }

    private static void fixNotificationIcon(Context context, Notification notification, Builder builder) {
        if (VERSION.SDK_INT < 23) {
            builder.setSmallIcon(notification.icon);
            builder.setLargeIcon(notification.largeIcon);
            return;
        }
        Icon smallIcon = notification.getSmallIcon();
        if (smallIcon != null) {
            Bitmap drawableToBitMap = drawableToBitMap(smallIcon.loadDrawable(context));
            if (drawableToBitMap != null) {
                builder.setSmallIcon(Icon.createWithBitmap(drawableToBitMap));
            }
        }
        Icon largeIcon = notification.getLargeIcon();
        if (largeIcon != null) {
            Bitmap drawableToBitMap2 = drawableToBitMap(largeIcon.loadDrawable(context));
            if (drawableToBitMap2 != null) {
                builder.setLargeIcon(Icon.createWithBitmap(drawableToBitMap2));
            }
        }
    }

    private static Bitmap drawableToBitMap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != -1 ? Config.ARGB_8888 : Config.RGB_565);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return createBitmap;
    }

    /* access modifiers changed from: 0000 */
    @TargetApi(23)
    public void fixIcon(Icon icon, Context context, boolean z) {
        if (icon != null && ((Integer) mirror.android.graphics.drawable.Icon.mType.get(icon)).intValue() == 2) {
            if (z) {
                mirror.android.graphics.drawable.Icon.mObj1.set(icon, context.getResources());
                mirror.android.graphics.drawable.Icon.mString1.set(icon, context.getPackageName());
            } else {
                mirror.android.graphics.drawable.Icon.mObj1.set(icon, drawableToBitMap(icon.loadDrawable(context)));
                mirror.android.graphics.drawable.Icon.mString1.set(icon, null);
                mirror.android.graphics.drawable.Icon.mType.set(icon, Integer.valueOf(1));
            }
        }
    }

    /* access modifiers changed from: 0000 */
    @TargetApi(21)
    public void fixNotificationRemoteViews(Context context, Notification notification) {
        Builder builder;
        try {
            builder = (Builder) Reflect.m79on(Builder.class).create(context, notification).get();
        } catch (Exception unused) {
            builder = null;
        }
        if (builder != null) {
            Notification build = builder.build();
            if (notification.tickerView == null) {
                notification.tickerView = build.tickerView;
            }
            if (notification.contentView == null) {
                notification.contentView = build.contentView;
            }
            if (notification.bigContentView == null) {
                notification.bigContentView = build.bigContentView;
            }
            if (notification.headsUpContentView == null) {
                notification.headsUpContentView = build.headsUpContentView;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean fixRemoteViewActions(Context context, boolean z, RemoteViews remoteViews) {
        RemoteViews remoteViews2 = remoteViews;
        boolean z2 = false;
        if (remoteViews2 != null) {
            int i = C1314id.icon.get();
            ArrayList<BitmapReflectionAction> arrayList = new ArrayList<>();
            ArrayList arrayList2 = (ArrayList) Reflect.m80on((Object) remoteViews).get("mActions");
            if (arrayList2 != null) {
                boolean z3 = false;
                for (int size = arrayList2.size() - 1; size >= 0; size--) {
                    Object obj = arrayList2.get(size);
                    if (obj != null) {
                        if (obj.getClass().getSimpleName().endsWith("TextViewDrawableAction")) {
                            arrayList2.remove(obj);
                        } else if (ReflectionActionCompat.isInstance(obj) || obj.getClass().getSimpleName().endsWith("ReflectionAction")) {
                            int intValue = ((Integer) Reflect.m80on(obj).get("viewId")).intValue();
                            String str = (String) Reflect.m80on(obj).get("methodName");
                            int intValue2 = ((Integer) Reflect.m80on(obj).get(CommonProperties.TYPE)).intValue();
                            Object obj2 = Reflect.m80on(obj).get("value");
                            if (!z3) {
                                z3 = intValue == i;
                                if (z3) {
                                    if (intValue2 == 4 && ((Integer) obj2).intValue() == 0) {
                                        z3 = false;
                                    }
                                    if (z3) {
                                        String str2 = TAG;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("find icon ");
                                        sb.append(str);
                                        sb.append(" type=");
                                        sb.append(intValue2);
                                        sb.append(", value=");
                                        sb.append(obj2);
                                        VLog.m90v(str2, sb.toString(), new Object[0]);
                                    }
                                }
                            }
                            if (str.equals("setImageResource")) {
                                arrayList.add(new BitmapReflectionAction(intValue, "setImageBitmap", drawableToBitMap(context.getResources().getDrawable(((Integer) obj2).intValue()))));
                                arrayList2.remove(obj);
                            } else if (str.equals("setText") && intValue2 == 4) {
                                Reflect.m80on(obj).set(CommonProperties.TYPE, Integer.valueOf(9));
                                Reflect.m80on(obj).set("value", context.getResources().getString(((Integer) obj2).intValue()));
                            } else if (str.equals("setLabelFor")) {
                                arrayList2.remove(obj);
                            } else if (str.equals("setBackgroundResource")) {
                                arrayList2.remove(obj);
                            } else if (str.equals("setImageURI")) {
                                if (!((Uri) obj2).getScheme().startsWith("http")) {
                                    arrayList2.remove(obj);
                                }
                            } else if (VERSION.SDK_INT >= 23 && (obj2 instanceof Icon)) {
                                fixIcon((Icon) obj2, context, z);
                            }
                        }
                    }
                    Context context2 = context;
                    boolean z4 = z;
                }
                for (BitmapReflectionAction bitmapReflectionAction : arrayList) {
                    remoteViews2.setBitmap(bitmapReflectionAction.viewId, bitmapReflectionAction.methodName, bitmapReflectionAction.bitmap);
                }
                z2 = z3;
            }
            if (VERSION.SDK_INT < 21) {
                mirror.android.widget.RemoteViews.mPackage.set(remoteViews2, VirtualCore.get().getHostPkg());
            }
        }
        return z2;
    }

    /* access modifiers changed from: 0000 */
    public void fixIconImage(Resources resources, RemoteViews remoteViews, boolean z, Notification notification) {
        if (remoteViews != null && notification.icon != 0 && this.mNotificationCompat.isSystemLayout(remoteViews)) {
            try {
                int i = C1314id.icon.get();
                if (!z && notification.largeIcon == null) {
                    Drawable drawable = resources.getDrawable(notification.icon);
                    drawable.setLevel(notification.iconLevel);
                    Bitmap drawableToBitMap = drawableToBitMap(drawable);
                    remoteViews.setImageViewBitmap(i, drawableToBitMap);
                    if (OSUtils.getInstance().isEmui() && notification.largeIcon == null) {
                        notification.largeIcon = drawableToBitMap;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
