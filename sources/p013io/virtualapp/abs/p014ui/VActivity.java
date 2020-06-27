package p013io.virtualapp.abs.p014ui;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.p001v4.app.Fragment;
import android.support.p004v7.app.AppCompatActivity;
import org.jdeferred.android.AndroidDeferredManager;

/* renamed from: io.virtualapp.abs.ui.VActivity */
public class VActivity extends AppCompatActivity {
    public Activity getActivity() {
        return this;
    }

    public Context getContext() {
        return this;
    }

    /* access modifiers changed from: protected */
    public AndroidDeferredManager defer() {
        return VUiKit.defer();
    }

    public Fragment findFragmentById(@IdRes int i) {
        return getSupportFragmentManager().findFragmentById(i);
    }

    public void replaceFragment(@IdRes int i, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(i, fragment).commit();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
    }
}
