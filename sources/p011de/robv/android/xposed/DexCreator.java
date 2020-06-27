package p011de.robv.android.xposed;

import android.os.Environment;
import android.support.p001v4.app.FragmentTransaction;
import com.android.launcher3.dragndrop.DragView;
import com.bumptech.glide.load.Key;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;

/* renamed from: de.robv.android.xposed.DexCreator */
class DexCreator {
    public static File DALVIK_CACHE = new File(Environment.getDataDirectory(), "dalvik-cache");

    public static File getDefaultFile(String str) {
        File file = DALVIK_CACHE;
        StringBuilder sb = new StringBuilder();
        sb.append("xposed_");
        sb.append(str.substring(str.lastIndexOf(46) + 1));
        sb.append(".dex");
        return new File(file, sb.toString());
    }

    public static File ensure(String str, Class<?> cls, Class<?> cls2) throws IOException {
        if (cls2.isAssignableFrom(cls)) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("xposed.dummy.");
                sb.append(str);
                sb.append("SuperClass");
                return ensure(sb.toString(), cls);
            } catch (IOException e) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Failed to create a superclass for ");
                sb2.append(str);
                throw new IOException(sb2.toString(), e);
            }
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Cannot initialize ");
            sb3.append(str);
            sb3.append(" because ");
            sb3.append(cls);
            sb3.append(" does not extend ");
            sb3.append(cls2);
            throw new ClassCastException(sb3.toString());
        }
    }

    public static File ensure(String str, Class<?> cls) throws IOException {
        return ensure(getDefaultFile(str), str, cls.getName());
    }

    public static File ensure(File file, String str, String str2) throws IOException {
        try {
            if (matches(XposedHelpers.inputStreamToByteArray(new FileInputStream(file)), str, str2)) {
                return file;
            }
            file.delete();
            byte[] create = create(str, str2);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(create);
            fileOutputStream.close();
            return file;
        } catch (IOException unused) {
            file.delete();
        }
    }

    public static boolean matches(byte[] bArr, String str, String str2) throws IOException {
        boolean z = str.compareTo(str2) < 0;
        StringBuilder sb = new StringBuilder();
        sb.append("L");
        sb.append(str.replace('.', '/'));
        sb.append(";");
        byte[] stringToBytes = stringToBytes(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("L");
        sb2.append(str2.replace('.', '/'));
        sb2.append(";");
        byte[] stringToBytes2 = stringToBytes(sb2.toString());
        if (stringToBytes.length + 160 + stringToBytes2.length >= bArr.length) {
            return false;
        }
        byte[] bArr2 = z ? stringToBytes : stringToBytes2;
        int length = bArr2.length;
        int i = 0;
        int i2 = 160;
        while (i < length) {
            int i3 = i2 + 1;
            if (bArr[i2] != bArr2[i]) {
                return false;
            }
            i++;
            i2 = i3;
        }
        if (z) {
            stringToBytes = stringToBytes2;
        }
        int length2 = stringToBytes.length;
        int i4 = 0;
        while (i4 < length2) {
            int i5 = i2 + 1;
            if (bArr[i2] != stringToBytes[i4]) {
                return false;
            }
            i4++;
            i2 = i5;
        }
        return true;
    }

    public static byte[] create(String str, String str2) throws IOException {
        int i = str.compareTo(str2) < 0 ? 1 : 0;
        StringBuilder sb = new StringBuilder();
        sb.append("L");
        sb.append(str.replace('.', '/'));
        sb.append(";");
        byte[] stringToBytes = stringToBytes(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("L");
        sb2.append(str2.replace('.', '/'));
        sb2.append(";");
        byte[] stringToBytes2 = stringToBytes(sb2.toString());
        int length = stringToBytes.length + stringToBytes2.length;
        int i2 = (-length) & 3;
        int i3 = length + i2;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write("dex\n035\u0000".getBytes());
        byteArrayOutputStream.write(new byte[24]);
        writeInt(byteArrayOutputStream, i3 + 252);
        writeInt(byteArrayOutputStream, 112);
        writeInt(byteArrayOutputStream, 305419896);
        writeInt(byteArrayOutputStream, 0);
        writeInt(byteArrayOutputStream, 0);
        int i4 = i3 + 164;
        writeInt(byteArrayOutputStream, i4);
        writeInt(byteArrayOutputStream, 2);
        writeInt(byteArrayOutputStream, 112);
        writeInt(byteArrayOutputStream, 2);
        writeInt(byteArrayOutputStream, DragView.COLOR_CHANGE_DURATION);
        writeInt(byteArrayOutputStream, 0);
        writeInt(byteArrayOutputStream, 0);
        writeInt(byteArrayOutputStream, 0);
        writeInt(byteArrayOutputStream, 0);
        writeInt(byteArrayOutputStream, 0);
        writeInt(byteArrayOutputStream, 0);
        writeInt(byteArrayOutputStream, 1);
        writeInt(byteArrayOutputStream, 128);
        writeInt(byteArrayOutputStream, i3 + 92);
        writeInt(byteArrayOutputStream, 160);
        writeInt(byteArrayOutputStream, 160);
        writeInt(byteArrayOutputStream, (i != 0 ? stringToBytes.length : stringToBytes2.length) + 160);
        writeInt(byteArrayOutputStream, 0);
        writeInt(byteArrayOutputStream, 1);
        writeInt(byteArrayOutputStream, i ^ 1);
        writeInt(byteArrayOutputStream, 1);
        writeInt(byteArrayOutputStream, i);
        writeInt(byteArrayOutputStream, 0);
        writeInt(byteArrayOutputStream, -1);
        writeInt(byteArrayOutputStream, 0);
        writeInt(byteArrayOutputStream, 0);
        writeInt(byteArrayOutputStream, 0);
        byteArrayOutputStream.write(i != 0 ? stringToBytes : stringToBytes2);
        if (i != 0) {
            stringToBytes = stringToBytes2;
        }
        byteArrayOutputStream.write(stringToBytes);
        byteArrayOutputStream.write(new byte[i2]);
        writeInt(byteArrayOutputStream, 0);
        writeInt(byteArrayOutputStream, 7);
        writeMapItem(byteArrayOutputStream, 0, 1, 0);
        writeMapItem(byteArrayOutputStream, 1, 2, 112);
        writeMapItem(byteArrayOutputStream, 2, 2, DragView.COLOR_CHANGE_DURATION);
        writeMapItem(byteArrayOutputStream, 6, 1, 128);
        writeMapItem(byteArrayOutputStream, 8194, 2, 160);
        writeMapItem(byteArrayOutputStream, FragmentTransaction.TRANSIT_FRAGMENT_FADE, 1, i3 + 160);
        writeMapItem(byteArrayOutputStream, 4096, 1, i4);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        updateSignature(byteArray);
        updateChecksum(byteArray);
        return byteArray;
    }

    private static void updateSignature(byte[] bArr) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-1");
            instance.update(bArr, 32, bArr.length - 32);
            instance.digest(bArr, 12, 20);
        } catch (DigestException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateChecksum(byte[] bArr) {
        Adler32 adler32 = new Adler32();
        adler32.update(bArr, 12, bArr.length - 12);
        int value = (int) adler32.getValue();
        bArr[8] = (byte) (value & 255);
        bArr[9] = (byte) ((value >> 8) & 255);
        bArr[10] = (byte) ((value >> 16) & 255);
        bArr[11] = (byte) ((value >> 24) & 255);
    }

    private static void writeUleb128(OutputStream outputStream, int i) throws IOException {
        while (i > 127) {
            outputStream.write((i & 127) | 128);
            i >>>= 7;
        }
        outputStream.write(i);
    }

    private static void writeInt(OutputStream outputStream, int i) throws IOException {
        outputStream.write(i);
        outputStream.write(i >> 8);
        outputStream.write(i >> 16);
        outputStream.write(i >> 24);
    }

    private static void writeMapItem(OutputStream outputStream, int i, int i2, int i3) throws IOException {
        writeInt(outputStream, i);
        writeInt(outputStream, i2);
        writeInt(outputStream, i3);
    }

    private static byte[] stringToBytes(String str) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writeUleb128(byteArrayOutputStream, str.length());
        byteArrayOutputStream.write(str.getBytes(Key.STRING_CHARSET_NAME));
        byteArrayOutputStream.write(0);
        return byteArrayOutputStream.toByteArray();
    }

    private DexCreator() {
    }
}
