package p015me.weishu.epic.art.method;

import android.os.Build.VERSION;
import com.taobao.android.dexposed.utility.Runtime;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import p015me.weishu.epic.art.EpicNative;

/* renamed from: me.weishu.epic.art.method.Offset */
class Offset {
    static Offset ART_ACCESS_FLAG_OFFSET = null;
    static Offset ART_JNI_ENTRY_OFFSET = null;
    static Offset ART_QUICK_CODE_OFFSET = null;
    private static final String TAG = "Offset";
    private BitWidth length;
    private long offset;

    /* renamed from: me.weishu.epic.art.method.Offset$BitWidth */
    private enum BitWidth {
        DWORD(4),
        QWORD(8);
        
        int width;

        private BitWidth(int i) {
            this.width = i;
        }
    }

    Offset() {
    }

    static {
        initFields();
    }

    public long getOffset() {
        return this.offset;
    }

    public void setOffset(long j) {
        this.offset = j;
    }

    public BitWidth getLength() {
        return this.length;
    }

    public void setLength(BitWidth bitWidth) {
        this.length = bitWidth;
    }

    public static long read(long j, Offset offset2) {
        byte[] bArr = EpicNative.get(j + offset2.offset, offset2.length.width);
        if (offset2.length == BitWidth.DWORD) {
            return ((long) ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).getInt()) & 4294967295L;
        }
        return ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    public static void write(long j, Offset offset2, long j2) {
        byte[] bArr;
        long j3 = j + offset2.offset;
        if (offset2.length != BitWidth.DWORD) {
            bArr = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(j2).array();
        } else if (j2 <= 4294967295L) {
            bArr = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) j2).array();
        } else {
            throw new IllegalStateException("overflow may occur");
        }
        EpicNative.put(bArr, j3);
    }

    private static void initFields() {
        ART_QUICK_CODE_OFFSET = new Offset();
        ART_ACCESS_FLAG_OFFSET = new Offset();
        ART_JNI_ENTRY_OFFSET = new Offset();
        ART_ACCESS_FLAG_OFFSET.setLength(BitWidth.DWORD);
        int i = VERSION.SDK_INT;
        if (Runtime.is64Bit()) {
            ART_QUICK_CODE_OFFSET.setLength(BitWidth.QWORD);
            ART_JNI_ENTRY_OFFSET.setLength(BitWidth.QWORD);
            switch (i) {
                case 19:
                    ART_QUICK_CODE_OFFSET.setOffset(32);
                    ART_ACCESS_FLAG_OFFSET.setOffset(28);
                    return;
                case 21:
                    ART_QUICK_CODE_OFFSET.setOffset(40);
                    ART_QUICK_CODE_OFFSET.setLength(BitWidth.QWORD);
                    ART_JNI_ENTRY_OFFSET.setOffset(32);
                    ART_JNI_ENTRY_OFFSET.setLength(BitWidth.QWORD);
                    ART_ACCESS_FLAG_OFFSET.setOffset(56);
                    return;
                case 22:
                    ART_QUICK_CODE_OFFSET.setOffset(52);
                    ART_JNI_ENTRY_OFFSET.setOffset(44);
                    ART_ACCESS_FLAG_OFFSET.setOffset(20);
                    return;
                case 23:
                    ART_QUICK_CODE_OFFSET.setOffset(48);
                    ART_JNI_ENTRY_OFFSET.setOffset(40);
                    ART_ACCESS_FLAG_OFFSET.setOffset(12);
                    return;
                case 24:
                case 25:
                    ART_QUICK_CODE_OFFSET.setOffset(48);
                    ART_JNI_ENTRY_OFFSET.setOffset(40);
                    ART_ACCESS_FLAG_OFFSET.setOffset(4);
                    return;
                case 26:
                case 27:
                    ART_QUICK_CODE_OFFSET.setOffset(40);
                    ART_JNI_ENTRY_OFFSET.setOffset(32);
                    ART_ACCESS_FLAG_OFFSET.setOffset(4);
                    return;
                case 28:
                case 29:
                    ART_QUICK_CODE_OFFSET.setOffset(32);
                    ART_JNI_ENTRY_OFFSET.setOffset(24);
                    ART_ACCESS_FLAG_OFFSET.setOffset(4);
                    return;
                default:
                    StringBuilder sb = new StringBuilder();
                    sb.append("API LEVEL: ");
                    sb.append(i);
                    sb.append(" is not supported now : (");
                    throw new RuntimeException(sb.toString());
            }
        } else {
            ART_QUICK_CODE_OFFSET.setLength(BitWidth.DWORD);
            ART_JNI_ENTRY_OFFSET.setLength(BitWidth.DWORD);
            switch (i) {
                case 19:
                    ART_QUICK_CODE_OFFSET.setOffset(32);
                    ART_ACCESS_FLAG_OFFSET.setOffset(28);
                    return;
                case 21:
                    ART_QUICK_CODE_OFFSET.setOffset(40);
                    ART_QUICK_CODE_OFFSET.setLength(BitWidth.QWORD);
                    ART_JNI_ENTRY_OFFSET.setOffset(32);
                    ART_JNI_ENTRY_OFFSET.setLength(BitWidth.QWORD);
                    ART_ACCESS_FLAG_OFFSET.setOffset(56);
                    return;
                case 22:
                    ART_QUICK_CODE_OFFSET.setOffset(44);
                    ART_JNI_ENTRY_OFFSET.setOffset(40);
                    ART_ACCESS_FLAG_OFFSET.setOffset(20);
                    return;
                case 23:
                    ART_QUICK_CODE_OFFSET.setOffset(36);
                    ART_JNI_ENTRY_OFFSET.setOffset(32);
                    ART_ACCESS_FLAG_OFFSET.setOffset(12);
                    return;
                case 24:
                case 25:
                    ART_QUICK_CODE_OFFSET.setOffset(32);
                    ART_JNI_ENTRY_OFFSET.setOffset(28);
                    ART_ACCESS_FLAG_OFFSET.setOffset(4);
                    return;
                case 26:
                case 27:
                    ART_QUICK_CODE_OFFSET.setOffset(28);
                    ART_JNI_ENTRY_OFFSET.setOffset(24);
                    ART_ACCESS_FLAG_OFFSET.setOffset(4);
                    return;
                case 28:
                case 29:
                    ART_QUICK_CODE_OFFSET.setOffset(24);
                    ART_JNI_ENTRY_OFFSET.setOffset(20);
                    ART_ACCESS_FLAG_OFFSET.setOffset(4);
                    return;
                default:
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("API LEVEL: ");
                    sb2.append(i);
                    sb2.append(" is not supported now : (");
                    throw new RuntimeException(sb2.toString());
            }
        }
    }
}
