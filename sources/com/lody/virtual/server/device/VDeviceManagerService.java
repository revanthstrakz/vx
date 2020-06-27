package com.lody.virtual.server.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.RemoteException;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.collection.SparseArray;
import com.lody.virtual.remote.VDeviceInfo;
import com.lody.virtual.server.IDeviceInfoManager.Stub;
import com.microsoft.appcenter.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class VDeviceManagerService extends Stub {
    private static VDeviceManagerService sInstance = new VDeviceManagerService();
    private final SparseArray<VDeviceInfo> mDeviceInfos = new SparseArray<>();
    private DeviceInfoPersistenceLayer mPersistenceLayer = new DeviceInfoPersistenceLayer(this);
    private UsedDeviceInfoPool mPool = new UsedDeviceInfoPool();

    private final class UsedDeviceInfoPool {
        List<String> androidIds;
        List<String> bluetoothMacs;
        List<String> deviceIds;
        List<String> iccIds;
        List<String> wifiMacs;

        private UsedDeviceInfoPool() {
            this.deviceIds = new ArrayList();
            this.androidIds = new ArrayList();
            this.wifiMacs = new ArrayList();
            this.bluetoothMacs = new ArrayList();
            this.iccIds = new ArrayList();
        }
    }

    public static VDeviceManagerService get() {
        return sInstance;
    }

    public VDeviceManagerService() {
        this.mPersistenceLayer.read();
        for (int i = 0; i < this.mDeviceInfos.size(); i++) {
            addDeviceInfoToPool((VDeviceInfo) this.mDeviceInfos.valueAt(i));
        }
    }

    private void addDeviceInfoToPool(VDeviceInfo vDeviceInfo) {
        this.mPool.deviceIds.add(vDeviceInfo.deviceId);
        this.mPool.androidIds.add(vDeviceInfo.androidId);
        this.mPool.wifiMacs.add(vDeviceInfo.wifiMac);
        this.mPool.bluetoothMacs.add(vDeviceInfo.bluetoothMac);
        this.mPool.iccIds.add(vDeviceInfo.iccId);
    }

    public VDeviceInfo getDeviceInfo(int i) throws RemoteException {
        VDeviceInfo vDeviceInfo;
        synchronized (this.mDeviceInfos) {
            vDeviceInfo = (VDeviceInfo) this.mDeviceInfos.get(i);
            if (vDeviceInfo == null) {
                vDeviceInfo = generateDeviceInfo();
                this.mDeviceInfos.put(i, vDeviceInfo);
                this.mPersistenceLayer.save();
            }
        }
        return vDeviceInfo;
    }

    public void updateDeviceInfo(int i, VDeviceInfo vDeviceInfo) throws RemoteException {
        synchronized (this.mDeviceInfos) {
            if (vDeviceInfo != null) {
                try {
                    this.mDeviceInfos.put(i, vDeviceInfo);
                    this.mPersistenceLayer.save();
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    private VDeviceInfo generateRandomDeviceInfo() {
        String generate10;
        String generate16;
        String generateMac;
        String generateMac2;
        String generate102;
        VDeviceInfo vDeviceInfo = new VDeviceInfo();
        do {
            generate10 = generate10(15);
            vDeviceInfo.deviceId = generate10;
        } while (this.mPool.deviceIds.contains(generate10));
        do {
            generate16 = generate16(16);
            vDeviceInfo.androidId = generate16;
        } while (this.mPool.androidIds.contains(generate16));
        do {
            generateMac = generateMac();
            vDeviceInfo.wifiMac = generateMac;
        } while (this.mPool.wifiMacs.contains(generateMac));
        do {
            generateMac2 = generateMac();
            vDeviceInfo.bluetoothMac = generateMac2;
        } while (this.mPool.bluetoothMacs.contains(generateMac2));
        do {
            generate102 = generate10(20);
            vDeviceInfo.iccId = generate102;
        } while (this.mPool.iccIds.contains(generate102));
        vDeviceInfo.serial = generateSerial();
        addDeviceInfoToPool(vDeviceInfo);
        return vDeviceInfo;
    }

    @SuppressLint({"HardwareIds"})
    private VDeviceInfo generateDeviceInfo() {
        VDeviceInfo generateRandomDeviceInfo = generateRandomDeviceInfo();
        Context context = VirtualCore.get().getContext();
        if (context == null) {
            return generateRandomDeviceInfo;
        }
        String str = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            if (telephonyManager != null) {
                str = telephonyManager.getDeviceId();
            }
            if (str != null) {
                generateRandomDeviceInfo.deviceId = str;
            }
            String string = System.getString(context.getContentResolver(), "android_id");
            if (string != null) {
                generateRandomDeviceInfo.androidId = string;
            }
            generateRandomDeviceInfo.serial = Build.SERIAL;
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return generateRandomDeviceInfo;
    }

    /* access modifiers changed from: 0000 */
    public SparseArray<VDeviceInfo> getDeviceInfos() {
        return this.mDeviceInfos;
    }

    private static String generate10(int i) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < i; i2++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private static String generate16(int i) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < i; i2++) {
            int nextInt = random.nextInt(16);
            if (nextInt < 10) {
                sb.append(nextInt);
            } else {
                sb.append((char) (nextInt + 87));
            }
        }
        return sb.toString();
    }

    private static String generateMac() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (int i2 = 0; i2 < 12; i2++) {
            int nextInt = random.nextInt(16);
            if (nextInt < 10) {
                sb.append(nextInt);
            } else {
                sb.append((char) (nextInt + 87));
            }
            if (i2 == i && i2 != 11) {
                sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
                i += 2;
            }
        }
        return sb.toString();
    }

    @SuppressLint({"HardwareIds"})
    private static String generateSerial() {
        String str = (Build.SERIAL == null || Build.SERIAL.length() <= 0) ? "0123456789ABCDEF" : Build.SERIAL;
        ArrayList<Character> arrayList = new ArrayList<>();
        for (char valueOf : str.toCharArray()) {
            arrayList.add(Character.valueOf(valueOf));
        }
        Collections.shuffle(arrayList);
        StringBuilder sb = new StringBuilder();
        for (Character charValue : arrayList) {
            sb.append(charValue.charValue());
        }
        return sb.toString();
    }
}
