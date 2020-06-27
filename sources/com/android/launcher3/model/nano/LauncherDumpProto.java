package com.android.launcher3.model.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface LauncherDumpProto {

    public interface ContainerType {
        public static final int FOLDER = 3;
        public static final int HOTSEAT = 2;
        public static final int UNKNOWN_CONTAINERTYPE = 0;
        public static final int WORKSPACE = 1;
    }

    public static final class DumpTarget extends MessageNano {
        private static volatile DumpTarget[] _emptyArray;
        public String component;
        public int containerType;
        public int gridX;
        public int gridY;
        public String itemId;
        public int itemType;
        public String packageName;
        public int pageId;
        public int spanX;
        public int spanY;
        public int type;
        public int userType;

        public interface Type {
            public static final int CONTAINER = 2;
            public static final int ITEM = 1;
            public static final int NONE = 0;
        }

        public static DumpTarget[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new DumpTarget[0];
                    }
                }
            }
            return _emptyArray;
        }

        public DumpTarget() {
            clear();
        }

        public DumpTarget clear() {
            this.type = 0;
            this.pageId = 0;
            this.gridX = 0;
            this.gridY = 0;
            this.containerType = 0;
            this.itemType = 0;
            this.packageName = "";
            this.component = "";
            this.itemId = "";
            this.spanX = 1;
            this.spanY = 1;
            this.userType = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.type != 0) {
                codedOutputByteBufferNano.writeInt32(1, this.type);
            }
            if (this.pageId != 0) {
                codedOutputByteBufferNano.writeInt32(2, this.pageId);
            }
            if (this.gridX != 0) {
                codedOutputByteBufferNano.writeInt32(3, this.gridX);
            }
            if (this.gridY != 0) {
                codedOutputByteBufferNano.writeInt32(4, this.gridY);
            }
            if (this.containerType != 0) {
                codedOutputByteBufferNano.writeInt32(5, this.containerType);
            }
            if (this.itemType != 0) {
                codedOutputByteBufferNano.writeInt32(6, this.itemType);
            }
            if (!this.packageName.equals("")) {
                codedOutputByteBufferNano.writeString(7, this.packageName);
            }
            if (!this.component.equals("")) {
                codedOutputByteBufferNano.writeString(8, this.component);
            }
            if (!this.itemId.equals("")) {
                codedOutputByteBufferNano.writeString(9, this.itemId);
            }
            if (this.spanX != 1) {
                codedOutputByteBufferNano.writeInt32(10, this.spanX);
            }
            if (this.spanY != 1) {
                codedOutputByteBufferNano.writeInt32(11, this.spanY);
            }
            if (this.userType != 0) {
                codedOutputByteBufferNano.writeInt32(12, this.userType);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.type != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.type);
            }
            if (this.pageId != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.pageId);
            }
            if (this.gridX != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.gridX);
            }
            if (this.gridY != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, this.gridY);
            }
            if (this.containerType != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, this.containerType);
            }
            if (this.itemType != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, this.itemType);
            }
            if (!this.packageName.equals("")) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(7, this.packageName);
            }
            if (!this.component.equals("")) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(8, this.component);
            }
            if (!this.itemId.equals("")) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(9, this.itemId);
            }
            if (this.spanX != 1) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, this.spanX);
            }
            if (this.spanY != 1) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, this.spanY);
            }
            return this.userType != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(12, this.userType) : computeSerializedSize;
        }

        public DumpTarget mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 8:
                        int readInt32 = codedInputByteBufferNano.readInt32();
                        switch (readInt32) {
                            case 0:
                            case 1:
                            case 2:
                                this.type = readInt32;
                                break;
                        }
                    case 16:
                        this.pageId = codedInputByteBufferNano.readInt32();
                        break;
                    case 24:
                        this.gridX = codedInputByteBufferNano.readInt32();
                        break;
                    case 32:
                        this.gridY = codedInputByteBufferNano.readInt32();
                        break;
                    case 40:
                        int readInt322 = codedInputByteBufferNano.readInt32();
                        switch (readInt322) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                this.containerType = readInt322;
                                break;
                        }
                    case 48:
                        int readInt323 = codedInputByteBufferNano.readInt32();
                        switch (readInt323) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                                this.itemType = readInt323;
                                break;
                        }
                    case 58:
                        this.packageName = codedInputByteBufferNano.readString();
                        break;
                    case 66:
                        this.component = codedInputByteBufferNano.readString();
                        break;
                    case 74:
                        this.itemId = codedInputByteBufferNano.readString();
                        break;
                    case 80:
                        this.spanX = codedInputByteBufferNano.readInt32();
                        break;
                    case 88:
                        this.spanY = codedInputByteBufferNano.readInt32();
                        break;
                    case 96:
                        int readInt324 = codedInputByteBufferNano.readInt32();
                        switch (readInt324) {
                            case 0:
                            case 1:
                                this.userType = readInt324;
                                break;
                        }
                    default:
                        if (WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                            break;
                        } else {
                            return this;
                        }
                }
            }
        }

        public static DumpTarget parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (DumpTarget) MessageNano.mergeFrom(new DumpTarget(), bArr);
        }

        public static DumpTarget parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new DumpTarget().mergeFrom(codedInputByteBufferNano);
        }
    }

    public interface ItemType {
        public static final int APP_ICON = 1;
        public static final int SHORTCUT = 3;
        public static final int UNKNOWN_ITEMTYPE = 0;
        public static final int WIDGET = 2;
    }

    public static final class LauncherImpression extends MessageNano {
        private static volatile LauncherImpression[] _emptyArray;
        public DumpTarget[] targets;

        public static LauncherImpression[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new LauncherImpression[0];
                    }
                }
            }
            return _emptyArray;
        }

        public LauncherImpression() {
            clear();
        }

        public LauncherImpression clear() {
            this.targets = DumpTarget.emptyArray();
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.targets != null && this.targets.length > 0) {
                for (DumpTarget dumpTarget : this.targets) {
                    if (dumpTarget != null) {
                        codedOutputByteBufferNano.writeMessage(1, dumpTarget);
                    }
                }
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.targets != null && this.targets.length > 0) {
                for (DumpTarget dumpTarget : this.targets) {
                    if (dumpTarget != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, dumpTarget);
                    }
                }
            }
            return computeSerializedSize;
        }

        public LauncherImpression mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 10) {
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 10);
                    int length = this.targets == null ? 0 : this.targets.length;
                    DumpTarget[] dumpTargetArr = new DumpTarget[(repeatedFieldArrayLength + length)];
                    if (length != 0) {
                        System.arraycopy(this.targets, 0, dumpTargetArr, 0, length);
                    }
                    while (length < dumpTargetArr.length - 1) {
                        dumpTargetArr[length] = new DumpTarget();
                        codedInputByteBufferNano.readMessage(dumpTargetArr[length]);
                        codedInputByteBufferNano.readTag();
                        length++;
                    }
                    dumpTargetArr[length] = new DumpTarget();
                    codedInputByteBufferNano.readMessage(dumpTargetArr[length]);
                    this.targets = dumpTargetArr;
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            }
        }

        public static LauncherImpression parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (LauncherImpression) MessageNano.mergeFrom(new LauncherImpression(), bArr);
        }

        public static LauncherImpression parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new LauncherImpression().mergeFrom(codedInputByteBufferNano);
        }
    }

    public interface UserType {
        public static final int DEFAULT = 0;
        public static final int WORK = 1;
    }
}
