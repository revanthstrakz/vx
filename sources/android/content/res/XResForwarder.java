package android.content.res;

public class XResForwarder {

    /* renamed from: id */
    private final int f0id;
    private final Resources res;

    public XResForwarder(Resources resources, int i) {
        this.res = resources;
        this.f0id = i;
    }

    public Resources getResources() {
        return this.res;
    }

    public int getId() {
        return this.f0id;
    }
}
