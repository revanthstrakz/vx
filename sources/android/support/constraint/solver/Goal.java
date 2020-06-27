package android.support.constraint.solver;

import android.support.constraint.solver.SolverVariable.Type;
import java.util.ArrayList;

public class Goal {
    ArrayList<SolverVariable> variables = new ArrayList<>();

    /* access modifiers changed from: 0000 */
    public SolverVariable getPivotCandidate() {
        int size = this.variables.size();
        SolverVariable solverVariable = null;
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            SolverVariable solverVariable2 = (SolverVariable) this.variables.get(i2);
            for (int i3 = 5; i3 >= 0; i3--) {
                float f = solverVariable2.strengthVector[i3];
                if (solverVariable == null && f < 0.0f && i3 >= i) {
                    solverVariable = solverVariable2;
                    i = i3;
                }
                if (f > 0.0f && i3 > i) {
                    solverVariable = null;
                    i = i3;
                }
            }
        }
        return solverVariable;
    }

    private void initFromSystemErrors(LinearSystem linearSystem) {
        this.variables.clear();
        for (int i = 1; i < linearSystem.mNumColumns; i++) {
            SolverVariable solverVariable = linearSystem.mCache.mIndexedVariables[i];
            for (int i2 = 0; i2 < 6; i2++) {
                solverVariable.strengthVector[i2] = 0.0f;
            }
            solverVariable.strengthVector[solverVariable.strength] = 1.0f;
            if (solverVariable.mType == Type.ERROR) {
                this.variables.add(solverVariable);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateFromSystem(LinearSystem linearSystem) {
        initFromSystemErrors(linearSystem);
        int size = this.variables.size();
        for (int i = 0; i < size; i++) {
            SolverVariable solverVariable = (SolverVariable) this.variables.get(i);
            if (solverVariable.definitionId != -1) {
                ArrayLinkedVariables arrayLinkedVariables = linearSystem.getRow(solverVariable.definitionId).variables;
                int i2 = arrayLinkedVariables.currentSize;
                for (int i3 = 0; i3 < i2; i3++) {
                    SolverVariable variable = arrayLinkedVariables.getVariable(i3);
                    if (variable != null) {
                        float variableValue = arrayLinkedVariables.getVariableValue(i3);
                        for (int i4 = 0; i4 < 6; i4++) {
                            float[] fArr = variable.strengthVector;
                            fArr[i4] = fArr[i4] + (solverVariable.strengthVector[i4] * variableValue);
                        }
                        if (!this.variables.contains(variable)) {
                            this.variables.add(variable);
                        }
                    }
                }
                solverVariable.clearStrengths();
            }
        }
    }

    public String toString() {
        String str = "Goal: ";
        int size = this.variables.size();
        for (int i = 0; i < size; i++) {
            SolverVariable solverVariable = (SolverVariable) this.variables.get(i);
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(solverVariable.strengthsToString());
            str = sb.toString();
        }
        return str;
    }
}
