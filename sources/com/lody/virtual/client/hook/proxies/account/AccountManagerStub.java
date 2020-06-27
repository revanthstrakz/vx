package com.lody.virtual.client.hook.proxies.account;

import android.accounts.Account;
import android.accounts.IAccountManagerResponse;
import android.os.Bundle;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.client.ipc.VAccountManager;
import java.lang.reflect.Method;
import mirror.android.accounts.IAccountManager.Stub;

public class AccountManagerStub extends BinderInvocationProxy {
    /* access modifiers changed from: private */
    public static VAccountManager Mgr = VAccountManager.get();

    private static class accountAuthenticated extends MethodProxy {
        public String getMethodName() {
            return "accountAuthenticated";
        }

        private accountAuthenticated() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return Boolean.valueOf(AccountManagerStub.Mgr.accountAuthenticated(objArr[0]));
        }
    }

    private static class addAccount extends MethodProxy {
        public String getMethodName() {
            return "addAccount";
        }

        private addAccount() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.addAccount((IAccountManagerResponse) objArr[0], (String) objArr[1], (String) objArr[2], (String[]) objArr[3], objArr[4].booleanValue(), (Bundle) objArr[5]);
            return Integer.valueOf(0);
        }
    }

    private static class addAccountAsUser extends MethodProxy {
        public String getMethodName() {
            return "addAccountAsUser";
        }

        private addAccountAsUser() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.addAccount((IAccountManagerResponse) objArr[0], (String) objArr[1], (String) objArr[2], (String[]) objArr[3], objArr[4].booleanValue(), (Bundle) objArr[5]);
            return Integer.valueOf(0);
        }
    }

    private static class addAccountExplicitly extends MethodProxy {
        public String getMethodName() {
            return "addAccountExplicitly";
        }

        private addAccountExplicitly() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return Boolean.valueOf(AccountManagerStub.Mgr.addAccountExplicitly(objArr[0], objArr[1], objArr[2]));
        }
    }

    private static class addSharedAccountAsUser extends MethodProxy {
        public String getMethodName() {
            return "addSharedAccountAsUser";
        }

        private addSharedAccountAsUser() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            Account account = objArr[0];
            objArr[1].intValue();
            return method.invoke(obj, objArr);
        }
    }

    private static class clearPassword extends MethodProxy {
        public String getMethodName() {
            return "clearPassword";
        }

        private clearPassword() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.clearPassword(objArr[0]);
            return Integer.valueOf(0);
        }
    }

    private static class confirmCredentialsAsUser extends MethodProxy {
        public String getMethodName() {
            return "confirmCredentialsAsUser";
        }

        private confirmCredentialsAsUser() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.confirmCredentials(objArr[0], objArr[1], objArr[2], objArr[3].booleanValue());
            return Integer.valueOf(0);
        }
    }

    private static class copyAccountToUser extends MethodProxy {
        public String getMethodName() {
            return "copyAccountToUser";
        }

        private copyAccountToUser() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            IAccountManagerResponse iAccountManagerResponse = objArr[0];
            Account account = objArr[1];
            objArr[2].intValue();
            objArr[3].intValue();
            method.invoke(obj, objArr);
            return Integer.valueOf(0);
        }
    }

    private static class editProperties extends MethodProxy {
        public String getMethodName() {
            return "editProperties";
        }

        private editProperties() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.editProperties(objArr[0], objArr[1], objArr[2].booleanValue());
            return Integer.valueOf(0);
        }
    }

    private static class getAccounts extends MethodProxy {
        public String getMethodName() {
            return "getAccounts";
        }

        private getAccounts() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return AccountManagerStub.Mgr.getAccounts(objArr[0]);
        }
    }

    private static class getAccountsAsUser extends MethodProxy {
        public String getMethodName() {
            return "getAccountsAsUser";
        }

        private getAccountsAsUser() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return AccountManagerStub.Mgr.getAccounts(objArr[0]);
        }
    }

    private static class getAccountsByFeatures extends MethodProxy {
        public String getMethodName() {
            return "getAccountsByFeatures";
        }

        private getAccountsByFeatures() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.getAccountsByFeatures(objArr[0], objArr[1], objArr[2]);
            return Integer.valueOf(0);
        }
    }

    private static class getAccountsByTypeForPackage extends MethodProxy {
        public String getMethodName() {
            return "getAccountsByTypeForPackage";
        }

        private getAccountsByTypeForPackage() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String str = objArr[0];
            String str2 = objArr[1];
            return AccountManagerStub.Mgr.getAccounts(str);
        }
    }

    private static class getAccountsForPackage extends MethodProxy {
        public String getMethodName() {
            return "getAccountsForPackage";
        }

        private getAccountsForPackage() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            String str = objArr[0];
            return AccountManagerStub.Mgr.getAccounts(null);
        }
    }

    private static class getAuthToken extends MethodProxy {
        public String getMethodName() {
            return "getAuthToken";
        }

        private getAuthToken() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.getAuthToken((IAccountManagerResponse) objArr[0], (Account) objArr[1], (String) objArr[2], objArr[3].booleanValue(), objArr[4].booleanValue(), (Bundle) objArr[5]);
            return Integer.valueOf(0);
        }
    }

    private static class getAuthTokenLabel extends MethodProxy {
        public String getMethodName() {
            return "getAuthTokenLabel";
        }

        private getAuthTokenLabel() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.getAuthTokenLabel(objArr[0], objArr[1], objArr[2]);
            return Integer.valueOf(0);
        }
    }

    private static class getAuthenticatorTypes extends MethodProxy {
        public String getMethodName() {
            return "getAuthenticatorTypes";
        }

        private getAuthenticatorTypes() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return AccountManagerStub.Mgr.getAuthenticatorTypes();
        }
    }

    private static class getPassword extends MethodProxy {
        public String getMethodName() {
            return "getPassword";
        }

        private getPassword() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return AccountManagerStub.Mgr.getPassword(objArr[0]);
        }
    }

    private static class getPreviousName extends MethodProxy {
        public String getMethodName() {
            return "getPreviousName";
        }

        private getPreviousName() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return AccountManagerStub.Mgr.getPreviousName(objArr[0]);
        }
    }

    private static class getSharedAccountsAsUser extends MethodProxy {
        public String getMethodName() {
            return "getSharedAccountsAsUser";
        }

        private getSharedAccountsAsUser() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            objArr[0].intValue();
            return method.invoke(obj, objArr);
        }
    }

    private static class getUserData extends MethodProxy {
        public String getMethodName() {
            return "getUserData";
        }

        private getUserData() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return AccountManagerStub.Mgr.getUserData(objArr[0], objArr[1]);
        }
    }

    private static class hasFeatures extends MethodProxy {
        public String getMethodName() {
            return "hasFeatures";
        }

        private hasFeatures() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.hasFeatures(objArr[0], objArr[1], objArr[2]);
            return Integer.valueOf(0);
        }
    }

    private static class invalidateAuthToken extends MethodProxy {
        public String getMethodName() {
            return "invalidateAuthToken";
        }

        private invalidateAuthToken() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.invalidateAuthToken(objArr[0], objArr[1]);
            return Integer.valueOf(0);
        }
    }

    private static class peekAuthToken extends MethodProxy {
        public String getMethodName() {
            return "peekAuthToken";
        }

        private peekAuthToken() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return AccountManagerStub.Mgr.peekAuthToken(objArr[0], objArr[1]);
        }
    }

    private static class removeAccount extends MethodProxy {
        public String getMethodName() {
            return "removeAccount";
        }

        private removeAccount() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.removeAccount(objArr[0], objArr[1], objArr[2].booleanValue());
            return Integer.valueOf(0);
        }
    }

    private static class removeAccountAsUser extends MethodProxy {
        public String getMethodName() {
            return "removeAccountAsUser";
        }

        private removeAccountAsUser() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.removeAccount(objArr[0], objArr[1], objArr[2].booleanValue());
            return Integer.valueOf(0);
        }
    }

    private static class removeAccountExplicitly extends MethodProxy {
        public String getMethodName() {
            return "removeAccountExplicitly";
        }

        private removeAccountExplicitly() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            return Boolean.valueOf(AccountManagerStub.Mgr.removeAccountExplicitly(objArr[0]));
        }
    }

    private static class removeSharedAccountAsUser extends MethodProxy {
        public String getMethodName() {
            return "removeSharedAccountAsUser";
        }

        private removeSharedAccountAsUser() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            Account account = objArr[0];
            objArr[1].intValue();
            return method.invoke(obj, objArr);
        }
    }

    private static class renameAccount extends MethodProxy {
        public String getMethodName() {
            return "renameAccount";
        }

        private renameAccount() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.renameAccount(objArr[0], objArr[1], objArr[2]);
            return Integer.valueOf(0);
        }
    }

    private static class renameSharedAccountAsUser extends MethodProxy {
        public String getMethodName() {
            return "renameSharedAccountAsUser";
        }

        private renameSharedAccountAsUser() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            Account account = objArr[0];
            String str = objArr[1];
            objArr[2].intValue();
            return method.invoke(obj, objArr);
        }
    }

    private static class setAuthToken extends MethodProxy {
        public String getMethodName() {
            return "setAuthToken";
        }

        private setAuthToken() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.setAuthToken(objArr[0], objArr[1], objArr[2]);
            return Integer.valueOf(0);
        }
    }

    private static class setPassword extends MethodProxy {
        public String getMethodName() {
            return "setPassword";
        }

        private setPassword() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.setPassword(objArr[0], objArr[1]);
            return Integer.valueOf(0);
        }
    }

    private static class setUserData extends MethodProxy {
        public String getMethodName() {
            return "setUserData";
        }

        private setUserData() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.setUserData(objArr[0], objArr[1], objArr[2]);
            return Integer.valueOf(0);
        }
    }

    private static class updateAppPermission extends MethodProxy {
        public String getMethodName() {
            return "updateAppPermission";
        }

        private updateAppPermission() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            Account account = objArr[0];
            String str = objArr[1];
            objArr[2].intValue();
            objArr[3].booleanValue();
            method.invoke(obj, objArr);
            return Integer.valueOf(0);
        }
    }

    private static class updateCredentials extends MethodProxy {
        public String getMethodName() {
            return "updateCredentials";
        }

        private updateCredentials() {
        }

        public Object call(Object obj, Method method, Object... objArr) throws Throwable {
            AccountManagerStub.Mgr.updateCredentials((IAccountManagerResponse) objArr[0], (Account) objArr[1], (String) objArr[2], objArr[3].booleanValue(), (Bundle) objArr[4]);
            return Integer.valueOf(0);
        }
    }

    public AccountManagerStub() {
        super(Stub.asInterface, ServiceManagerNative.ACCOUNT);
    }

    /* access modifiers changed from: protected */
    public void onBindMethods() {
        super.onBindMethods();
        addMethodProxy((MethodProxy) new getPassword());
        addMethodProxy((MethodProxy) new getUserData());
        addMethodProxy((MethodProxy) new getAuthenticatorTypes());
        addMethodProxy((MethodProxy) new getAccounts());
        addMethodProxy((MethodProxy) new getAccountsForPackage());
        addMethodProxy((MethodProxy) new getAccountsByTypeForPackage());
        addMethodProxy((MethodProxy) new getAccountsAsUser());
        addMethodProxy((MethodProxy) new hasFeatures());
        addMethodProxy((MethodProxy) new getAccountsByFeatures());
        addMethodProxy((MethodProxy) new addAccountExplicitly());
        addMethodProxy((MethodProxy) new removeAccount());
        addMethodProxy((MethodProxy) new removeAccountAsUser());
        addMethodProxy((MethodProxy) new removeAccountExplicitly());
        addMethodProxy((MethodProxy) new copyAccountToUser());
        addMethodProxy((MethodProxy) new invalidateAuthToken());
        addMethodProxy((MethodProxy) new peekAuthToken());
        addMethodProxy((MethodProxy) new setAuthToken());
        addMethodProxy((MethodProxy) new setPassword());
        addMethodProxy((MethodProxy) new clearPassword());
        addMethodProxy((MethodProxy) new setUserData());
        addMethodProxy((MethodProxy) new updateAppPermission());
        addMethodProxy((MethodProxy) new getAuthToken());
        addMethodProxy((MethodProxy) new addAccount());
        addMethodProxy((MethodProxy) new addAccountAsUser());
        addMethodProxy((MethodProxy) new updateCredentials());
        addMethodProxy((MethodProxy) new editProperties());
        addMethodProxy((MethodProxy) new confirmCredentialsAsUser());
        addMethodProxy((MethodProxy) new accountAuthenticated());
        addMethodProxy((MethodProxy) new getAuthTokenLabel());
        addMethodProxy((MethodProxy) new addSharedAccountAsUser());
        addMethodProxy((MethodProxy) new getSharedAccountsAsUser());
        addMethodProxy((MethodProxy) new removeSharedAccountAsUser());
        addMethodProxy((MethodProxy) new renameAccount());
        addMethodProxy((MethodProxy) new getPreviousName());
        addMethodProxy((MethodProxy) new renameSharedAccountAsUser());
    }
}
