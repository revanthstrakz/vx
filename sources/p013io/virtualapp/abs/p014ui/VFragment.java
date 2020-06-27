package p013io.virtualapp.abs.p014ui;

import android.support.p001v4.app.Fragment;
import android.support.p001v4.app.FragmentActivity;
import org.jdeferred.android.AndroidDeferredManager;
import p013io.virtualapp.abs.BasePresenter;

/* renamed from: io.virtualapp.abs.ui.VFragment */
public class VFragment<T extends BasePresenter> extends Fragment {
    protected T mPresenter;

    public T getPresenter() {
        return this.mPresenter;
    }

    public void setPresenter(T t) {
        this.mPresenter = t;
    }

    /* access modifiers changed from: protected */
    public AndroidDeferredManager defer() {
        return VUiKit.defer();
    }

    public void finishActivity() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    public void destroy() {
        finishActivity();
    }
}
