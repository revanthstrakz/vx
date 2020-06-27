package com.lody.virtual.p007os;

import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.LocalProxyUtils;
import com.lody.virtual.client.ipc.ServiceManagerNative;
import com.lody.virtual.server.IUserManager;
import com.lody.virtual.server.IUserManager.Stub;
import java.util.List;

/* renamed from: com.lody.virtual.os.VUserManager */
public class VUserManager {
    public static final String DISALLOW_CONFIG_BLUETOOTH = "no_config_bluetooth";
    public static final String DISALLOW_CONFIG_CREDENTIALS = "no_config_credentials";
    public static final String DISALLOW_CONFIG_WIFI = "no_config_wifi";
    public static final String DISALLOW_INSTALL_APPS = "no_install_apps";
    public static final String DISALLOW_INSTALL_UNKNOWN_SOURCES = "no_install_unknown_sources";
    public static final String DISALLOW_MODIFY_ACCOUNTS = "no_modify_accounts";
    public static final String DISALLOW_REMOVE_USER = "no_remove_user";
    public static final String DISALLOW_SHARE_LOCATION = "no_share_location";
    public static final String DISALLOW_UNINSTALL_APPS = "no_uninstall_apps";
    public static final String DISALLOW_USB_FILE_TRANSFER = "no_usb_file_transfer";
    private static String TAG = "VUserManager";
    private static VUserManager sInstance;
    private IUserManager mService;

    public static int getMaxSupportedUsers() {
        return Integer.MAX_VALUE;
    }

    public boolean isUserAGoat() {
        return false;
    }

    public static synchronized VUserManager get() {
        VUserManager vUserManager;
        synchronized (VUserManager.class) {
            if (sInstance == null) {
                sInstance = new VUserManager(Stub.asInterface(ServiceManagerNative.getService(ServiceManagerNative.USER)));
            }
            vUserManager = sInstance;
        }
        return vUserManager;
    }

    public VUserManager(IUserManager iUserManager) {
        this.mService = iUserManager;
    }

    private IUserManager getService() {
        if (this.mService == null || (!VirtualCore.get().isVAppProcess() && !this.mService.asBinder().pingBinder())) {
            synchronized (this) {
                this.mService = (IUserManager) LocalProxyUtils.genProxy(IUserManager.class, getStubInterface());
            }
        }
        return this.mService;
    }

    private Object getStubInterface() {
        return Stub.asInterface(ServiceManagerNative.getService(ServiceManagerNative.USER));
    }

    public static boolean supportsMultipleUsers() {
        return getMaxSupportedUsers() > 1;
    }

    public int getUserHandle() {
        return VUserHandle.myUserId();
    }

    public String getUserName() {
        try {
            return getService().getUserInfo(getUserHandle()).name;
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get user name", e);
            return "";
        }
    }

    public VUserInfo getUserInfo(int i) {
        try {
            return getService().getUserInfo(i);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get user info", e);
            return null;
        }
    }

    public long getSerialNumberForUser(VUserHandle vUserHandle) {
        return (long) getUserSerialNumber(vUserHandle.getIdentifier());
    }

    public VUserHandle getUserForSerialNumber(long j) {
        int userHandle = getUserHandle((int) j);
        if (userHandle >= 0) {
            return new VUserHandle(userHandle);
        }
        return null;
    }

    public VUserInfo createUser(String str, int i) {
        try {
            return getService().createUser(str, i);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not create a user", e);
            return null;
        }
    }

    public int getUserCount() {
        List users = getUsers();
        if (users != null) {
            return users.size();
        }
        return 1;
    }

    public List<VUserInfo> getUsers() {
        try {
            return getService().getUsers(false);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get user list", e);
            return null;
        }
    }

    public List<VUserInfo> getUsers(boolean z) {
        try {
            return getService().getUsers(z);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get user list", e);
            return null;
        }
    }

    public boolean removeUser(int i) {
        try {
            return getService().removeUser(i);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not remove user ", e);
            return false;
        }
    }

    public void setUserName(int i, String str) {
        try {
            getService().setUserName(i, str);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not set the user name ", e);
        }
    }

    public void setUserIcon(int i, Bitmap bitmap) {
        try {
            getService().setUserIcon(i, bitmap);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not set the user icon ", e);
        }
    }

    public Bitmap getUserIcon(int i) {
        try {
            return getService().getUserIcon(i);
        } catch (RemoteException e) {
            Log.w(TAG, "Could not get the user icon ", e);
            return null;
        }
    }

    public void setGuestEnabled(boolean z) {
        try {
            getService().setGuestEnabled(z);
        } catch (RemoteException unused) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Could not change guest account availability to ");
            sb.append(z);
            Log.w(str, sb.toString());
        }
    }

    public boolean isGuestEnabled() {
        try {
            return getService().isGuestEnabled();
        } catch (RemoteException unused) {
            Log.w(TAG, "Could not retrieve guest enabled state");
            return false;
        }
    }

    public void wipeUser(int i) {
        try {
            getService().wipeUser(i);
        } catch (RemoteException unused) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Could not wipe user ");
            sb.append(i);
            Log.w(str, sb.toString());
        }
    }

    public int getUserSerialNumber(int i) {
        try {
            return getService().getUserSerialNumber(i);
        } catch (RemoteException unused) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Could not get serial number for user ");
            sb.append(i);
            Log.w(str, sb.toString());
            return -1;
        }
    }

    public int getUserHandle(int i) {
        try {
            return getService().getUserHandle(i);
        } catch (RemoteException unused) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Could not get VUserHandle for user ");
            sb.append(i);
            Log.w(str, sb.toString());
            return -1;
        }
    }
}
