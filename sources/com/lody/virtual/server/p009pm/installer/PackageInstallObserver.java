package com.lody.virtual.server.p009pm.installer;

import android.content.Intent;
import android.content.p000pm.IPackageInstallObserver2;
import android.content.p000pm.IPackageInstallObserver2.Stub;
import android.os.Bundle;

/* renamed from: com.lody.virtual.server.pm.installer.PackageInstallObserver */
public class PackageInstallObserver {
    private final Stub mBinder = new Stub() {
        public void onUserActionRequired(Intent intent) {
            PackageInstallObserver.this.onUserActionRequired(intent);
        }

        public void onPackageInstalled(String str, int i, String str2, Bundle bundle) {
            PackageInstallObserver.this.onPackageInstalled(str, i, str2, bundle);
        }
    };

    public void onPackageInstalled(String str, int i, String str2, Bundle bundle) {
    }

    public void onUserActionRequired(Intent intent) {
    }

    public IPackageInstallObserver2 getBinder() {
        return this.mBinder;
    }
}
