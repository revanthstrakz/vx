package p015me.weishu.epic.art.arch;

import java.nio.ByteOrder;

/* renamed from: me.weishu.epic.art.arch.Arm64 */
public class Arm64 extends ShellCode {
    public String getName() {
        return "64-bit ARM";
    }

    public int sizeOfBridgeJump() {
        return 96;
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
        byte[] bArr = {31, 32, 3, -43, 105, 2, 0, 88, 31, 0, 9, -21, -95, 2, 0, 84, Byte.MIN_VALUE, 1, 0, 88, 41, 2, 0, 88, -22, 3, 0, -111, 42, 1, 0, -7, 34, 5, 0, -7, 35, 9, 0, -7, -29, 3, 9, -86, 34, 1, 0, 88, 34, 13, 0, -7, -30, 3, 19, -86, -119, 0, 0, 88, 32, 1, 31, -42, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        writeLong(j, ByteOrder.LITTLE_ENDIAN, bArr, bArr.length - 32);
        writeLong(j2, ByteOrder.LITTLE_ENDIAN, bArr, bArr.length - 24);
        writeLong(j3, ByteOrder.LITTLE_ENDIAN, bArr, bArr.length - 16);
        writeLong(j4, ByteOrder.LITTLE_ENDIAN, bArr, bArr.length - 8);
        return bArr;
    }
}
