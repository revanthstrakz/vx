package android.support.constraint.solver;

import java.util.Arrays;

public class SolverVariable {
    private static final boolean INTERNAL_DEBUG = false;
    static final int MAX_STRENGTH = 6;
    public static final int STRENGTH_EQUALITY = 5;
    public static final int STRENGTH_HIGH = 3;
    public static final int STRENGTH_HIGHEST = 4;
    public static final int STRENGTH_LOW = 1;
    public static final int STRENGTH_MEDIUM = 2;
    public static final int STRENGTH_NONE = 0;
    private static int uniqueId = 1;
    public float computedValue;
    int definitionId = -1;

    /* renamed from: id */
    public int f8id = -1;
    ArrayRow[] mClientEquations = new ArrayRow[8];
    int mClientEquationsCount = 0;
    private String mName;
    Type mType;
    public int strength = 0;
    float[] strengthVector = new float[6];

    public enum Type {
        UNRESTRICTED,
        CONSTANT,
        SLACK,
        ERROR,
        UNKNOWN
    }

    private static String getUniqueName(Type type) {
        uniqueId++;
        switch (type) {
            case UNRESTRICTED:
                StringBuilder sb = new StringBuilder();
                sb.append("U");
                sb.append(uniqueId);
                return sb.toString();
            case CONSTANT:
                StringBuilder sb2 = new StringBuilder();
                sb2.append("C");
                sb2.append(uniqueId);
                return sb2.toString();
            case SLACK:
                StringBuilder sb3 = new StringBuilder();
                sb3.append("S");
                sb3.append(uniqueId);
                return sb3.toString();
            case ERROR:
                StringBuilder sb4 = new StringBuilder();
                sb4.append("e");
                sb4.append(uniqueId);
                return sb4.toString();
            default:
                StringBuilder sb5 = new StringBuilder();
                sb5.append("V");
                sb5.append(uniqueId);
                return sb5.toString();
        }
    }

    public SolverVariable(String str, Type type) {
        this.mName = str;
        this.mType = type;
    }

    public SolverVariable(Type type) {
        this.mType = type;
    }

    /* access modifiers changed from: 0000 */
    public void clearStrengths() {
        for (int i = 0; i < 6; i++) {
            this.strengthVector[i] = 0.0f;
        }
    }

    /* access modifiers changed from: 0000 */
    public String strengthsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this);
        sb.append("[");
        String sb2 = sb.toString();
        for (int i = 0; i < this.strengthVector.length; i++) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(sb2);
            sb3.append(this.strengthVector[i]);
            String sb4 = sb3.toString();
            if (i < this.strengthVector.length - 1) {
                StringBuilder sb5 = new StringBuilder();
                sb5.append(sb4);
                sb5.append(", ");
                sb2 = sb5.toString();
            } else {
                StringBuilder sb6 = new StringBuilder();
                sb6.append(sb4);
                sb6.append("] ");
                sb2 = sb6.toString();
            }
        }
        return sb2;
    }

    /* access modifiers changed from: 0000 */
    public void addClientEquation(ArrayRow arrayRow) {
        int i = 0;
        while (i < this.mClientEquationsCount) {
            if (this.mClientEquations[i] != arrayRow) {
                i++;
            } else {
                return;
            }
        }
        if (this.mClientEquationsCount >= this.mClientEquations.length) {
            this.mClientEquations = (ArrayRow[]) Arrays.copyOf(this.mClientEquations, this.mClientEquations.length * 2);
        }
        this.mClientEquations[this.mClientEquationsCount] = arrayRow;
        this.mClientEquationsCount++;
    }

    /* access modifiers changed from: 0000 */
    public void removeClientEquation(ArrayRow arrayRow) {
        for (int i = 0; i < this.mClientEquationsCount; i++) {
            if (this.mClientEquations[i] == arrayRow) {
                for (int i2 = 0; i2 < (this.mClientEquationsCount - i) - 1; i2++) {
                    int i3 = i + i2;
                    this.mClientEquations[i3] = this.mClientEquations[i3 + 1];
                }
                this.mClientEquationsCount--;
                return;
            }
        }
    }

    public void reset() {
        this.mName = null;
        this.mType = Type.UNKNOWN;
        this.strength = 0;
        this.f8id = -1;
        this.definitionId = -1;
        this.computedValue = 0.0f;
        this.mClientEquationsCount = 0;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String str) {
        this.mName = str;
    }

    public void setType(Type type) {
        this.mType = type;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(this.mName);
        return sb.toString();
    }
}
