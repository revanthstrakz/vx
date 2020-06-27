package com.lody.virtual.server.p008am;

import com.lody.virtual.helper.utils.FileUtils;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.p007os.VEnvironment;
import com.lody.virtual.server.p009pm.parser.VPackage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/* renamed from: com.lody.virtual.server.am.UidSystem */
public class UidSystem {
    private static final String TAG = "UidSystem";
    private int mFreeUid = 10000;
    private final HashMap<String, Integer> mSharedUserIdMap = new HashMap<>();

    public void initUidList() {
        this.mSharedUserIdMap.clear();
        if (!loadUidList(VEnvironment.getUidListFile())) {
            loadUidList(VEnvironment.getBakUidListFile());
        }
    }

    private boolean loadUidList(File file) {
        if (!file.exists()) {
            return false;
        }
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            this.mFreeUid = objectInputStream.readInt();
            this.mSharedUserIdMap.putAll((HashMap) objectInputStream.readObject());
            objectInputStream.close();
            return true;
        } catch (Throwable unused) {
            return false;
        }
    }

    private void save() {
        File uidListFile = VEnvironment.getUidListFile();
        File bakUidListFile = VEnvironment.getBakUidListFile();
        if (uidListFile.exists()) {
            if (bakUidListFile.exists() && !bakUidListFile.delete()) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Warning: Unable to delete the expired file --\n ");
                sb.append(bakUidListFile.getPath());
                VLog.m91w(str, sb.toString(), new Object[0]);
            }
            try {
                FileUtils.copyFile(uidListFile, bakUidListFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(uidListFile));
            objectOutputStream.writeInt(this.mFreeUid);
            objectOutputStream.writeObject(this.mSharedUserIdMap);
            objectOutputStream.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public int getOrCreateUid(VPackage vPackage) {
        String str = vPackage.mSharedUserId;
        if (str == null) {
            str = vPackage.packageName;
        }
        Integer num = (Integer) this.mSharedUserIdMap.get(str);
        if (num != null) {
            return num.intValue();
        }
        int i = this.mFreeUid + 1;
        this.mFreeUid = i;
        this.mSharedUserIdMap.put(str, Integer.valueOf(i));
        save();
        return i;
    }
}
