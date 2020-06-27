package com.google.android.apps.nexuslauncher.smartspace.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;
import java.util.Arrays;

public interface SmartspaceProto {

    /* renamed from: com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto$a */
    public static final class C0947a extends MessageNano {
        private static volatile C0947a[] _emptyArray;

        /* renamed from: cw */
        public C0948b[] f139cw;

        public static C0947a[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new C0947a[0];
                    }
                }
            }
            return _emptyArray;
        }

        public C0947a() {
            clear();
        }

        public C0947a clear() {
            this.f139cw = C0948b.emptyArray();
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.f139cw != null && this.f139cw.length > 0) {
                for (C0948b bVar : this.f139cw) {
                    if (bVar != null) {
                        codedOutputByteBufferNano.writeMessage(1, bVar);
                    }
                }
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.f139cw != null && this.f139cw.length > 0) {
                for (C0948b bVar : this.f139cw) {
                    if (bVar != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, bVar);
                    }
                }
            }
            return computeSerializedSize;
        }

        public C0947a mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 10) {
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 10);
                    int length = this.f139cw == null ? 0 : this.f139cw.length;
                    C0948b[] bVarArr = new C0948b[(repeatedFieldArrayLength + length)];
                    if (length != 0) {
                        System.arraycopy(this.f139cw, 0, bVarArr, 0, length);
                    }
                    while (length < bVarArr.length - 1) {
                        bVarArr[length] = new C0948b();
                        codedInputByteBufferNano.readMessage(bVarArr[length]);
                        codedInputByteBufferNano.readTag();
                        length++;
                    }
                    bVarArr[length] = new C0948b();
                    codedInputByteBufferNano.readMessage(bVarArr[length]);
                    this.f139cw = bVarArr;
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            }
        }

        public static C0947a parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (C0947a) MessageNano.mergeFrom(new C0947a(), bArr);
        }

        public static C0947a parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new C0947a().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* renamed from: com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto$b */
    public static final class C0948b extends MessageNano {
        private static volatile C0948b[] _emptyArray;

        /* renamed from: cA */
        public int f140cA;

        /* renamed from: cB */
        public C0949c f141cB;

        /* renamed from: cC */
        public C0949c f142cC;

        /* renamed from: cD */
        public long f143cD;

        /* renamed from: cE */
        public long f144cE;

        /* renamed from: cF */
        public C0954h f145cF;

        /* renamed from: cG */
        public C0953g f146cG;

        /* renamed from: cH */
        public C0949c f147cH;

        /* renamed from: cJ */
        public int f148cJ;

        /* renamed from: cK */
        public long f149cK;

        /* renamed from: cx */
        public C0952f f150cx;

        /* renamed from: cy */
        public boolean f151cy;

        /* renamed from: cz */
        public int f152cz;

        public static C0948b[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new C0948b[0];
                    }
                }
            }
            return _emptyArray;
        }

        public C0948b() {
            clear();
        }

        public C0948b clear() {
            this.f151cy = false;
            this.f148cJ = 0;
            this.f141cB = null;
            this.f142cC = null;
            this.f147cH = null;
            this.f150cx = null;
            this.f140cA = 0;
            this.f146cG = null;
            this.f149cK = 0;
            this.f143cD = 0;
            this.f144cE = 0;
            this.f145cF = null;
            this.f152cz = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.f151cy) {
                codedOutputByteBufferNano.writeBool(1, this.f151cy);
            }
            if (this.f148cJ != 0) {
                codedOutputByteBufferNano.writeInt32(2, this.f148cJ);
            }
            if (this.f141cB != null) {
                codedOutputByteBufferNano.writeMessage(3, this.f141cB);
            }
            if (this.f142cC != null) {
                codedOutputByteBufferNano.writeMessage(4, this.f142cC);
            }
            if (this.f147cH != null) {
                codedOutputByteBufferNano.writeMessage(5, this.f147cH);
            }
            if (this.f150cx != null) {
                codedOutputByteBufferNano.writeMessage(6, this.f150cx);
            }
            if (this.f140cA != 0) {
                codedOutputByteBufferNano.writeInt32(7, this.f140cA);
            }
            if (this.f146cG != null) {
                codedOutputByteBufferNano.writeMessage(8, this.f146cG);
            }
            if (this.f149cK != 0) {
                codedOutputByteBufferNano.writeInt64(9, this.f149cK);
            }
            if (this.f143cD != 0) {
                codedOutputByteBufferNano.writeInt64(10, this.f143cD);
            }
            if (this.f144cE != 0) {
                codedOutputByteBufferNano.writeInt64(11, this.f144cE);
            }
            if (this.f145cF != null) {
                codedOutputByteBufferNano.writeMessage(12, this.f145cF);
            }
            if (this.f152cz != 0) {
                codedOutputByteBufferNano.writeInt32(13, this.f152cz);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.f151cy) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(1, this.f151cy);
            }
            if (this.f148cJ != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.f148cJ);
            }
            if (this.f141cB != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, this.f141cB);
            }
            if (this.f142cC != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, this.f142cC);
            }
            if (this.f147cH != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, this.f147cH);
            }
            if (this.f150cx != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(6, this.f150cx);
            }
            if (this.f140cA != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, this.f140cA);
            }
            if (this.f146cG != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(8, this.f146cG);
            }
            if (this.f149cK != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(9, this.f149cK);
            }
            if (this.f143cD != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(10, this.f143cD);
            }
            if (this.f144cE != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(11, this.f144cE);
            }
            if (this.f145cF != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(12, this.f145cF);
            }
            return this.f152cz != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(13, this.f152cz) : computeSerializedSize;
        }

        public C0948b mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                switch (readTag) {
                    case 0:
                        return this;
                    case 8:
                        this.f151cy = codedInputByteBufferNano.readBool();
                        break;
                    case 16:
                        this.f148cJ = codedInputByteBufferNano.readInt32();
                        break;
                    case 26:
                        if (this.f141cB == null) {
                            this.f141cB = new C0949c();
                        }
                        codedInputByteBufferNano.readMessage(this.f141cB);
                        break;
                    case 34:
                        if (this.f142cC == null) {
                            this.f142cC = new C0949c();
                        }
                        codedInputByteBufferNano.readMessage(this.f142cC);
                        break;
                    case 42:
                        if (this.f147cH == null) {
                            this.f147cH = new C0949c();
                        }
                        codedInputByteBufferNano.readMessage(this.f147cH);
                        break;
                    case 50:
                        if (this.f150cx == null) {
                            this.f150cx = new C0952f();
                        }
                        codedInputByteBufferNano.readMessage(this.f150cx);
                        break;
                    case 56:
                        this.f140cA = codedInputByteBufferNano.readInt32();
                        break;
                    case 66:
                        if (this.f146cG == null) {
                            this.f146cG = new C0953g();
                        }
                        codedInputByteBufferNano.readMessage(this.f146cG);
                        break;
                    case 72:
                        this.f149cK = codedInputByteBufferNano.readInt64();
                        break;
                    case 80:
                        this.f143cD = codedInputByteBufferNano.readInt64();
                        break;
                    case 88:
                        this.f144cE = codedInputByteBufferNano.readInt64();
                        break;
                    case 98:
                        if (this.f145cF == null) {
                            this.f145cF = new C0954h();
                        }
                        codedInputByteBufferNano.readMessage(this.f145cF);
                        break;
                    case 104:
                        this.f152cz = codedInputByteBufferNano.readInt32();
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

        public static C0948b parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (C0948b) MessageNano.mergeFrom(new C0948b(), bArr);
        }

        public static C0948b parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new C0948b().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* renamed from: com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto$c */
    public static final class C0949c extends MessageNano {
        private static volatile C0949c[] _emptyArray;

        /* renamed from: cL */
        public C0950d f153cL;

        /* renamed from: cM */
        public C0950d f154cM;

        public static C0949c[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new C0949c[0];
                    }
                }
            }
            return _emptyArray;
        }

        public C0949c() {
            clear();
        }

        public C0949c clear() {
            this.f153cL = null;
            this.f154cM = null;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.f153cL != null) {
                codedOutputByteBufferNano.writeMessage(1, this.f153cL);
            }
            if (this.f154cM != null) {
                codedOutputByteBufferNano.writeMessage(2, this.f154cM);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.f153cL != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.f153cL);
            }
            return this.f154cM != null ? computeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(2, this.f154cM) : computeSerializedSize;
        }

        public C0949c mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 10) {
                    if (this.f153cL == null) {
                        this.f153cL = new C0950d();
                    }
                    codedInputByteBufferNano.readMessage(this.f153cL);
                } else if (readTag == 18) {
                    if (this.f154cM == null) {
                        this.f154cM = new C0950d();
                    }
                    codedInputByteBufferNano.readMessage(this.f154cM);
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            }
        }

        public static C0949c parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (C0949c) MessageNano.mergeFrom(new C0949c(), bArr);
        }

        public static C0949c parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new C0949c().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* renamed from: com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto$d */
    public static final class C0950d extends MessageNano {
        private static volatile C0950d[] _emptyArray;

        /* renamed from: cN */
        public String f155cN;

        /* renamed from: cO */
        public C0951e[] f156cO;

        /* renamed from: cP */
        public int f157cP;

        public static C0950d[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new C0950d[0];
                    }
                }
            }
            return _emptyArray;
        }

        public C0950d() {
            clear();
        }

        public C0950d clear() {
            this.f155cN = "";
            this.f157cP = 0;
            this.f156cO = C0951e.emptyArray();
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (!this.f155cN.equals("")) {
                codedOutputByteBufferNano.writeString(1, this.f155cN);
            }
            if (this.f157cP != 0) {
                codedOutputByteBufferNano.writeInt32(2, this.f157cP);
            }
            if (this.f156cO != null && this.f156cO.length > 0) {
                for (C0951e eVar : this.f156cO) {
                    if (eVar != null) {
                        codedOutputByteBufferNano.writeMessage(3, eVar);
                    }
                }
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (!this.f155cN.equals("")) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.f155cN);
            }
            if (this.f157cP != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.f157cP);
            }
            if (this.f156cO != null && this.f156cO.length > 0) {
                for (C0951e eVar : this.f156cO) {
                    if (eVar != null) {
                        computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(3, eVar);
                    }
                }
            }
            return computeSerializedSize;
        }

        public C0950d mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 10) {
                    this.f155cN = codedInputByteBufferNano.readString();
                } else if (readTag == 16) {
                    this.f157cP = codedInputByteBufferNano.readInt32();
                } else if (readTag == 26) {
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 26);
                    int length = this.f156cO == null ? 0 : this.f156cO.length;
                    C0951e[] eVarArr = new C0951e[(repeatedFieldArrayLength + length)];
                    if (length != 0) {
                        System.arraycopy(this.f156cO, 0, eVarArr, 0, length);
                    }
                    while (length < eVarArr.length - 1) {
                        eVarArr[length] = new C0951e();
                        codedInputByteBufferNano.readMessage(eVarArr[length]);
                        codedInputByteBufferNano.readTag();
                        length++;
                    }
                    eVarArr[length] = new C0951e();
                    codedInputByteBufferNano.readMessage(eVarArr[length]);
                    this.f156cO = eVarArr;
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            }
        }

        public static C0950d parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (C0950d) MessageNano.mergeFrom(new C0950d(), bArr);
        }

        public static C0950d parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new C0950d().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* renamed from: com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto$e */
    public static final class C0951e extends MessageNano {
        private static volatile C0951e[] _emptyArray;

        /* renamed from: cQ */
        public int f158cQ;

        /* renamed from: cR */
        public String f159cR;

        /* renamed from: cS */
        public int f160cS;

        /* renamed from: cU */
        public boolean f161cU;

        public static C0951e[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new C0951e[0];
                    }
                }
            }
            return _emptyArray;
        }

        public C0951e() {
            clear();
        }

        public C0951e clear() {
            this.f159cR = "";
            this.f160cS = 0;
            this.f158cQ = 0;
            this.f161cU = false;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (!this.f159cR.equals("")) {
                codedOutputByteBufferNano.writeString(1, this.f159cR);
            }
            if (this.f160cS != 0) {
                codedOutputByteBufferNano.writeInt32(2, this.f160cS);
            }
            if (this.f158cQ != 0) {
                codedOutputByteBufferNano.writeInt32(3, this.f158cQ);
            }
            if (this.f161cU) {
                codedOutputByteBufferNano.writeBool(4, this.f161cU);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (!this.f159cR.equals("")) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.f159cR);
            }
            if (this.f160cS != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, this.f160cS);
            }
            if (this.f158cQ != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, this.f158cQ);
            }
            return this.f161cU ? computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(4, this.f161cU) : computeSerializedSize;
        }

        public C0951e mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 10) {
                    this.f159cR = codedInputByteBufferNano.readString();
                } else if (readTag == 16) {
                    this.f160cS = codedInputByteBufferNano.readInt32();
                } else if (readTag == 24) {
                    this.f158cQ = codedInputByteBufferNano.readInt32();
                } else if (readTag == 32) {
                    this.f161cU = codedInputByteBufferNano.readBool();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            }
        }

        public static C0951e parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (C0951e) MessageNano.mergeFrom(new C0951e(), bArr);
        }

        public static C0951e parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new C0951e().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* renamed from: com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto$f */
    public static final class C0952f extends MessageNano {
        private static volatile C0952f[] _emptyArray;

        /* renamed from: cV */
        public String f162cV;

        /* renamed from: cW */
        public String f163cW;

        /* renamed from: cX */
        public String f164cX;

        public static C0952f[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new C0952f[0];
                    }
                }
            }
            return _emptyArray;
        }

        public C0952f() {
            clear();
        }

        public C0952f clear() {
            this.f162cV = "";
            this.f163cW = "";
            this.f164cX = "";
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (!this.f162cV.equals("")) {
                codedOutputByteBufferNano.writeString(1, this.f162cV);
            }
            if (!this.f163cW.equals("")) {
                codedOutputByteBufferNano.writeString(2, this.f163cW);
            }
            if (!this.f164cX.equals("")) {
                codedOutputByteBufferNano.writeString(3, this.f164cX);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (!this.f162cV.equals("")) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(1, this.f162cV);
            }
            if (!this.f163cW.equals("")) {
                computeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.f163cW);
            }
            return !this.f164cX.equals("") ? computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(3, this.f164cX) : computeSerializedSize;
        }

        public C0952f mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 10) {
                    this.f162cV = codedInputByteBufferNano.readString();
                } else if (readTag == 18) {
                    this.f163cW = codedInputByteBufferNano.readString();
                } else if (readTag == 26) {
                    this.f164cX = codedInputByteBufferNano.readString();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            }
        }

        public static C0952f parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (C0952f) MessageNano.mergeFrom(new C0952f(), bArr);
        }

        public static C0952f parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new C0952f().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* renamed from: com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto$g */
    public static final class C0953g extends MessageNano {
        private static volatile C0953g[] _emptyArray;

        /* renamed from: cY */
        public int f165cY;

        /* renamed from: cZ */
        public String f166cZ;

        public static C0953g[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new C0953g[0];
                    }
                }
            }
            return _emptyArray;
        }

        public C0953g() {
            clear();
        }

        public C0953g clear() {
            this.f165cY = 0;
            this.f166cZ = "";
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.f165cY != 0) {
                codedOutputByteBufferNano.writeInt32(1, this.f165cY);
            }
            if (!this.f166cZ.equals("")) {
                codedOutputByteBufferNano.writeString(2, this.f166cZ);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.f165cY != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, this.f165cY);
            }
            return !this.f166cZ.equals("") ? computeSerializedSize + CodedOutputByteBufferNano.computeStringSize(2, this.f166cZ) : computeSerializedSize;
        }

        public C0953g mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 8) {
                    this.f165cY = codedInputByteBufferNano.readInt32();
                } else if (readTag == 18) {
                    this.f166cZ = codedInputByteBufferNano.readString();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            }
        }

        public static C0953g parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (C0953g) MessageNano.mergeFrom(new C0953g(), bArr);
        }

        public static C0953g parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new C0953g().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* renamed from: com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto$h */
    public static final class C0954h extends MessageNano {
        private static volatile C0954h[] _emptyArray;

        /* renamed from: da */
        public long f167da;

        /* renamed from: db */
        public int f168db;

        public static C0954h[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new C0954h[0];
                    }
                }
            }
            return _emptyArray;
        }

        public C0954h() {
            clear();
        }

        public C0954h clear() {
            this.f167da = 0;
            this.f168db = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.f167da != 0) {
                codedOutputByteBufferNano.writeInt64(1, this.f167da);
            }
            if (this.f168db != 0) {
                codedOutputByteBufferNano.writeInt32(2, this.f168db);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.f167da != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(1, this.f167da);
            }
            return this.f168db != 0 ? computeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, this.f168db) : computeSerializedSize;
        }

        public C0954h mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 8) {
                    this.f167da = codedInputByteBufferNano.readInt64();
                } else if (readTag == 16) {
                    this.f168db = codedInputByteBufferNano.readInt32();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            }
        }

        public static C0954h parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (C0954h) MessageNano.mergeFrom(new C0954h(), bArr);
        }

        public static C0954h parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new C0954h().mergeFrom(codedInputByteBufferNano);
        }
    }

    /* renamed from: com.google.android.apps.nexuslauncher.smartspace.nano.SmartspaceProto$i */
    public static final class C0955i extends MessageNano {
        private static volatile C0955i[] _emptyArray;

        /* renamed from: dc */
        public boolean f169dc;

        /* renamed from: dd */
        public byte[] f170dd;

        /* renamed from: de */
        public C0948b f171de;

        /* renamed from: df */
        public long f172df;

        /* renamed from: dg */
        public int f173dg;

        /* renamed from: dh */
        public long f174dh;

        public static C0955i[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new C0955i[0];
                    }
                }
            }
            return _emptyArray;
        }

        public C0955i() {
            clear();
        }

        public C0955i clear() {
            this.f169dc = false;
            this.f170dd = WireFormatNano.EMPTY_BYTES;
            this.f171de = null;
            this.f172df = 0;
            this.f173dg = 0;
            this.f174dh = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
            if (this.f171de != null) {
                codedOutputByteBufferNano.writeMessage(1, this.f171de);
            }
            if (this.f172df != 0) {
                codedOutputByteBufferNano.writeInt64(2, this.f172df);
            }
            if (this.f174dh != 0) {
                codedOutputByteBufferNano.writeInt64(3, this.f174dh);
            }
            if (this.f173dg != 0) {
                codedOutputByteBufferNano.writeInt32(4, this.f173dg);
            }
            if (!Arrays.equals(this.f170dd, WireFormatNano.EMPTY_BYTES)) {
                codedOutputByteBufferNano.writeBytes(5, this.f170dd);
            }
            if (this.f169dc) {
                codedOutputByteBufferNano.writeBool(6, this.f169dc);
            }
            super.writeTo(codedOutputByteBufferNano);
        }

        /* access modifiers changed from: protected */
        public int computeSerializedSize() {
            int computeSerializedSize = super.computeSerializedSize();
            if (this.f171de != null) {
                computeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, this.f171de);
            }
            if (this.f172df != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(2, this.f172df);
            }
            if (this.f174dh != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(3, this.f174dh);
            }
            if (this.f173dg != 0) {
                computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, this.f173dg);
            }
            if (!Arrays.equals(this.f170dd, WireFormatNano.EMPTY_BYTES)) {
                computeSerializedSize += CodedOutputByteBufferNano.computeBytesSize(5, this.f170dd);
            }
            return this.f169dc ? computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(6, this.f169dc) : computeSerializedSize;
        }

        public C0955i mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            while (true) {
                int readTag = codedInputByteBufferNano.readTag();
                if (readTag == 0) {
                    return this;
                }
                if (readTag == 10) {
                    if (this.f171de == null) {
                        this.f171de = new C0948b();
                    }
                    codedInputByteBufferNano.readMessage(this.f171de);
                } else if (readTag == 16) {
                    this.f172df = codedInputByteBufferNano.readInt64();
                } else if (readTag == 24) {
                    this.f174dh = codedInputByteBufferNano.readInt64();
                } else if (readTag == 32) {
                    this.f173dg = codedInputByteBufferNano.readInt32();
                } else if (readTag == 42) {
                    this.f170dd = codedInputByteBufferNano.readBytes();
                } else if (readTag == 48) {
                    this.f169dc = codedInputByteBufferNano.readBool();
                } else if (!WireFormatNano.parseUnknownField(codedInputByteBufferNano, readTag)) {
                    return this;
                }
            }
        }

        public static C0955i parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
            return (C0955i) MessageNano.mergeFrom(new C0955i(), bArr);
        }

        public static C0955i parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
            return new C0955i().mergeFrom(codedInputByteBufferNano);
        }
    }
}
