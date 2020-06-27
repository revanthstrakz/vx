package p015me.weishu.epic.art.arch;

import java.nio.ByteOrder;

/* renamed from: me.weishu.epic.art.arch.Thumb2 */
public class Thumb2 extends ShellCode {
    public String getName() {
        return "Thumb2";
    }

    public int sizeOfBridgeJump() {
        return 60;
    }

    public int sizeOfDirectJump() {
        return 12;
    }

    public long toMem(long j) {
        return j & -2;
    }

    public byte[] createDirectJump(long j) {
        byte[] bArr = {-33, -8, 0, -16, 0, 0, 0, 0};
        writeInt((int) j, ByteOrder.LITTLE_ENDIAN, bArr, bArr.length - 4);
        return bArr;
    }

    public byte[] createBridgeJump(long j, long j2, long j3, long j4) {
        byte[] bArr = {-33, -8, 48, -64, 96, 69, 64, -16, 25, Byte.MIN_VALUE, 8, 72, -33, -8, 40, -64, -52, -8, 0, -48, -52, -8, 4, 32, -52, -8, 8, 48, 99, 70, 5, 74, -52, -8, 12, 32, 74, 70, 74, 70, -33, -8, 4, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        writeInt((int) j, ByteOrder.LITTLE_ENDIAN, bArr, bArr.length - 16);
        writeInt((int) j2, ByteOrder.LITTLE_ENDIAN, bArr, bArr.length - 12);
        writeInt((int) j3, ByteOrder.LITTLE_ENDIAN, bArr, bArr.length - 8);
        writeInt((int) j4, ByteOrder.LITTLE_ENDIAN, bArr, bArr.length - 4);
        return bArr;
    }

    public long toPC(long j) {
        return toMem(j) + 1;
    }
}
