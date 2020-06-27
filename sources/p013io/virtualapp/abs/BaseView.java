package p013io.virtualapp.abs;

import android.app.Activity;
import android.content.Context;

/* renamed from: io.virtualapp.abs.BaseView */
public interface BaseView<T> {
    Activity getActivity();

    Context getContext();

    void setPresenter(T t);
}
