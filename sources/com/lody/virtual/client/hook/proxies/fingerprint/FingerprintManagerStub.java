package com.lody.virtual.client.hook.proxies.fingerprint;

import android.annotation.TargetApi;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastPkgMethodProxy;
import mirror.android.hardware.fingerprint.IFingerprintService.Stub;

@TargetApi(23)
public class FingerprintManagerStub extends BinderInvocationProxy {
    public FingerprintManagerStub() {
        super(Stub.asInterface, "fingerprint");
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("isHardwareDetected"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("hasEnrolledFingerprints"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("authenticate"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("cancelAuthentication"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getEnrolledFingerprints"));
        addMethodProxy((MethodProxy) new ReplaceLastPkgMethodProxy("getAuthenticatorId"));
    }
}
