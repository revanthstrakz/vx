package p011de.robv.android.xposed.callbacks;

import android.content.res.XResources;
import android.content.res.XResources.ResourceNames;
import android.view.View;
import p011de.robv.android.xposed.XposedBridge.CopyOnWriteSortedSet;
import p011de.robv.android.xposed.callbacks.XCallback.Param;

/* renamed from: de.robv.android.xposed.callbacks.XC_LayoutInflated */
public abstract class XC_LayoutInflated extends XCallback {

    /* renamed from: de.robv.android.xposed.callbacks.XC_LayoutInflated$LayoutInflatedParam */
    public static final class LayoutInflatedParam extends Param {
        public XResources res;
        public ResourceNames resNames;
        public String variant;
        public View view;

        public LayoutInflatedParam(CopyOnWriteSortedSet<XC_LayoutInflated> copyOnWriteSortedSet) {
            super(copyOnWriteSortedSet);
        }
    }

    /* renamed from: de.robv.android.xposed.callbacks.XC_LayoutInflated$Unhook */
    public class Unhook implements IXUnhook<XC_LayoutInflated> {

        /* renamed from: id */
        private final int f201id;
        private final String resDir;

        public Unhook(String str, int i) {
            this.resDir = str;
            this.f201id = i;
        }

        public int getId() {
            return this.f201id;
        }

        public XC_LayoutInflated getCallback() {
            return XC_LayoutInflated.this;
        }

        public void unhook() {
            XResources.unhookLayout(this.resDir, this.f201id, XC_LayoutInflated.this);
        }
    }

    public abstract void handleLayoutInflated(LayoutInflatedParam layoutInflatedParam) throws Throwable;

    public XC_LayoutInflated() {
    }

    public XC_LayoutInflated(int i) {
        super(i);
    }

    /* access modifiers changed from: protected */
    public void call(Param param) throws Throwable {
        if (param instanceof LayoutInflatedParam) {
            handleLayoutInflated((LayoutInflatedParam) param);
        }
    }
}
