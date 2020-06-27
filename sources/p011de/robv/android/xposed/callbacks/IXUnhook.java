package p011de.robv.android.xposed.callbacks;

/* renamed from: de.robv.android.xposed.callbacks.IXUnhook */
public interface IXUnhook<T> {
    T getCallback();

    void unhook();
}
