package com.lody.virtual.server.p009pm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.util.SparseArray;
import com.lody.virtual.C0966R;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.Constants;
import com.lody.virtual.helper.utils.ArrayUtils;
import com.lody.virtual.helper.utils.AtomicFile;
import com.lody.virtual.helper.utils.FastXmlSerializer;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.p007os.VBinder;
import com.lody.virtual.p007os.VEnvironment;
import com.lody.virtual.p007os.VUserHandle;
import com.lody.virtual.p007os.VUserInfo;
import com.lody.virtual.p007os.VUserManager;
import com.lody.virtual.server.IUserManager.Stub;
import com.lody.virtual.server.p008am.VActivityManagerService;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

/* renamed from: com.lody.virtual.server.pm.VUserManagerService */
public class VUserManagerService extends Stub {
    private static final String ATTR_CREATION_TIME = "created";
    private static final String ATTR_FLAGS = "flags";
    private static final String ATTR_ICON_PATH = "icon";
    private static final String ATTR_ID = "id";
    private static final String ATTR_LAST_LOGGED_IN_TIME = "lastLoggedIn";
    private static final String ATTR_NEXT_SERIAL_NO = "nextSerialNumber";
    private static final String ATTR_PARTIAL = "partial";
    private static final String ATTR_SERIAL_NO = "serialNumber";
    private static final String ATTR_USER_VERSION = "version";
    private static final boolean DBG = false;
    private static final long EPOCH_PLUS_30_YEARS = 946080000000L;
    private static final String LOG_TAG = "VUserManagerService";
    private static final int MIN_USER_ID = 1;
    private static final String TAG_NAME = "name";
    private static final String TAG_USER = "user";
    private static final String TAG_USERS = "users";
    private static final String USER_INFO_DIR;
    private static final String USER_LIST_FILENAME = "userlist.xml";
    private static final String USER_PHOTO_FILENAME = "photo.png";
    private static final int USER_VERSION = 1;
    private static VUserManagerService sInstance;
    private final File mBaseUserPath;
    private final Context mContext;
    private boolean mGuestEnabled;
    /* access modifiers changed from: private */
    public final Object mInstallLock;
    private int mNextSerialNumber;
    private int mNextUserId;
    /* access modifiers changed from: private */
    public final Object mPackagesLock;
    private final VPackageManagerService mPm;
    private HashSet<Integer> mRemovingUserIds;
    private int[] mUserIds;
    private final File mUserListFile;
    private int mUserVersion;
    private SparseArray<VUserInfo> mUsers;
    private final File mUsersDir;

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
    }

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("system");
        sb.append(File.separator);
        sb.append(TAG_USERS);
        USER_INFO_DIR = sb.toString();
    }

    VUserManagerService(Context context, VPackageManagerService vPackageManagerService, Object obj, Object obj2) {
        this(context, vPackageManagerService, obj, obj2, VEnvironment.getDataDirectory(), new File(VEnvironment.getDataDirectory(), "user"));
    }

    private VUserManagerService(Context context, VPackageManagerService vPackageManagerService, Object obj, Object obj2, File file, File file2) {
        this.mUsers = new SparseArray<>();
        this.mRemovingUserIds = new HashSet<>();
        this.mNextUserId = 1;
        this.mUserVersion = 0;
        this.mContext = context;
        this.mPm = vPackageManagerService;
        this.mInstallLock = obj;
        this.mPackagesLock = obj2;
        synchronized (this.mInstallLock) {
            synchronized (this.mPackagesLock) {
                this.mUsersDir = new File(file, USER_INFO_DIR);
                this.mUsersDir.mkdirs();
                new File(this.mUsersDir, "0").mkdirs();
                this.mBaseUserPath = file2;
                this.mUserListFile = new File(this.mUsersDir, USER_LIST_FILENAME);
                readUserListLocked();
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < this.mUsers.size(); i++) {
                    VUserInfo vUserInfo = (VUserInfo) this.mUsers.valueAt(i);
                    if (vUserInfo.partial && i != 0) {
                        arrayList.add(vUserInfo);
                    }
                }
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    VUserInfo vUserInfo2 = (VUserInfo) arrayList.get(i2);
                    String str = LOG_TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Removing partially created user #");
                    sb.append(i2);
                    sb.append(" (name=");
                    sb.append(vUserInfo2.name);
                    sb.append(")");
                    VLog.m91w(str, sb.toString(), new Object[0]);
                    removeUserStateLocked(vUserInfo2.f180id);
                }
                sInstance = this;
            }
        }
    }

    public static VUserManagerService get() {
        VUserManagerService vUserManagerService;
        synchronized (VUserManagerService.class) {
            vUserManagerService = sInstance;
        }
        return vUserManagerService;
    }

    private static void checkManageUsersPermission(String str) {
        if (VBinder.getCallingUid() != VirtualCore.get().myUid()) {
            StringBuilder sb = new StringBuilder();
            sb.append("You need MANAGE_USERS permission to: ");
            sb.append(str);
            throw new SecurityException(sb.toString());
        }
    }

    public List<VUserInfo> getUsers(boolean z) {
        ArrayList arrayList;
        synchronized (this.mPackagesLock) {
            arrayList = new ArrayList(this.mUsers.size());
            for (int i = 0; i < this.mUsers.size(); i++) {
                VUserInfo vUserInfo = (VUserInfo) this.mUsers.valueAt(i);
                if (!vUserInfo.partial) {
                    if (!z || !this.mRemovingUserIds.contains(Integer.valueOf(vUserInfo.f180id))) {
                        arrayList.add(vUserInfo);
                    }
                }
            }
        }
        return arrayList;
    }

    public VUserInfo getUserInfo(int i) {
        VUserInfo userInfoLocked;
        synchronized (this.mPackagesLock) {
            userInfoLocked = getUserInfoLocked(i);
        }
        return userInfoLocked;
    }

    private VUserInfo getUserInfoLocked(int i) {
        VUserInfo vUserInfo = (VUserInfo) this.mUsers.get(i);
        if (vUserInfo == null || !vUserInfo.partial || this.mRemovingUserIds.contains(Integer.valueOf(i))) {
            return vUserInfo;
        }
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("getUserInfo: unknown user #");
        sb.append(i);
        VLog.m91w(str, sb.toString(), new Object[0]);
        return null;
    }

    public boolean exists(int i) {
        boolean contains;
        synchronized (this.mPackagesLock) {
            contains = ArrayUtils.contains(this.mUserIds, i);
        }
        return contains;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0029, code lost:
        if (r2 == false) goto L_0x002e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002b, code lost:
        sendUserInfoChangedBroadcast(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002e, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setUserName(int r5, java.lang.String r6) {
        /*
            r4 = this;
            java.lang.String r0 = "rename users"
            checkManageUsersPermission(r0)
            java.lang.Object r0 = r4.mPackagesLock
            monitor-enter(r0)
            android.util.SparseArray<com.lody.virtual.os.VUserInfo> r1 = r4.mUsers     // Catch:{ all -> 0x0049 }
            java.lang.Object r1 = r1.get(r5)     // Catch:{ all -> 0x0049 }
            com.lody.virtual.os.VUserInfo r1 = (com.lody.virtual.p007os.VUserInfo) r1     // Catch:{ all -> 0x0049 }
            r2 = 0
            if (r1 == 0) goto L_0x002f
            boolean r3 = r1.partial     // Catch:{ all -> 0x0049 }
            if (r3 == 0) goto L_0x0018
            goto L_0x002f
        L_0x0018:
            if (r6 == 0) goto L_0x0028
            java.lang.String r3 = r1.name     // Catch:{ all -> 0x0049 }
            boolean r3 = r6.equals(r3)     // Catch:{ all -> 0x0049 }
            if (r3 != 0) goto L_0x0028
            r1.name = r6     // Catch:{ all -> 0x0049 }
            r4.writeUserLocked(r1)     // Catch:{ all -> 0x0049 }
            r2 = 1
        L_0x0028:
            monitor-exit(r0)     // Catch:{ all -> 0x0049 }
            if (r2 == 0) goto L_0x002e
            r4.sendUserInfoChangedBroadcast(r5)
        L_0x002e:
            return
        L_0x002f:
            java.lang.String r6 = "VUserManagerService"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0049 }
            r1.<init>()     // Catch:{ all -> 0x0049 }
            java.lang.String r3 = "setUserName: unknown user #"
            r1.append(r3)     // Catch:{ all -> 0x0049 }
            r1.append(r5)     // Catch:{ all -> 0x0049 }
            java.lang.String r5 = r1.toString()     // Catch:{ all -> 0x0049 }
            java.lang.Object[] r1 = new java.lang.Object[r2]     // Catch:{ all -> 0x0049 }
            com.lody.virtual.helper.utils.VLog.m91w(r6, r5, r1)     // Catch:{ all -> 0x0049 }
            monitor-exit(r0)     // Catch:{ all -> 0x0049 }
            return
        L_0x0049:
            r5 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0049 }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.VUserManagerService.setUserName(int, java.lang.String):void");
    }

    public void setUserIcon(int i, Bitmap bitmap) {
        checkManageUsersPermission("update users");
        synchronized (this.mPackagesLock) {
            VUserInfo vUserInfo = (VUserInfo) this.mUsers.get(i);
            if (vUserInfo != null) {
                if (!vUserInfo.partial) {
                    writeBitmapLocked(vUserInfo, bitmap);
                    writeUserLocked(vUserInfo);
                    sendUserInfoChangedBroadcast(i);
                    return;
                }
            }
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("setUserIcon: unknown user #");
            sb.append(i);
            VLog.m91w(str, sb.toString(), new Object[0]);
        }
    }

    private void sendUserInfoChangedBroadcast(int i) {
        Intent intent = new Intent(Constants.ACTION_USER_INFO_CHANGED);
        intent.putExtra(Constants.EXTRA_USER_HANDLE, i);
        intent.addFlags(1073741824);
        VActivityManagerService.get().sendBroadcastAsUser(intent, new VUserHandle(i));
    }

    public Bitmap getUserIcon(int i) {
        synchronized (this.mPackagesLock) {
            VUserInfo vUserInfo = (VUserInfo) this.mUsers.get(i);
            if (vUserInfo != null) {
                if (!vUserInfo.partial) {
                    if (vUserInfo.iconPath == null) {
                        return null;
                    }
                    Bitmap decodeFile = BitmapFactory.decodeFile(vUserInfo.iconPath);
                    return decodeFile;
                }
            }
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("getUserIcon: unknown user #");
            sb.append(i);
            VLog.m91w(str, sb.toString(), new Object[0]);
            return null;
        }
    }

    public boolean isGuestEnabled() {
        boolean z;
        synchronized (this.mPackagesLock) {
            z = this.mGuestEnabled;
        }
        return z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0031, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003e, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setGuestEnabled(boolean r5) {
        /*
            r4 = this;
            java.lang.String r0 = "enable guest users"
            checkManageUsersPermission(r0)
            java.lang.Object r0 = r4.mPackagesLock
            monitor-enter(r0)
            boolean r1 = r4.mGuestEnabled     // Catch:{ all -> 0x003f }
            if (r1 == r5) goto L_0x003d
            r4.mGuestEnabled = r5     // Catch:{ all -> 0x003f }
            r1 = 0
        L_0x000f:
            android.util.SparseArray<com.lody.virtual.os.VUserInfo> r2 = r4.mUsers     // Catch:{ all -> 0x003f }
            int r2 = r2.size()     // Catch:{ all -> 0x003f }
            if (r1 >= r2) goto L_0x0035
            android.util.SparseArray<com.lody.virtual.os.VUserInfo> r2 = r4.mUsers     // Catch:{ all -> 0x003f }
            java.lang.Object r2 = r2.valueAt(r1)     // Catch:{ all -> 0x003f }
            com.lody.virtual.os.VUserInfo r2 = (com.lody.virtual.p007os.VUserInfo) r2     // Catch:{ all -> 0x003f }
            boolean r3 = r2.partial     // Catch:{ all -> 0x003f }
            if (r3 != 0) goto L_0x0032
            boolean r3 = r2.isGuest()     // Catch:{ all -> 0x003f }
            if (r3 == 0) goto L_0x0032
            if (r5 != 0) goto L_0x0030
            int r5 = r2.f180id     // Catch:{ all -> 0x003f }
            r4.removeUser(r5)     // Catch:{ all -> 0x003f }
        L_0x0030:
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            return
        L_0x0032:
            int r1 = r1 + 1
            goto L_0x000f
        L_0x0035:
            if (r5 == 0) goto L_0x003d
            java.lang.String r5 = "Guest"
            r1 = 4
            r4.createUser(r5, r1)     // Catch:{ all -> 0x003f }
        L_0x003d:
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            return
        L_0x003f:
            r5 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.VUserManagerService.setGuestEnabled(boolean):void");
    }

    public void wipeUser(int i) {
        checkManageUsersPermission("wipe user");
    }

    public void makeInitialized(int i) {
        checkManageUsersPermission("makeInitialized");
        synchronized (this.mPackagesLock) {
            VUserInfo vUserInfo = (VUserInfo) this.mUsers.get(i);
            if (vUserInfo == null || vUserInfo.partial) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("makeInitialized: unknown user #");
                sb.append(i);
                VLog.m91w(str, sb.toString(), new Object[0]);
            }
            if ((vUserInfo.flags & 16) == 0) {
                vUserInfo.flags |= 16;
                writeUserLocked(vUserInfo);
            }
        }
    }

    private boolean isUserLimitReachedLocked() {
        return this.mUsers.size() >= VUserManager.getMaxSupportedUsers();
    }

    private void writeBitmapLocked(VUserInfo vUserInfo, Bitmap bitmap) {
        try {
            File file = new File(this.mUsersDir, Integer.toString(vUserInfo.f180id));
            File file2 = new File(file, USER_PHOTO_FILENAME);
            if (!file.exists()) {
                file.mkdir();
            }
            CompressFormat compressFormat = CompressFormat.PNG;
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            if (bitmap.compress(compressFormat, 100, fileOutputStream)) {
                vUserInfo.iconPath = file2.getAbsolutePath();
            }
            try {
                fileOutputStream.close();
            } catch (IOException unused) {
            }
        } catch (FileNotFoundException e) {
            VLog.m91w(LOG_TAG, "Error setting photo for user ", e);
        }
    }

    public int[] getUserIds() {
        int[] iArr;
        synchronized (this.mPackagesLock) {
            iArr = this.mUserIds;
        }
        return iArr;
    }

    /* access modifiers changed from: 0000 */
    public int[] getUserIdsLPr() {
        return this.mUserIds;
    }

    private void readUserList() {
        synchronized (this.mPackagesLock) {
            readUserListLocked();
        }
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:57:0x00c9, B:62:0x00d2] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:57:0x00c9 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:62:0x00d2 */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x002f A[Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0046  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00ce A[SYNTHETIC, Splitter:B:60:0x00ce] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00d7 A[SYNTHETIC, Splitter:B:65:0x00d7] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00e2 A[SYNTHETIC, Splitter:B:71:0x00e2] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:62:0x00d2=Splitter:B:62:0x00d2, B:57:0x00c9=Splitter:B:57:0x00c9} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readUserListLocked() {
        /*
            r8 = this;
            r0 = 0
            r8.mGuestEnabled = r0
            java.io.File r1 = r8.mUserListFile
            boolean r1 = r1.exists()
            if (r1 != 0) goto L_0x000f
            r8.fallbackToSingleUserLocked()
            return
        L_0x000f:
            com.lody.virtual.helper.utils.AtomicFile r1 = new com.lody.virtual.helper.utils.AtomicFile
            java.io.File r2 = r8.mUserListFile
            r1.<init>(r2)
            r2 = 0
            java.io.FileInputStream r1 = r1.openRead()     // Catch:{ IOException -> 0x00d2, XmlPullParserException -> 0x00c9 }
            org.xmlpull.v1.XmlPullParser r3 = android.util.Xml.newPullParser()     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            r3.setInput(r1, r2)     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
        L_0x0022:
            int r4 = r3.next()     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            r5 = 2
            r6 = 1
            if (r4 == r5) goto L_0x002d
            if (r4 == r6) goto L_0x002d
            goto L_0x0022
        L_0x002d:
            if (r4 == r5) goto L_0x0046
            java.lang.String r2 = "VUserManagerService"
            java.lang.String r3 = "Unable to read user list"
            java.lang.Object[] r0 = new java.lang.Object[r0]     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            com.lody.virtual.helper.utils.VLog.m87e(r2, r3, r0)     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            r8.fallbackToSingleUserLocked()     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            if (r1 == 0) goto L_0x0045
            r1.close()     // Catch:{ IOException -> 0x0041 }
            goto L_0x0045
        L_0x0041:
            r0 = move-exception
            r0.printStackTrace()
        L_0x0045:
            return
        L_0x0046:
            r0 = -1
            r8.mNextSerialNumber = r0     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            java.lang.String r0 = r3.getName()     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            java.lang.String r4 = "users"
            boolean r0 = r0.equals(r4)     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            if (r0 == 0) goto L_0x0071
            java.lang.String r0 = "nextSerialNumber"
            java.lang.String r0 = r3.getAttributeValue(r2, r0)     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            if (r0 == 0) goto L_0x0063
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            r8.mNextSerialNumber = r0     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
        L_0x0063:
            java.lang.String r0 = "version"
            java.lang.String r0 = r3.getAttributeValue(r2, r0)     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            if (r0 == 0) goto L_0x0071
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            r8.mUserVersion = r0     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
        L_0x0071:
            int r0 = r3.next()     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            if (r0 == r6) goto L_0x00b4
            if (r0 != r5) goto L_0x0071
            java.lang.String r0 = r3.getName()     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            java.lang.String r4 = "user"
            boolean r0 = r0.equals(r4)     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            if (r0 == 0) goto L_0x0071
            java.lang.String r0 = "id"
            java.lang.String r0 = r3.getAttributeValue(r2, r0)     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            com.lody.virtual.os.VUserInfo r0 = r8.readUser(r0)     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            if (r0 == 0) goto L_0x0071
            android.util.SparseArray<com.lody.virtual.os.VUserInfo> r4 = r8.mUsers     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            int r7 = r0.f180id     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            r4.put(r7, r0)     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            boolean r4 = r0.isGuest()     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            if (r4 == 0) goto L_0x00a4
            r8.mGuestEnabled = r6     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
        L_0x00a4:
            int r4 = r8.mNextSerialNumber     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            if (r4 < 0) goto L_0x00ae
            int r4 = r8.mNextSerialNumber     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            int r7 = r0.f180id     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            if (r4 > r7) goto L_0x0071
        L_0x00ae:
            int r0 = r0.f180id     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            int r0 = r0 + r6
            r8.mNextSerialNumber = r0     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            goto L_0x0071
        L_0x00b4:
            r8.updateUserIdsLocked()     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            r8.upgradeIfNecessary()     // Catch:{ IOException -> 0x00c4, XmlPullParserException -> 0x00c2, all -> 0x00c0 }
            if (r1 == 0) goto L_0x00df
            r1.close()     // Catch:{ IOException -> 0x00db }
            goto L_0x00df
        L_0x00c0:
            r0 = move-exception
            goto L_0x00e0
        L_0x00c2:
            r2 = r1
            goto L_0x00c9
        L_0x00c4:
            r2 = r1
            goto L_0x00d2
        L_0x00c6:
            r0 = move-exception
            r1 = r2
            goto L_0x00e0
        L_0x00c9:
            r8.fallbackToSingleUserLocked()     // Catch:{ all -> 0x00c6 }
            if (r2 == 0) goto L_0x00df
            r2.close()     // Catch:{ IOException -> 0x00db }
            goto L_0x00df
        L_0x00d2:
            r8.fallbackToSingleUserLocked()     // Catch:{ all -> 0x00c6 }
            if (r2 == 0) goto L_0x00df
            r2.close()     // Catch:{ IOException -> 0x00db }
            goto L_0x00df
        L_0x00db:
            r0 = move-exception
            r0.printStackTrace()
        L_0x00df:
            return
        L_0x00e0:
            if (r1 == 0) goto L_0x00ea
            r1.close()     // Catch:{ IOException -> 0x00e6 }
            goto L_0x00ea
        L_0x00e6:
            r1 = move-exception
            r1.printStackTrace()
        L_0x00ea:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.VUserManagerService.readUserListLocked():void");
    }

    private void upgradeIfNecessary() {
        int i = this.mUserVersion;
        if (i < 1) {
            VUserInfo vUserInfo = (VUserInfo) this.mUsers.get(0);
            if ("Primary".equals(vUserInfo.name)) {
                vUserInfo.name = "Admin";
                writeUserLocked(vUserInfo);
            }
            i = 1;
        }
        if (i < 1) {
            String str = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("User version ");
            sb.append(this.mUserVersion);
            sb.append(" didn't upgrade as expected to ");
            sb.append(1);
            VLog.m91w(str, sb.toString(), new Object[0]);
            return;
        }
        this.mUserVersion = i;
        writeUserListLocked();
    }

    private void fallbackToSingleUserLocked() {
        VUserInfo vUserInfo = new VUserInfo(0, this.mContext.getResources().getString(C0966R.string.owner_name), null, 19);
        this.mUsers.put(0, vUserInfo);
        this.mNextSerialNumber = 1;
        updateUserIdsLocked();
        writeUserListLocked();
        writeUserLocked(vUserInfo);
    }

    private void writeUserLocked(VUserInfo vUserInfo) {
        FileOutputStream fileOutputStream;
        Object e;
        File file = this.mUsersDir;
        StringBuilder sb = new StringBuilder();
        sb.append(vUserInfo.f180id);
        sb.append(".xml");
        AtomicFile atomicFile = new AtomicFile(new File(file, sb.toString()));
        try {
            fileOutputStream = atomicFile.startWrite();
            try {
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                FastXmlSerializer fastXmlSerializer = new FastXmlSerializer();
                fastXmlSerializer.setOutput(bufferedOutputStream, "utf-8");
                fastXmlSerializer.startDocument(null, Boolean.valueOf(true));
                fastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                fastXmlSerializer.startTag(null, "user");
                fastXmlSerializer.attribute(null, "id", Integer.toString(vUserInfo.f180id));
                fastXmlSerializer.attribute(null, ATTR_SERIAL_NO, Integer.toString(vUserInfo.serialNumber));
                fastXmlSerializer.attribute(null, ATTR_FLAGS, Integer.toString(vUserInfo.flags));
                fastXmlSerializer.attribute(null, ATTR_CREATION_TIME, Long.toString(vUserInfo.creationTime));
                fastXmlSerializer.attribute(null, ATTR_LAST_LOGGED_IN_TIME, Long.toString(vUserInfo.lastLoggedInTime));
                if (vUserInfo.iconPath != null) {
                    fastXmlSerializer.attribute(null, "icon", vUserInfo.iconPath);
                }
                if (vUserInfo.partial) {
                    fastXmlSerializer.attribute(null, ATTR_PARTIAL, "true");
                }
                fastXmlSerializer.startTag(null, "name");
                fastXmlSerializer.text(vUserInfo.name);
                fastXmlSerializer.endTag(null, "name");
                fastXmlSerializer.endTag(null, "user");
                fastXmlSerializer.endDocument();
                atomicFile.finishWrite(fileOutputStream);
            } catch (Exception e2) {
                e = e2;
                String str = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Error writing user info ");
                sb2.append(vUserInfo.f180id);
                sb2.append("\n");
                sb2.append(e);
                VLog.m87e(str, sb2.toString(), new Object[0]);
                atomicFile.failWrite(fileOutputStream);
            }
        } catch (Exception e3) {
            Object obj = e3;
            fileOutputStream = null;
            e = obj;
            String str2 = LOG_TAG;
            StringBuilder sb22 = new StringBuilder();
            sb22.append("Error writing user info ");
            sb22.append(vUserInfo.f180id);
            sb22.append("\n");
            sb22.append(e);
            VLog.m87e(str2, sb22.toString(), new Object[0]);
            atomicFile.failWrite(fileOutputStream);
        }
    }

    private void writeUserListLocked() {
        FileOutputStream fileOutputStream;
        AtomicFile atomicFile = new AtomicFile(this.mUserListFile);
        try {
            fileOutputStream = atomicFile.startWrite();
            try {
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                FastXmlSerializer fastXmlSerializer = new FastXmlSerializer();
                fastXmlSerializer.setOutput(bufferedOutputStream, "utf-8");
                fastXmlSerializer.startDocument(null, Boolean.valueOf(true));
                fastXmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                fastXmlSerializer.startTag(null, TAG_USERS);
                fastXmlSerializer.attribute(null, ATTR_NEXT_SERIAL_NO, Integer.toString(this.mNextSerialNumber));
                fastXmlSerializer.attribute(null, ATTR_USER_VERSION, Integer.toString(this.mUserVersion));
                for (int i = 0; i < this.mUsers.size(); i++) {
                    VUserInfo vUserInfo = (VUserInfo) this.mUsers.valueAt(i);
                    fastXmlSerializer.startTag(null, "user");
                    fastXmlSerializer.attribute(null, "id", Integer.toString(vUserInfo.f180id));
                    fastXmlSerializer.endTag(null, "user");
                }
                fastXmlSerializer.endTag(null, TAG_USERS);
                fastXmlSerializer.endDocument();
                atomicFile.finishWrite(fileOutputStream);
            } catch (Exception unused) {
                atomicFile.failWrite(fileOutputStream);
                VLog.m87e(LOG_TAG, "Error writing user list", new Object[0]);
            }
        } catch (Exception unused2) {
            fileOutputStream = null;
            atomicFile.failWrite(fileOutputStream);
            VLog.m87e(LOG_TAG, "Error writing user list", new Object[0]);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0103, code lost:
        if (r3 != null) goto L_0x0105;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x010a, code lost:
        if (r3 != null) goto L_0x0105;
     */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00fe A[SYNTHETIC, Splitter:B:59:0x00fe] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.lody.virtual.p007os.VUserInfo readUser(int r18) {
        /*
            r17 = this;
            r1 = r17
            r0 = r18
            r2 = 0
            com.lody.virtual.helper.utils.AtomicFile r3 = new com.lody.virtual.helper.utils.AtomicFile     // Catch:{ IOException -> 0x0109, XmlPullParserException -> 0x0102, all -> 0x00fa }
            java.io.File r4 = new java.io.File     // Catch:{ IOException -> 0x0109, XmlPullParserException -> 0x0102, all -> 0x00fa }
            java.io.File r5 = r1.mUsersDir     // Catch:{ IOException -> 0x0109, XmlPullParserException -> 0x0102, all -> 0x00fa }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0109, XmlPullParserException -> 0x0102, all -> 0x00fa }
            r6.<init>()     // Catch:{ IOException -> 0x0109, XmlPullParserException -> 0x0102, all -> 0x00fa }
            java.lang.String r7 = java.lang.Integer.toString(r18)     // Catch:{ IOException -> 0x0109, XmlPullParserException -> 0x0102, all -> 0x00fa }
            r6.append(r7)     // Catch:{ IOException -> 0x0109, XmlPullParserException -> 0x0102, all -> 0x00fa }
            java.lang.String r7 = ".xml"
            r6.append(r7)     // Catch:{ IOException -> 0x0109, XmlPullParserException -> 0x0102, all -> 0x00fa }
            java.lang.String r6 = r6.toString()     // Catch:{ IOException -> 0x0109, XmlPullParserException -> 0x0102, all -> 0x00fa }
            r4.<init>(r5, r6)     // Catch:{ IOException -> 0x0109, XmlPullParserException -> 0x0102, all -> 0x00fa }
            r3.<init>(r4)     // Catch:{ IOException -> 0x0109, XmlPullParserException -> 0x0102, all -> 0x00fa }
            java.io.FileInputStream r3 = r3.openRead()     // Catch:{ IOException -> 0x0109, XmlPullParserException -> 0x0102, all -> 0x00fa }
            org.xmlpull.v1.XmlPullParser r4 = android.util.Xml.newPullParser()     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            r4.setInput(r3, r2)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
        L_0x0031:
            int r5 = r4.next()     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            r6 = 1
            r7 = 2
            if (r5 == r7) goto L_0x003c
            if (r5 == r6) goto L_0x003c
            goto L_0x0031
        L_0x003c:
            r8 = 0
            if (r5 == r7) goto L_0x005d
            java.lang.String r4 = "VUserManagerService"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            r5.<init>()     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            java.lang.String r6 = "Unable to read user "
            r5.append(r6)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            r5.append(r0)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            java.lang.String r0 = r5.toString()     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            java.lang.Object[] r5 = new java.lang.Object[r8]     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            com.lody.virtual.helper.utils.VLog.m87e(r4, r0, r5)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            if (r3 == 0) goto L_0x005c
            r3.close()     // Catch:{ IOException -> 0x005c }
        L_0x005c:
            return r2
        L_0x005d:
            java.lang.String r5 = r4.getName()     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            java.lang.String r9 = "user"
            boolean r5 = r5.equals(r9)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            r9 = 0
            if (r5 == 0) goto L_0x00db
            java.lang.String r5 = "id"
            r11 = -1
            int r5 = r1.readIntAttribute(r4, r5, r11)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            if (r5 == r0) goto L_0x0083
            java.lang.String r0 = "VUserManagerService"
            java.lang.String r4 = "User id does not match the file name"
            java.lang.Object[] r5 = new java.lang.Object[r8]     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            com.lody.virtual.helper.utils.VLog.m87e(r0, r4, r5)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            if (r3 == 0) goto L_0x0082
            r3.close()     // Catch:{ IOException -> 0x0082 }
        L_0x0082:
            return r2
        L_0x0083:
            java.lang.String r5 = "serialNumber"
            int r5 = r1.readIntAttribute(r4, r5, r0)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            java.lang.String r11 = "flags"
            int r11 = r1.readIntAttribute(r4, r11, r8)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            java.lang.String r12 = "icon"
            java.lang.String r12 = r4.getAttributeValue(r2, r12)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            java.lang.String r13 = "created"
            long r13 = r1.readLongAttribute(r4, r13, r9)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            java.lang.String r15 = "lastLoggedIn"
            long r9 = r1.readLongAttribute(r4, r15, r9)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            java.lang.String r15 = "partial"
            java.lang.String r15 = r4.getAttributeValue(r2, r15)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            java.lang.String r8 = "true"
            boolean r8 = r8.equals(r15)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            if (r8 == 0) goto L_0x00b2
            r16 = 1
            goto L_0x00b4
        L_0x00b2:
            r16 = 0
        L_0x00b4:
            int r8 = r4.next()     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            if (r8 == r7) goto L_0x00bd
            if (r8 == r6) goto L_0x00bd
            goto L_0x00b4
        L_0x00bd:
            if (r8 != r7) goto L_0x00d7
            java.lang.String r6 = r4.getName()     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            java.lang.String r7 = "name"
            boolean r6 = r6.equals(r7)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            if (r6 == 0) goto L_0x00d7
            int r6 = r4.next()     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            r7 = 4
            if (r6 != r7) goto L_0x00d7
            java.lang.String r4 = r4.getText()     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            goto L_0x00d8
        L_0x00d7:
            r4 = r2
        L_0x00d8:
            r6 = r16
            goto L_0x00e1
        L_0x00db:
            r5 = r0
            r4 = r2
            r12 = r4
            r13 = r9
            r6 = 0
            r11 = 0
        L_0x00e1:
            com.lody.virtual.os.VUserInfo r7 = new com.lody.virtual.os.VUserInfo     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            r7.<init>(r0, r4, r12, r11)     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            r7.serialNumber = r5     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            r7.creationTime = r13     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            r7.lastLoggedInTime = r9     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            r7.partial = r6     // Catch:{ IOException -> 0x00f8, XmlPullParserException -> 0x00f6, all -> 0x00f4 }
            if (r3 == 0) goto L_0x00f3
            r3.close()     // Catch:{ IOException -> 0x00f3 }
        L_0x00f3:
            return r7
        L_0x00f4:
            r0 = move-exception
            goto L_0x00fc
        L_0x00f6:
            goto L_0x0103
        L_0x00f8:
            goto L_0x010a
        L_0x00fa:
            r0 = move-exception
            r3 = r2
        L_0x00fc:
            if (r3 == 0) goto L_0x0101
            r3.close()     // Catch:{ IOException -> 0x0101 }
        L_0x0101:
            throw r0
        L_0x0102:
            r3 = r2
        L_0x0103:
            if (r3 == 0) goto L_0x010d
        L_0x0105:
            r3.close()     // Catch:{ IOException -> 0x010d }
            goto L_0x010d
        L_0x0109:
            r3 = r2
        L_0x010a:
            if (r3 == 0) goto L_0x010d
            goto L_0x0105
        L_0x010d:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.VUserManagerService.readUser(int):com.lody.virtual.os.VUserInfo");
    }

    private int readIntAttribute(XmlPullParser xmlPullParser, String str, int i) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        if (attributeValue == null) {
            return i;
        }
        try {
            return Integer.parseInt(attributeValue);
        } catch (NumberFormatException unused) {
            return i;
        }
    }

    private long readLongAttribute(XmlPullParser xmlPullParser, String str, long j) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        if (attributeValue == null) {
            return j;
        }
        try {
            return Long.parseLong(attributeValue);
        } catch (NumberFormatException unused) {
            return j;
        }
    }

    public VUserInfo createUser(String str, int i) {
        checkManageUsersPermission("Only the system can create users");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mInstallLock) {
                synchronized (this.mPackagesLock) {
                    if (isUserLimitReachedLocked()) {
                        return null;
                    }
                    int nextAvailableIdLocked = getNextAvailableIdLocked();
                    VUserInfo vUserInfo = new VUserInfo(nextAvailableIdLocked, str, null, i);
                    File file = new File(this.mBaseUserPath, Integer.toString(nextAvailableIdLocked));
                    int i2 = this.mNextSerialNumber;
                    this.mNextSerialNumber = i2 + 1;
                    vUserInfo.serialNumber = i2;
                    long currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis <= EPOCH_PLUS_30_YEARS) {
                        currentTimeMillis = 0;
                    }
                    vUserInfo.creationTime = currentTimeMillis;
                    vUserInfo.partial = true;
                    VEnvironment.getUserSystemDirectory(vUserInfo.f180id).mkdirs();
                    this.mUsers.put(nextAvailableIdLocked, vUserInfo);
                    writeUserListLocked();
                    writeUserLocked(vUserInfo);
                    this.mPm.createNewUser(nextAvailableIdLocked, file);
                    vUserInfo.partial = false;
                    writeUserLocked(vUserInfo);
                    updateUserIdsLocked();
                    Intent intent = new Intent(Constants.ACTION_USER_ADDED);
                    intent.putExtra(Constants.EXTRA_USER_HANDLE, vUserInfo.f180id);
                    VActivityManagerService.get().sendBroadcastAsUser(intent, VUserHandle.ALL, null);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return vUserInfo;
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0033, code lost:
        if (com.lody.virtual.server.p008am.VActivityManagerService.get().stopUser(r6, new com.lody.virtual.server.p009pm.VUserManagerService.C11241(r5)) != 0) goto L_0x0036;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0035, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0036, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean removeUser(int r6) {
        /*
            r5 = this;
            java.lang.String r0 = "Only the system can remove users"
            checkManageUsersPermission(r0)
            java.lang.Object r0 = r5.mPackagesLock
            monitor-enter(r0)
            android.util.SparseArray<com.lody.virtual.os.VUserInfo> r1 = r5.mUsers     // Catch:{ all -> 0x0039 }
            java.lang.Object r1 = r1.get(r6)     // Catch:{ all -> 0x0039 }
            com.lody.virtual.os.VUserInfo r1 = (com.lody.virtual.p007os.VUserInfo) r1     // Catch:{ all -> 0x0039 }
            r2 = 0
            if (r6 == 0) goto L_0x0037
            if (r1 != 0) goto L_0x0016
            goto L_0x0037
        L_0x0016:
            java.util.HashSet<java.lang.Integer> r3 = r5.mRemovingUserIds     // Catch:{ all -> 0x0039 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x0039 }
            r3.add(r4)     // Catch:{ all -> 0x0039 }
            r3 = 1
            r1.partial = r3     // Catch:{ all -> 0x0039 }
            r5.writeUserLocked(r1)     // Catch:{ all -> 0x0039 }
            monitor-exit(r0)     // Catch:{ all -> 0x0039 }
            com.lody.virtual.server.am.VActivityManagerService r0 = com.lody.virtual.server.p008am.VActivityManagerService.get()
            com.lody.virtual.server.pm.VUserManagerService$1 r1 = new com.lody.virtual.server.pm.VUserManagerService$1
            r1.<init>()
            int r6 = r0.stopUser(r6, r1)
            if (r6 != 0) goto L_0x0036
            r2 = 1
        L_0x0036:
            return r2
        L_0x0037:
            monitor-exit(r0)     // Catch:{ all -> 0x0039 }
            return r2
        L_0x0039:
            r6 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0039 }
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.VUserManagerService.removeUser(int):boolean");
    }

    /* access modifiers changed from: 0000 */
    public void finishRemoveUser(final int i) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Intent intent = new Intent(Constants.ACTION_USER_REMOVED);
            intent.putExtra(Constants.EXTRA_USER_HANDLE, i);
            VActivityManagerService.get().sendOrderedBroadcastAsUser(intent, VUserHandle.ALL, null, new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    new Thread() {
                        public void run() {
                            synchronized (VUserManagerService.this.mInstallLock) {
                                synchronized (VUserManagerService.this.mPackagesLock) {
                                    VUserManagerService.this.removeUserStateLocked(i);
                                }
                            }
                        }
                    }.start();
                }
            }, null, -1, null, null);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* access modifiers changed from: private */
    public void removeUserStateLocked(int i) {
        this.mPm.cleanUpUser(i);
        this.mUsers.remove(i);
        this.mRemovingUserIds.remove(Integer.valueOf(i));
        File file = this.mUsersDir;
        StringBuilder sb = new StringBuilder();
        sb.append(i);
        sb.append(".xml");
        new AtomicFile(new File(file, sb.toString())).delete();
        writeUserListLocked();
        updateUserIdsLocked();
        removeDirectoryRecursive(VEnvironment.getUserSystemDirectory(i));
    }

    private void removeDirectoryRecursive(File file) {
        if (file.isDirectory()) {
            for (String file2 : file.list()) {
                removeDirectoryRecursive(new File(file, file2));
            }
        }
        file.delete();
    }

    public int getUserSerialNumber(int i) {
        synchronized (this.mPackagesLock) {
            if (!exists(i)) {
                return -1;
            }
            int i2 = getUserInfoLocked(i).serialNumber;
            return i2;
        }
    }

    public int getUserHandle(int i) {
        int[] iArr;
        synchronized (this.mPackagesLock) {
            for (int i2 : this.mUserIds) {
                if (getUserInfoLocked(i2).serialNumber == i) {
                    return i2;
                }
            }
            return -1;
        }
    }

    private void updateUserIdsLocked() {
        int i = 0;
        for (int i2 = 0; i2 < this.mUsers.size(); i2++) {
            if (!((VUserInfo) this.mUsers.valueAt(i2)).partial) {
                i++;
            }
        }
        int[] iArr = new int[i];
        int i3 = 0;
        for (int i4 = 0; i4 < this.mUsers.size(); i4++) {
            if (!((VUserInfo) this.mUsers.valueAt(i4)).partial) {
                int i5 = i3 + 1;
                iArr[i3] = this.mUsers.keyAt(i4);
                i3 = i5;
            }
        }
        this.mUserIds = iArr;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0025, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void userForeground(int r7) {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mPackagesLock
            monitor-enter(r0)
            android.util.SparseArray<com.lody.virtual.os.VUserInfo> r1 = r6.mUsers     // Catch:{ all -> 0x0041 }
            java.lang.Object r1 = r1.get(r7)     // Catch:{ all -> 0x0041 }
            com.lody.virtual.os.VUserInfo r1 = (com.lody.virtual.p007os.VUserInfo) r1     // Catch:{ all -> 0x0041 }
            long r2 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0041 }
            if (r1 == 0) goto L_0x0026
            boolean r4 = r1.partial     // Catch:{ all -> 0x0041 }
            if (r4 == 0) goto L_0x0016
            goto L_0x0026
        L_0x0016:
            r4 = 946080000000(0xdc46c32800, double:4.674256262175E-312)
            int r7 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r7 <= 0) goto L_0x0024
            r1.lastLoggedInTime = r2     // Catch:{ all -> 0x0041 }
            r6.writeUserLocked(r1)     // Catch:{ all -> 0x0041 }
        L_0x0024:
            monitor-exit(r0)     // Catch:{ all -> 0x0041 }
            return
        L_0x0026:
            java.lang.String r1 = "VUserManagerService"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0041 }
            r2.<init>()     // Catch:{ all -> 0x0041 }
            java.lang.String r3 = "userForeground: unknown user #"
            r2.append(r3)     // Catch:{ all -> 0x0041 }
            r2.append(r7)     // Catch:{ all -> 0x0041 }
            java.lang.String r7 = r2.toString()     // Catch:{ all -> 0x0041 }
            r2 = 0
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0041 }
            com.lody.virtual.helper.utils.VLog.m91w(r1, r7, r2)     // Catch:{ all -> 0x0041 }
            monitor-exit(r0)     // Catch:{ all -> 0x0041 }
            return
        L_0x0041:
            r7 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0041 }
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lody.virtual.server.p009pm.VUserManagerService.userForeground(int):void");
    }

    private int getNextAvailableIdLocked() {
        int i;
        synchronized (this.mPackagesLock) {
            i = this.mNextUserId;
            while (true) {
                if (i < Integer.MAX_VALUE) {
                    if (this.mUsers.indexOfKey(i) < 0 && !this.mRemovingUserIds.contains(Integer.valueOf(i))) {
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
            this.mNextUserId = i + 1;
        }
        return i;
    }
}
