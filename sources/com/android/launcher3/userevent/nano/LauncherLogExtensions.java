package com.android.launcher3.userevent.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface LauncherLogExtensions {

    public static final class LauncherEventExtension extends MessageNano {
        private static volatile LauncherEventExtension[] _emptyArray;

        public static LauncherEventExtension[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new LauncherEventExtension[0];
                    }
                }
            }
            return _emptyArray;
        }

        public LauncherEventExtension() {
            clear();
        }

        public LauncherEventExtension clear() {
            this.cachedSize = -1;
            return this;
        }

        public LauncherEventExtension mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            int readTag;
            do {
                readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
            } while (WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag));
            return this;
        }

        public static LauncherEventExtension parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (LauncherEventExtension) MessageNano.mergeFrom(new LauncherEventExtension(), bArr);
        }

        public static LauncherEventExtension parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new LauncherEventExtension().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class TargetExtension extends MessageNano {
        private static volatile TargetExtension[] _emptyArray;

        public static TargetExtension[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new TargetExtension[0];
                    }
                }
            }
            return _emptyArray;
        }

        public TargetExtension() {
            clear();
        }

        public TargetExtension clear() {
            this.cachedSize = -1;
            return this;
        }

        public TargetExtension mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            int readTag;
            do {
                readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
            } while (WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag));
            return this;
        }

        public static TargetExtension parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (TargetExtension) MessageNano.mergeFrom(new TargetExtension(), bArr);
        }

        public static TargetExtension parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new TargetExtension().mergeFrom(codedInputByteBufferNano);
        }
    }
}
