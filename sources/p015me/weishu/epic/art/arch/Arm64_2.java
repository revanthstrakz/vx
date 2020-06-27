package p015me.weishu.epic.art.arch;

import java.nio.ByteOrder;

/* renamed from: me.weishu.epic.art.arch.Arm64_2 */
public class Arm64_2 extends ShellCode {
    public String getName() {
        return "64-bit ARM(Android M)";
    }

    public int sizeOfBridgeJump() {
        return 88;
    }

    public int sizeOfDirectJump() {
        return 16;
    }

    public long toMem(long j) {
        return j;
    }

    public long toPC(long j) {
        return j;
    }

    public byte[] createDirectJump(long j) {
        byte[] bArr = {80, 0, 0, 88, 0, 2, 31, -42, 0, 0, 0, 0, 0, 0, 0, 0};
        writeLong(j, ByteOrder.LITTLE_ENDIAN, bArr, bArr.length - 8);
        return bArr;
    }

    public byte[] createBridgeJump(long j, long j2, long j3, long j4) {
        byte[] bArr = {31, 32, 3, -43, 49, 2, 0, 88, 31, 0, 17, -21, 97, 2, 0, 84, 64, 1, 0, 88, -15, 1, 0, 88, -16, 3, 0, -111, 48, 2, 0, -7, 34, 6, 0, -7, 48, 1, 0, 88, 48, 10, 0, -7, -30, 3, 17, -86, -111, 0, 0, 88, 32, 2, 31, -42, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        writeLong(j, ByteOrder.LITTLE_ENDIAN, bArr, bArr.length - 32);
        writeLong(j2, ByteOrder.LITTLE_ENDIAN, bArr, bArr.length - 24);
        writeLong(j3, ByteOrder.LITTLE_ENDIAN, bArr, bArr.length - 16);
        writeLong(j4, ByteOrder.LITTLE_ENDIAN, bArr, bArr.length - 8);
        return bArr;
    }
}
