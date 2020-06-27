package p015me.weishu.epic.art.arch;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* renamed from: me.weishu.epic.art.arch.ShellCode */
public abstract class ShellCode {
    public abstract byte[] createDirectJump(long j);

    public abstract String getName();

    public abstract int sizeOfBridgeJump();

    public abstract int sizeOfDirectJump();

    public abstract long toMem(long j);

    public abstract long toPC(long j);

    public byte[] createCallOrigin(long j, byte[] bArr) {
        byte[] bArr2 = new byte[sizeOfCallOrigin()];
        System.arraycopy(bArr, 0, bArr2, 0, sizeOfDirectJump());
        byte[] createDirectJump = createDirectJump(toPC(j + ((long) sizeOfDirectJump())));
        System.arraycopy(createDirectJump, 0, bArr2, sizeOfDirectJump(), createDirectJump.length);
        return bArr2;
    }

    public int sizeOfCallOrigin() {
        return sizeOfDirectJump() * 2;
    }

    public byte[] createBridgeJump(long j, long j2, long j3, long j4) {
        throw new RuntimeException("not impled");
    }

    static void writeInt(int i, ByteOrder byteOrder, byte[] bArr, int i2) {
        System.arraycopy(ByteBuffer.allocate(4).order(byteOrder).putInt(i).array(), 0, bArr, i2, 4);
    }

    static void writeLong(long j, ByteOrder byteOrder, byte[] bArr, int i) {
        System.arraycopy(ByteBuffer.allocate(8).order(byteOrder).putLong(j).array(), 0, bArr, i, 8);
    }
}
