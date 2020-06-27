package com.android.launcher3.userevent.nano;

import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.userevent.nano.LauncherLogExtensions.LauncherEventExtension;
import com.android.launcher3.userevent.nano.LauncherLogExtensions.TargetExtension;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface LauncherLogProto {

    public static final class Action extends MessageNano {
        private static volatile Action[] _emptyArray;
        public int command;
        public int dir;
        public boolean isOutside;
        public int touch;
        public int type;

        public interface Command {
            public static final int BACK = 1;
            public static final int CANCEL = 3;
            public static final int CONFIRM = 4;
            public static final int ENTRY = 2;
            public static final int HOME_INTENT = 0;
        }

        public interface Direction {
            public static final int DOWN = 2;
            public static final int LEFT = 3;
            public static final int NONE = 0;
            public static final int RIGHT = 4;

            /* renamed from: UP */
            public static final int f67UP = 1;
        }

        public interface Touch {
            public static final int DRAGDROP = 2;
            public static final int FLING = 4;
            public static final int LONGPRESS = 1;
            public static final int PINCH = 5;
            public static final int SWIPE = 3;
            public static final int TAP = 0;
        }

        public interface Type {
            public static final int AUTOMATED = 1;
            public static final int COMMAND = 2;
            public static final int TOUCH = 0;
        }

        public static Action[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Action[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Action() {
            clear();
        }

        public Action clear() {
            this.type = 0;
            this.touch = 0;
            this.dir = 0;
            this.command = 0;
            this.isOutside = false;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.type != 0) {
                codedOutputByteBufferNano.writeInt32(1, this.type);
            }
            if (this.touch != 0) {
                codedOutputByteBufferNano.writeInt32(2, this.touch);
            }
            if (this.dir != 0) {
                codedOutputByteBufferNano.writeInt32(3, this.dir);
            }
            if (this.command != 0) {
                codedOutputByteBufferNano.writeInt32(4, this.command);
            }
            if (this.isOutside) {
                codedOutputByteBufferNano.writeBool(5, this.isOutside);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.type != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.type);
            }
            if (this.touch != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.touch);
            }
            if (this.dir != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.dir);
            }
            if (this.command != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, this.command);
            }
            return this.isOutside ? computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(5, this.isOutside) : computeSerializedSize;
        }

        public Action mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag != 0) {
                    if (readTag != 8) {
                        if (readTag != 16) {
                            if (readTag != 24) {
                                if (readTag == 32) {
                                    int readInt32 = codedInputByteBufferNano.readInt32();
                                    switch (readInt32) {
                                        case 0:
                                        case 1:
                                        case 2:
                                        case 3:
                                        case 4:
                                            this.command = readInt32;
                                            break;
                                    }
                                } else if (readTag == 40) {
                                    this.isOutside = codedInputByteBufferNano.readBool();
                                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                                    return this;
                                }
                            } else {
                                int readInt322 = codedInputByteBufferNano.readInt32();
                                switch (readInt322) {
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4:
                                        this.dir = readInt322;
                                        break;
                                }
                            }
                        } else {
                            int readInt323 = codedInputByteBufferNano.readInt32();
                            switch (readInt323) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                    this.touch = readInt323;
                                    break;
                            }
                        }
                    } else {
                        int readInt324 = codedInputByteBufferNano.readInt32();
                        switch (readInt324) {
                            case 0:
                            case 1:
                            case 2:
                                this.type = readInt324;
                                break;
                        }
                    }
                } else {
                    return this;
                }
            }
        }

        public static Action parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (Action) MessageNano.mergeFrom(new Action(), bArr);
        }

        public static Action parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new Action().mergeFrom(codedInputByteBufferNano);
        }
    }

    public interface ContainerType {
        public static final int ALLAPPS = 4;
        public static final int DEEPSHORTCUTS = 9;
        public static final int DEFAULT_CONTAINERTYPE = 0;
        public static final int FOLDER = 3;
        public static final int HOTSEAT = 2;
        public static final int OVERVIEW = 6;
        public static final int PINITEM = 10;
        public static final int PREDICTION = 7;
        public static final int SEARCHRESULT = 8;
        public static final int WIDGETS = 5;
        public static final int WORKSPACE = 1;
    }

    public interface ControlType {
        public static final int ALL_APPS_BUTTON = 1;
        public static final int APPINFO_TARGET = 7;
        public static final int BACK_BUTTON = 11;
        public static final int DEFAULT_CONTROLTYPE = 0;
        public static final int HOME_INTENT = 10;
        public static final int REMOVE_TARGET = 5;
        public static final int RESIZE_HANDLE = 8;
        public static final int SETTINGS_BUTTON = 4;
        public static final int UNINSTALL_TARGET = 6;
        public static final int VERTICAL_SCROLL = 9;
        public static final int WALLPAPER_BUTTON = 3;
        public static final int WIDGETS_BUTTON = 2;
    }

    public interface ItemType {
        public static final int APP_ICON = 1;
        public static final int DEEPSHORTCUT = 5;
        public static final int DEFAULT_ITEMTYPE = 0;
        public static final int EDITTEXT = 7;
        public static final int FOLDER_ICON = 4;
        public static final int NOTIFICATION = 8;
        public static final int SEARCHBOX = 6;
        public static final int SHORTCUT = 2;
        public static final int WIDGET = 3;
    }

    public static final class LauncherEvent extends MessageNano {
        private static volatile LauncherEvent[] _emptyArray;
        public Action action;
        public long actionDurationMillis;
        public Target[] destTarget;
        public long elapsedContainerMillis;
        public long elapsedSessionMillis;
        public LauncherEventExtension extension;
        public boolean isInLandscapeMode;
        public boolean isInMultiWindowMode;
        public Target[] srcTarget;

        public static LauncherEvent[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new LauncherEvent[0];
                    }
                }
            }
            return _emptyArray;
        }

        public LauncherEvent() {
            clear();
        }

        public LauncherEvent clear() {
            this.action = null;
            this.srcTarget = Target.emptyArray();
            this.destTarget = Target.emptyArray();
            this.actionDurationMillis = 0;
            this.elapsedContainerMillis = 0;
            this.elapsedSessionMillis = 0;
            this.isInMultiWindowMode = false;
            this.isInLandscapeMode = false;
            this.extension = null;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.action != null) {
                codedOutputByteBufferNano.writeMessage(1, this.action);
            }
            if (this.srcTarget != null && this.srcTarget.length > 0) {
                for (Target target : this.srcTarget) {
                    if (target != null) {
                        codedOutputByteBufferNano.writeMessage(2, target);
                    }
                }
            }
            if (this.destTarget != null && this.destTarget.length > 0) {
                for (Target target2 : this.destTarget) {
                    if (target2 != null) {
                        codedOutputByteBufferNano.writeMessage(3, target2);
                    }
                }
            }
            if (this.actionDurationMillis != 0) {
                codedOutputByteBufferNano.writeInt64(4, this.actionDurationMillis);
            }
            if (this.elapsedContainerMillis != 0) {
                codedOutputByteBufferNano.writeInt64(5, this.elapsedContainerMillis);
            }
            if (this.elapsedSessionMillis != 0) {
                codedOutputByteBufferNano.writeInt64(6, this.elapsedSessionMillis);
            }
            if (this.isInMultiWindowMode) {
                codedOutputByteBufferNano.writeBool(7, this.isInMultiWindowMode);
            }
            if (this.isInLandscapeMode) {
                codedOutputByteBufferNano.writeBool(8, this.isInLandscapeMode);
            }
            if (this.extension != null) {
                codedOutputByteBufferNano.writeMessage(9, this.extension);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.action != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.action);
            }
            if (this.srcTarget != null && this.srcTarget.length > 0) {
                int i = computeSerializedSize;
                for (Target target : this.srcTarget) {
                    if (target != null) {
                        i += CodedOutputByteBufferNano.computeMessageSize(2, target);
                    }
                }
                computeSerializedSize = i;
            }
            if (this.destTarget != null && this.destTarget.length > 0) {
                for (Target target2 : this.destTarget) {
                    if (target2 != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, target2);
                    }
                }
            }
            if (this.actionDurationMillis != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(4, this.actionDurationMillis);
            }
            if (this.elapsedContainerMillis != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(5, this.elapsedContainerMillis);
            }
            if (this.elapsedSessionMillis != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(6, this.elapsedSessionMillis);
            }
            if (this.isInMultiWindowMode) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(7, this.isInMultiWindowMode);
            }
            if (this.isInLandscapeMode) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(8, this.isInLandscapeMode);
            }
            return this.extension != null ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(9, this.extension) : computeSerializedSize;
        }

        public LauncherEvent mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 10) {
                    if (this.action == null) {
                        this.action = new Action();
                    }
                    codedInputByteBufferNano.readMessage(this.action);
                } else if (readTag == 18) {
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                    int length = this.srcTarget == null ? 0 : this.srcTarget.length;
                    Target[] targetArr = new Target[(repeatedFieldArrayLength + length)];
                    if (length != 0) {
                        System.arraycopy(this.srcTarget, 0, targetArr, 0, length);
                    }
                    while (length < targetArr.length - 1) {
                        targetArr[length] = new Target();
                        codedInputByteBufferNano.readMessage(targetArr[length]);
                        codedInputByteBufferNano.readTag();
                        length++;
                    }
                    targetArr[length] = new Target();
                    codedInputByteBufferNano.readMessage(targetArr[length]);
                    this.srcTarget = targetArr;
                } else if (readTag == 26) {
                    int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 26);
                    int length2 = this.destTarget == null ? 0 : this.destTarget.length;
                    Target[] targetArr2 = new Target[(repeatedFieldArrayLength2 + length2)];
                    if (length2 != 0) {
                        System.arraycopy(this.destTarget, 0, targetArr2, 0, length2);
                    }
                    while (length2 < targetArr2.length - 1) {
                        targetArr2[length2] = new Target();
                        codedInputByteBufferNano.readMessage(targetArr2[length2]);
                        codedInputByteBufferNano.readTag();
                        length2++;
                    }
                    targetArr2[length2] = new Target();
                    codedInputByteBufferNano.readMessage(targetArr2[length2]);
                    this.destTarget = targetArr2;
                } else if (readTag == 32) {
                    this.actionDurationMillis = codedInputByteBufferNano.readInt64();
                } else if (readTag == 40) {
                    this.elapsedContainerMillis = codedInputByteBufferNano.readInt64();
                } else if (readTag == 48) {
                    this.elapsedSessionMillis = codedInputByteBufferNano.readInt64();
                } else if (readTag == 56) {
                    this.isInMultiWindowMode = codedInputByteBufferNano.readBool();
                } else if (readTag == 64) {
                    this.isInLandscapeMode = codedInputByteBufferNano.readBool();
                } else if (readTag == 74) {
                    if (this.extension == null) {
                        this.extension = new LauncherEventExtension();
                    }
                    codedInputByteBufferNano.readMessage(this.extension);
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            }
        }

        public static LauncherEvent parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (LauncherEvent) MessageNano.mergeFrom(new LauncherEvent(), bArr);
        }

        public static LauncherEvent parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new LauncherEvent().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class Target extends MessageNano {
        private static volatile Target[] _emptyArray;
        public int cardinality;
        public int componentHash;
        public int containerType;
        public int controlType;
        public TargetExtension extension;
        public int gridX;
        public int gridY;
        public int intentHash;
        public int itemType;
        public int packageNameHash;
        public int pageIndex;
        public int predictedRank;
        public int rank;
        public int spanX;
        public int spanY;
        public int type;

        public interface Type {
            public static final int CONTAINER = 3;
            public static final int CONTROL = 2;
            public static final int ITEM = 1;
            public static final int NONE = 0;
        }

        public static Target[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Target[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Target() {
            clear();
        }

        public Target clear() {
            this.type = 0;
            this.pageIndex = 0;
            this.rank = 0;
            this.gridX = 0;
            this.gridY = 0;
            this.containerType = 0;
            this.cardinality = 0;
            this.controlType = 0;
            this.itemType = 0;
            this.packageNameHash = 0;
            this.componentHash = 0;
            this.intentHash = 0;
            this.spanX = 1;
            this.spanY = 1;
            this.predictedRank = 0;
            this.extension = null;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.type != 0) {
                codedOutputByteBufferNano.writeInt32(1, this.type);
            }
            if (this.pageIndex != 0) {
                codedOutputByteBufferNano.writeInt32(2, this.pageIndex);
            }
            if (this.rank != 0) {
                codedOutputByteBufferNano.writeInt32(3, this.rank);
            }
            if (this.gridX != 0) {
                codedOutputByteBufferNano.writeInt32(4, this.gridX);
            }
            if (this.gridY != 0) {
                codedOutputByteBufferNano.writeInt32(5, this.gridY);
            }
            if (this.containerType != 0) {
                codedOutputByteBufferNano.writeInt32(6, this.containerType);
            }
            if (this.cardinality != 0) {
                codedOutputByteBufferNano.writeInt32(7, this.cardinality);
            }
            if (this.controlType != 0) {
                codedOutputByteBufferNano.writeInt32(8, this.controlType);
            }
            if (this.itemType != 0) {
                codedOutputByteBufferNano.writeInt32(9, this.itemType);
            }
            if (this.packageNameHash != 0) {
                codedOutputByteBufferNano.writeInt32(10, this.packageNameHash);
            }
            if (this.componentHash != 0) {
                codedOutputByteBufferNano.writeInt32(11, this.componentHash);
            }
            if (this.intentHash != 0) {
                codedOutputByteBufferNano.writeInt32(12, this.intentHash);
            }
            if (this.spanX != 1) {
                codedOutputByteBufferNano.writeInt32(13, this.spanX);
            }
            if (this.spanY != 1) {
                codedOutputByteBufferNano.writeInt32(14, this.spanY);
            }
            if (this.predictedRank != 0) {
                codedOutputByteBufferNano.writeInt32(15, this.predictedRank);
            }
            if (this.extension != null) {
                codedOutputByteBufferNano.writeMessage(16, this.extension);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.type != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.type);
            }
            if (this.pageIndex != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.pageIndex);
            }
            if (this.rank != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.rank);
            }
            if (this.gridX != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, this.gridX);
            }
            if (this.gridY != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, this.gridY);
            }
            if (this.containerType != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, this.containerType);
            }
            if (this.cardinality != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, this.cardinality);
            }
            if (this.controlType != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, this.controlType);
            }
            if (this.itemType != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, this.itemType);
            }
            if (this.packageNameHash != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, this.packageNameHash);
            }
            if (this.componentHash != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, this.componentHash);
            }
            if (this.intentHash != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(12, this.intentHash);
            }
            if (this.spanX != 1) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(13, this.spanX);
            }
            if (this.spanY != 1) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(14, this.spanY);
            }
            if (this.predictedRank != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(15, this.predictedRank);
            }
            return this.extension != null ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(16, this.extension) : computeSerializedSize;
        }

        public Target mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
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
                            case 3:
                                this.type = readInt32;
                                break;
                        }
                    case 16:
                        this.pageIndex = codedInputByteBufferNano.readInt32();
                        break;
                    case 24:
                        this.rank = codedInputByteBufferNano.readInt32();
                        break;
                    case 32:
                        this.gridX = codedInputByteBufferNano.readInt32();
                        break;
                    case 40:
                        this.gridY = codedInputByteBufferNano.readInt32();
                        break;
                    case 48:
                        int readInt322 = codedInputByteBufferNano.readInt32();
                        switch (readInt322) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                                this.containerType = readInt322;
                                break;
                        }
                    case 56:
                        this.cardinality = codedInputByteBufferNano.readInt32();
                        break;
                    case 64:
                        int readInt323 = codedInputByteBufferNano.readInt32();
                        switch (readInt323) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                                this.controlType = readInt323;
                                break;
                        }
                    case 72:
                        int readInt324 = codedInputByteBufferNano.readInt32();
                        switch (readInt324) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                                this.itemType = readInt324;
                                break;
                        }
                    case 80:
                        this.packageNameHash = codedInputByteBufferNano.readInt32();
                        break;
                    case 88:
                        this.componentHash = codedInputByteBufferNano.readInt32();
                        break;
                    case 96:
                        this.intentHash = codedInputByteBufferNano.readInt32();
                        break;
                    case 104:
                        this.spanX = codedInputByteBufferNano.readInt32();
                        break;
                    case 112:
                        this.spanY = codedInputByteBufferNano.readInt32();
                        break;
                    case DragView.COLOR_CHANGE_DURATION /*120*/:
                        this.predictedRank = codedInputByteBufferNano.readInt32();
                        break;
                    case 130:
                        if (this.extension == null) {
                            this.extension = new TargetExtension();
                        }
                        codedInputByteBufferNano.readMessage(this.extension);
                        break;
                    default:
                        if (WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                            break;
                        } else {
                            return this;
                        }
                }
            }
        }

        public static Target parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (Target) MessageNano.mergeFrom(new Target(), bArr);
        }

        public static Target parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new Target().mergeFrom(codedInputByteBufferNano);
        }
    }
}
