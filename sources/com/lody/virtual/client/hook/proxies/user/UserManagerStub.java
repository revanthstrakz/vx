package com.lody.virtual.client.hook.proxies.user;

import android.annotation.TargetApi;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.lody.virtual.client.hook.base.ResultStaticMethodProxy;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import java.util.Collections;
import mirror.android.content.p016pm.UserInfo;
import mirror.android.p017os.IUserManager.Stub;

@TargetApi(17)
public class UserManagerStub extends BinderInvocationProxy {
    public UserManagerStub() {
        super(Stub.asInterface, ServiceManagerNative.USER);
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("setApplicationRestrictions"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getApplicationRestrictions"));
        addMethodProxy((MethodProxy) new ReplaceCallingPkgMethodProxy("getApplicationRestrictionsForUser"));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getProfileParent", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getUserIcon", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getUserInfo", UserInfo.ctor.newInstance(Integer.valueOf(0), "Admin", Integer.valueOf(UserInfo.FLAG_PRIMARY.get()))));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getDefaultGuestRestrictions", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("setDefaultGuestRestrictions", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("removeRestrictions", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getUsers", Collections.EMPTY_LIST));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("createUser", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("createProfileForUser", null));
        addMethodProxy((MethodProxy) new ResultStaticMethodProxy("getProfiles", Collections.EMPTY_LIST));
    }
}
