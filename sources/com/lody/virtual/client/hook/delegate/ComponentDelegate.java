package com.lody.virtual.client.hook.delegate;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

public interface ComponentDelegate {
    public static final ComponentDelegate EMPTY = new ComponentDelegate() {
        public void afterActivityCreate(Activity activity) {
        }

        public void afterActivityDestroy(Activity activity) {
        }

        public void afterActivityPause(Activity activity) {
        }

        public void afterActivityResume(Activity activity) {
        }

        public void afterApplicationCreate(Application application) {
        }

        public void beforeActivityCreate(Activity activity) {
        }

        public void beforeActivityDestroy(Activity activity) {
        }

        public void beforeActivityPause(Activity activity) {
        }

        public void beforeActivityResume(Activity activity) {
        }

        public void beforeApplicationCreate(Application application) {
        }

        public void onSendBroadcast(Intent intent) {
        }
    };

    void afterActivityCreate(Activity activity);

    void afterActivityDestroy(Activity activity);

    void afterActivityPause(Activity activity);

    void afterActivityResume(Activity activity);

    void afterApplicationCreate(Application application);

    void beforeActivityCreate(Activity activity);

    void beforeActivityDestroy(Activity activity);

    void beforeActivityPause(Activity activity);

    void beforeActivityResume(Activity activity);

    void beforeApplicationCreate(Application application);

    void onSendBroadcast(Intent intent);
}
