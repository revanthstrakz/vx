package p015me.weishu.epic.art;

import com.taobao.android.dexposed.utility.Debug;
import com.taobao.android.dexposed.utility.Logger;
import com.taobao.android.dexposed.utility.Runtime;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import p015me.weishu.epic.art.arch.ShellCode;
import p015me.weishu.epic.art.entry.Entry;
import p015me.weishu.epic.art.entry.Entry64;
import p015me.weishu.epic.art.method.ArtMethod;

/* renamed from: me.weishu.epic.art.Trampoline */
class Trampoline {
    private static final String TAG = "Trampoline";
    private boolean active;
    private final long jumpToAddress;
    private final byte[] originalCode;
    private Set<ArtMethod> segments = new HashSet();
    private final ShellCode shellCode;
    private long trampolineAddress;
    private int trampolineSize;

    Trampoline(ShellCode shellCode2, long j) {
        this.shellCode = shellCode2;
        this.jumpToAddress = shellCode2.toMem(j);
        this.originalCode = EpicNative.get(this.jumpToAddress, shellCode2.sizeOfDirectJump());
    }

    public boolean install(ArtMethod artMethod) {
        if (!this.segments.add(artMethod)) {
            String str = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(artMethod);
            sb.append(" is already hooked, return.");
            Logger.m92d(str, sb.toString());
            return true;
        }
        EpicNative.put(create(), getTrampolineAddress());
        int quickCompiledCodeSize = Epic.getQuickCompiledCodeSize(artMethod);
        if (quickCompiledCodeSize >= this.shellCode.sizeOfDirectJump()) {
            return activate();
        }
        String str2 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(artMethod.toGenericString());
        sb2.append(" quickCompiledCodeSize: ");
        sb2.append(quickCompiledCodeSize);
        Logger.m96w(str2, sb2.toString());
        artMethod.setEntryPointFromQuickCompiledCode(getTrampolinePc());
        return true;
    }

    private long getTrampolineAddress() {
        if (getSize() != this.trampolineSize) {
            alloc();
        }
        return this.trampolineAddress;
    }

    private long getTrampolinePc() {
        return this.shellCode.toPC(getTrampolineAddress());
    }

    private void alloc() {
        if (this.trampolineAddress != 0) {
            free();
        }
        this.trampolineSize = getSize();
        this.trampolineAddress = EpicNative.map(this.trampolineSize);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Trampoline alloc:");
        sb.append(this.trampolineSize);
        sb.append(", addr: 0x");
        sb.append(Long.toHexString(this.trampolineAddress));
        Logger.m92d(str, sb.toString());
    }

    private void free() {
        if (this.trampolineAddress != 0) {
            EpicNative.unmap(this.trampolineAddress, this.trampolineSize);
            this.trampolineAddress = 0;
            this.trampolineSize = 0;
        }
        if (this.active) {
            EpicNative.put(this.originalCode, this.jumpToAddress);
        }
    }

    private int getSize() {
        return (this.shellCode.sizeOfBridgeJump() * this.segments.size()) + 0 + this.shellCode.sizeOfCallOrigin();
    }

    private byte[] create() {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("create trampoline.");
        sb.append(this.segments);
        Logger.m92d(str, sb.toString());
        byte[] bArr = new byte[getSize()];
        int i = 0;
        for (ArtMethod createTrampoline : this.segments) {
            byte[] createTrampoline2 = createTrampoline(createTrampoline);
            int length = createTrampoline2.length;
            System.arraycopy(createTrampoline2, 0, bArr, i, length);
            i += length;
        }
        byte[] createCallOrigin = this.shellCode.createCallOrigin(this.jumpToAddress, this.originalCode);
        System.arraycopy(createCallOrigin, 0, bArr, i, createCallOrigin.length);
        return bArr;
    }

    private boolean activate() {
        boolean activateNative;
        long trampolinePc = getTrampolinePc();
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Writing direct jump entry ");
        sb.append(Debug.addrHex(trampolinePc));
        sb.append(" to origin entry: 0x");
        sb.append(Debug.addrHex(this.jumpToAddress));
        Logger.m92d(str, sb.toString());
        synchronized (Trampoline.class) {
            activateNative = EpicNative.activateNative(this.jumpToAddress, trampolinePc, (long) this.shellCode.sizeOfDirectJump(), (long) this.shellCode.sizeOfBridgeJump(), this.shellCode.createDirectJump(trampolinePc));
        }
        return activateNative;
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        free();
        super.finalize();
    }

    private byte[] createTrampoline(ArtMethod artMethod) {
        Method method;
        Class<?> cls = Epic.getMethodInfo(artMethod.getAddress()).returnType;
        if (Runtime.is64Bit()) {
            method = Entry64.getBridgeMethod(cls);
        } else {
            method = Entry.getBridgeMethod(cls);
        }
        ArtMethod of = ArtMethod.m105of(method);
        long address = of.getAddress();
        long entryPointFromQuickCompiledCode = of.getEntryPointFromQuickCompiledCode();
        long address2 = artMethod.getAddress();
        long malloc = EpicNative.malloc(4);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("targetAddress:");
        sb.append(Debug.longHex(address));
        Logger.m92d(str, sb.toString());
        String str2 = TAG;
        StringBuilder sb2 = new StringBuilder();
        sb2.append("sourceAddress:");
        sb2.append(Debug.longHex(address2));
        Logger.m92d(str2, sb2.toString());
        String str3 = TAG;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("targetEntry:");
        sb3.append(Debug.longHex(entryPointFromQuickCompiledCode));
        Logger.m92d(str3, sb3.toString());
        String str4 = TAG;
        StringBuilder sb4 = new StringBuilder();
        sb4.append("structAddress:");
        sb4.append(Debug.longHex(malloc));
        Logger.m92d(str4, sb4.toString());
        return this.shellCode.createBridgeJump(address, entryPointFromQuickCompiledCode, address2, malloc);
    }
}
