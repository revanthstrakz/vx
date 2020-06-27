package com.google.android.apps.nexuslauncher.search.nano;

import com.android.launcher3.dragndrop.DragView;
import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface SearchProto {

    public static final class a_search extends MessageNano {
        private static volatile a_search[] _emptyArray;

        /* renamed from: ee */
        public int f92ee;

        /* renamed from: ef */
        public int f93ef;

        /* renamed from: eg */
        public int f94eg;

        /* renamed from: eh */
        public int f95eh;

        public static a_search[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new a_search[0];
                    }
                }
            }
            return _emptyArray;
        }

        public a_search() {
            clear();
        }

        public a_search clear() {
            this.f93ef = 0;
            this.f94eg = 0;
            this.f95eh = 0;
            this.f92ee = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.f93ef != 0) {
                codedOutputByteBufferNano.writeInt32(1, this.f93ef);
            }
            if (this.f94eg != 0) {
                codedOutputByteBufferNano.writeInt32(2, this.f94eg);
            }
            if (this.f95eh != 0) {
                codedOutputByteBufferNano.writeInt32(3, this.f95eh);
            }
            if (this.f92ee != 0) {
                codedOutputByteBufferNano.writeInt32(4, this.f92ee);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.f93ef != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.f93ef);
            }
            if (this.f94eg != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.f94eg);
            }
            if (this.f95eh != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.f95eh);
            }
            return this.f92ee != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(4, this.f92ee) : computeSerializedSize;
        }

        public a_search mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 8) {
                    this.f93ef = codedInputByteBufferNano.readInt32();
                } else if (readTag == 16) {
                    this.f94eg = codedInputByteBufferNano.readInt32();
                } else if (readTag == 24) {
                    this.f95eh = codedInputByteBufferNano.readInt32();
                } else if (readTag == 32) {
                    this.f92ee = codedInputByteBufferNano.readInt32();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            }
        }

        public static a_search parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (a_search) MessageNano.mergeFrom(new a_search(), bArr);
        }

        public static a_search parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new a_search().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class b_search extends MessageNano {
        private static volatile b_search[] _emptyArray;

        /* renamed from: ej */
        public String f96ej;

        /* renamed from: ek */
        public String f97ek;

        /* renamed from: el */
        public String f98el;
        public String label;

        public static b_search[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new b_search[0];
                    }
                }
            }
            return _emptyArray;
        }

        public b_search() {
            clear();
        }

        public b_search clear() {
            this.label = "";
            this.f97ek = "";
            this.f96ej = "";
            this.f98el = "";
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (!this.label.equals("")) {
                codedOutputByteBufferNano.writeString(1, this.label);
            }
            if (!this.f97ek.equals("")) {
                codedOutputByteBufferNano.writeString(2, this.f97ek);
            }
            if (!this.f96ej.equals("")) {
                codedOutputByteBufferNano.writeString(3, this.f96ej);
            }
            if (!this.f98el.equals("")) {
                codedOutputByteBufferNano.writeString(4, this.f98el);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (!this.label.equals("")) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.label);
            }
            if (!this.f97ek.equals("")) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.f97ek);
            }
            if (!this.f96ej.equals("")) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.f96ej);
            }
            return !this.f98el.equals("") ? computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(4, this.f98el) : computeSerializedSize;
        }

        public b_search mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 10) {
                    this.label = codedInputByteBufferNano.readString();
                } else if (readTag == 18) {
                    this.f97ek = codedInputByteBufferNano.readString();
                } else if (readTag == 26) {
                    this.f96ej = codedInputByteBufferNano.readString();
                } else if (readTag == 34) {
                    this.f98el = codedInputByteBufferNano.readString();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            }
        }

        public static b_search parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (b_search) MessageNano.mergeFrom(new b_search(), bArr);
        }

        public static b_search parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new b_search().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class c_search extends MessageNano {
        private static volatile c_search[] _emptyArray;

        /* renamed from: eA */
        public boolean f99eA;

        /* renamed from: em */
        public int f100em;

        /* renamed from: en */
        public a_search f101en;

        /* renamed from: eo */
        public b_search[] f102eo;

        /* renamed from: ep */
        public String f103ep;

        /* renamed from: eq */
        public boolean f104eq;

        /* renamed from: er */
        public String f105er;

        /* renamed from: es */
        public int f106es;

        /* renamed from: et */
        public a_search f107et;

        /* renamed from: eu */
        public String f108eu;

        /* renamed from: ev */
        public a_search f109ev;

        /* renamed from: ew */
        public int f110ew;

        /* renamed from: ex */
        public int f111ex;

        /* renamed from: ey */
        public String f112ey;

        /* renamed from: ez */
        public a_search f113ez;

        public static c_search[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new c_search[0];
                    }
                }
            }
            return _emptyArray;
        }

        public c_search() {
            clear();
        }

        public c_search clear() {
            this.f100em = 0;
            this.f103ep = "";
            this.f105er = "";
            this.f101en = null;
            this.f106es = 0;
            this.f102eo = b_search.emptyArray();
            this.f109ev = null;
            this.f112ey = "";
            this.f110ew = 0;
            this.f111ex = 0;
            this.f108eu = "";
            this.f107et = null;
            this.f99eA = false;
            this.f113ez = null;
            this.f104eq = false;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.f100em != 0) {
                codedOutputByteBufferNano.writeInt32(1, this.f100em);
            }
            if (!this.f103ep.equals("")) {
                codedOutputByteBufferNano.writeString(2, this.f103ep);
            }
            if (!this.f105er.equals("")) {
                codedOutputByteBufferNano.writeString(3, this.f105er);
            }
            if (this.f101en != null) {
                codedOutputByteBufferNano.writeMessage(4, this.f101en);
            }
            if (this.f106es != 0) {
                codedOutputByteBufferNano.writeInt32(5, this.f106es);
            }
            if (this.f102eo != null && this.f102eo.length > 0) {
                for (b_search b_search : this.f102eo) {
                    if (b_search != null) {
                        codedOutputByteBufferNano.writeMessage(6, b_search);
                    }
                }
            }
            if (this.f109ev != null) {
                codedOutputByteBufferNano.writeMessage(7, this.f109ev);
            }
            if (!this.f112ey.equals("")) {
                codedOutputByteBufferNano.writeString(8, this.f112ey);
            }
            if (this.f110ew != 0) {
                codedOutputByteBufferNano.writeInt32(9, this.f110ew);
            }
            if (this.f111ex != 0) {
                codedOutputByteBufferNano.writeInt32(10, this.f111ex);
            }
            if (!this.f108eu.equals("")) {
                codedOutputByteBufferNano.writeString(11, this.f108eu);
            }
            if (this.f107et != null) {
                codedOutputByteBufferNano.writeMessage(12, this.f107et);
            }
            if (this.f99eA) {
                codedOutputByteBufferNano.writeBool(13, this.f99eA);
            }
            if (this.f113ez != null) {
                codedOutputByteBufferNano.writeMessage(14, this.f113ez);
            }
            if (this.f104eq) {
                codedOutputByteBufferNano.writeBool(15, this.f104eq);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.f100em != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.f100em);
            }
            if (!this.f103ep.equals("")) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.f103ep);
            }
            if (!this.f105er.equals("")) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.f105er);
            }
            if (this.f101en != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, this.f101en);
            }
            if (this.f106es != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, this.f106es);
            }
            if (this.f102eo != null && this.f102eo.length > 0) {
                for (b_search b_search : this.f102eo) {
                    if (b_search != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(6, b_search);
                    }
                }
            }
            if (this.f109ev != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(7, this.f109ev);
            }
            if (!this.f112ey.equals("")) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(8, this.f112ey);
            }
            if (this.f110ew != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, this.f110ew);
            }
            if (this.f111ex != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, this.f111ex);
            }
            if (!this.f108eu.equals("")) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(11, this.f108eu);
            }
            if (this.f107et != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(12, this.f107et);
            }
            if (this.f99eA) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(13, this.f99eA);
            }
            if (this.f113ez != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(14, this.f113ez);
            }
            return this.f104eq ? computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(15, this.f104eq) : computeSerializedSize;
        }

        public c_search mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 8:
                        this.f100em = codedInputByteBufferNano.readInt32();
                        break;
                    case 18:
                        this.f103ep = codedInputByteBufferNano.readString();
                        break;
                    case 26:
                        this.f105er = codedInputByteBufferNano.readString();
                        break;
                    case 34:
                        if (this.f101en == null) {
                            this.f101en = new a_search();
                        }
                        codedInputByteBufferNano.readMessage(this.f101en);
                        break;
                    case 40:
                        this.f106es = codedInputByteBufferNano.readInt32();
                        break;
                    case 50:
                        int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 50);
                        int length = this.f102eo == null ? 0 : this.f102eo.length;
                        b_search[] b_searchArr = new b_search[(repeatedFieldArrayLength + length)];
                        if (length != 0) {
                            System.arraycopy(this.f102eo, 0, b_searchArr, 0, length);
                        }
                        while (length < b_searchArr.length - 1) {
                            b_searchArr[length] = new b_search();
                            codedInputByteBufferNano.readMessage(b_searchArr[length]);
                            codedInputByteBufferNano.readTag();
                            length++;
                        }
                        b_searchArr[length] = new b_search();
                        codedInputByteBufferNano.readMessage(b_searchArr[length]);
                        this.f102eo = b_searchArr;
                        break;
                    case 58:
                        if (this.f109ev == null) {
                            this.f109ev = new a_search();
                        }
                        codedInputByteBufferNano.readMessage(this.f109ev);
                        break;
                    case 66:
                        this.f112ey = codedInputByteBufferNano.readString();
                        break;
                    case 72:
                        this.f110ew = codedInputByteBufferNano.readInt32();
                        break;
                    case 80:
                        this.f111ex = codedInputByteBufferNano.readInt32();
                        break;
                    case 90:
                        this.f108eu = codedInputByteBufferNano.readString();
                        break;
                    case 98:
                        if (this.f107et == null) {
                            this.f107et = new a_search();
                        }
                        codedInputByteBufferNano.readMessage(this.f107et);
                        break;
                    case 104:
                        this.f99eA = codedInputByteBufferNano.readBool();
                        break;
                    case 114:
                        if (this.f113ez == null) {
                            this.f113ez = new a_search();
                        }
                        codedInputByteBufferNano.readMessage(this.f113ez);
                        break;
                    case DragView.COLOR_CHANGE_DURATION /*120*/:
                        this.f104eq = codedInputByteBufferNano.readBool();
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

        public static c_search parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (c_search) MessageNano.mergeFrom(new c_search(), bArr);
        }

        public static c_search parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new c_search().mergeFrom(codedInputByteBufferNano);
        }
    }

    public static final class d_search extends MessageNano {
        private static volatile d_search[] _emptyArray;

        /* renamed from: eB */
        public c_search f114eB;

        public static d_search[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new d_search[0];
                    }
                }
            }
            return _emptyArray;
        }

        public d_search() {
            clear();
        }

        public d_search clear() {
            this.f114eB = null;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.f114eB != null) {
                codedOutputByteBufferNano.writeMessage(1, this.f114eB);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            return this.f114eB != null ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, this.f114eB) : computeSerializedSize;
        }

        public d_search mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 10) {
                    if (this.f114eB == null) {
                        this.f114eB = new c_search();
                    }
                    codedInputByteBufferNano.readMessage(this.f114eB);
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            }
        }

        public static d_search parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (d_search) MessageNano.mergeFrom(new d_search(), bArr);
        }

        public static d_search parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new d_search().mergeFrom(codedInputByteBufferNano);
        }
    }
}
