package p011de.robv.android.xposed.callbacks;

import android.os.Bundle;
import java.io.Serializable;
import p011de.robv.android.xposed.XposedBridge;
import p011de.robv.android.xposed.XposedBridge.CopyOnWriteSortedSet;

/* renamed from: de.robv.android.xposed.callbacks.XCallback */
public abstract class XCallback implements Comparable<XCallback> {
    public static final int PRIORITY_DEFAULT = 50;
    public static final int PRIORITY_HIGHEST = 10000;
    public static final int PRIORITY_LOWEST = -10000;
    public final int priority;

    /* renamed from: de.robv.android.xposed.callbacks.XCallback$Param */
    public static abstract class Param {
        public final Object[] callbacks;
        private Bundle extra;

        /* renamed from: de.robv.android.xposed.callbacks.XCallback$Param$SerializeWrapper */
        private static class SerializeWrapper implements Serializable {
            private static final long serialVersionUID = 1;
            /* access modifiers changed from: private */
            public final Object object;

            public SerializeWrapper(Object obj) {
                this.object = obj;
            }
        }

        @Deprecated
        protected Param() {
            this.callbacks = null;
        }

        protected Param(CopyOnWriteSortedSet<? extends XCallback> copyOnWriteSortedSet) {
            this.callbacks = copyOnWriteSortedSet.getSnapshot();
        }

        public synchronized Bundle getExtra() {
            if (this.extra == null) {
                this.extra = new Bundle();
            }
            return this.extra;
        }

        public Object getObjectExtra(String str) {
            Serializable serializable = getExtra().getSerializable(str);
            if (serializable instanceof SerializeWrapper) {
                return ((SerializeWrapper) serializable).object;
            }
            return null;
        }

        public void setObjectExtra(String str, Object obj) {
            getExtra().putSerializable(str, new SerializeWrapper(obj));
        }
    }

    /* access modifiers changed from: protected */
    public void call(Param param) throws Throwable {
    }

    @Deprecated
    public XCallback() {
        this.priority = 50;
    }

    public XCallback(int i) {
        this.priority = i;
    }

    public static void callAll(Param param) {
        if (param.callbacks != null) {
            for (int i = 0; i < param.callbacks.length; i++) {
                try {
                    ((XCallback) param.callbacks[i]).call(param);
                } catch (Throwable th) {
                    XposedBridge.log(th);
                }
            }
            return;
        }
        throw new IllegalStateException("This object was not created for use with callAll");
    }

    public int compareTo(XCallback xCallback) {
        if (this == xCallback) {
            return 0;
        }
        if (xCallback.priority != this.priority) {
            return xCallback.priority - this.priority;
        }
        return System.identityHashCode(this) < System.identityHashCode(xCallback) ? -1 : 1;
    }
}
