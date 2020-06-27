package com.android.launcher3;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.view.View.AccessibilityDelegate;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.util.SystemUiController;

public abstract class BaseActivity extends Activity {
    protected DeviceProfile mDeviceProfile;
    protected SystemUiController mSystemUiController;
    protected UserEventDispatcher mUserEventDispatcher;

    public AccessibilityDelegate getAccessibilityDelegate() {
        return null;
    }

    public DeviceProfile getDeviceProfile() {
        return this.mDeviceProfile;
    }

    public final UserEventDispatcher getUserEventDispatcher() {
        if (this.mUserEventDispatcher == null) {
            this.mUserEventDispatcher = UserEventDispatcher.newInstance(this, this.mDeviceProfile.isLandscape, isInMultiWindowModeCompat());
        }
        return this.mUserEventDispatcher;
    }

    public boolean isInMultiWindowModeCompat() {
        return Utilities.ATLEAST_NOUGAT && isInMultiWindowMode();
    }

    public static BaseActivity fromContext(Context context) {
        if (context instanceof BaseActivity) {
            return (BaseActivity) context;
        }
        return (BaseActivity) ((ContextWrapper) context).getBaseContext();
    }

    public SystemUiController getSystemUiController() {
        if (this.mSystemUiController == null) {
            this.mSystemUiController = new SystemUiController(getWindow());
        }
        return this.mSystemUiController;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }
}
