package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.widgets.ConstraintAnchor.Strength;
import android.support.constraint.solver.widgets.ConstraintAnchor.Type;
import android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour;
import java.util.ArrayList;

public class ConstraintTableLayout extends ConstraintWidgetContainer {
    public static final int ALIGN_CENTER = 0;
    private static final int ALIGN_FULL = 3;
    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_RIGHT = 2;
    private ArrayList<Guideline> mHorizontalGuidelines = new ArrayList<>();
    private ArrayList<HorizontalSlice> mHorizontalSlices = new ArrayList<>();
    private int mNumCols = 0;
    private int mNumRows = 0;
    private int mPadding = 8;
    private boolean mVerticalGrowth = true;
    private ArrayList<Guideline> mVerticalGuidelines = new ArrayList<>();
    private ArrayList<VerticalSlice> mVerticalSlices = new ArrayList<>();
    private LinearSystem system = null;

    class HorizontalSlice {
        ConstraintWidget bottom;
        int padding;
        ConstraintWidget top;

        HorizontalSlice() {
        }
    }

    class VerticalSlice {
        int alignment = 1;
        ConstraintWidget left;
        int padding;
        ConstraintWidget right;

        VerticalSlice() {
        }
    }

    public String getType() {
        return "ConstraintTableLayout";
    }

    public boolean handlesInternalConstraints() {
        return true;
    }

    public ConstraintTableLayout() {
    }

    public ConstraintTableLayout(int i, int i2, int i3, int i4) {
        super(i, i2, i3, i4);
    }

    public ConstraintTableLayout(int i, int i2) {
        super(i, i2);
    }

    public int getNumRows() {
        return this.mNumRows;
    }

    public int getNumCols() {
        return this.mNumCols;
    }

    public int getPadding() {
        return this.mPadding;
    }

    public String getColumnsAlignmentRepresentation() {
        int size = this.mVerticalSlices.size();
        String str = "";
        for (int i = 0; i < size; i++) {
            VerticalSlice verticalSlice = (VerticalSlice) this.mVerticalSlices.get(i);
            if (verticalSlice.alignment == 1) {
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append("L");
                str = sb.toString();
            } else if (verticalSlice.alignment == 0) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append("C");
                str = sb2.toString();
            } else if (verticalSlice.alignment == 3) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(str);
                sb3.append("F");
                str = sb3.toString();
            } else if (verticalSlice.alignment == 2) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(str);
                sb4.append("R");
                str = sb4.toString();
            }
        }
        return str;
    }

    public String getColumnAlignmentRepresentation(int i) {
        VerticalSlice verticalSlice = (VerticalSlice) this.mVerticalSlices.get(i);
        if (verticalSlice.alignment == 1) {
            return "L";
        }
        if (verticalSlice.alignment == 0) {
            return "C";
        }
        if (verticalSlice.alignment == 3) {
            return "F";
        }
        return verticalSlice.alignment == 2 ? "R" : "!";
    }

    public void setNumCols(int i) {
        if (this.mVerticalGrowth && this.mNumCols != i) {
            this.mNumCols = i;
            setVerticalSlices();
            setTableDimensions();
        }
    }

    public void setNumRows(int i) {
        if (!this.mVerticalGrowth && this.mNumCols != i) {
            this.mNumRows = i;
            setHorizontalSlices();
            setTableDimensions();
        }
    }

    public boolean isVerticalGrowth() {
        return this.mVerticalGrowth;
    }

    public void setVerticalGrowth(boolean z) {
        this.mVerticalGrowth = z;
    }

    public void setPadding(int i) {
        if (i > 1) {
            this.mPadding = i;
        }
    }

    public void setColumnAlignment(int i, int i2) {
        if (i < this.mVerticalSlices.size()) {
            ((VerticalSlice) this.mVerticalSlices.get(i)).alignment = i2;
            setChildrenConnections();
        }
    }

    public void cycleColumnAlignment(int i) {
        VerticalSlice verticalSlice = (VerticalSlice) this.mVerticalSlices.get(i);
        switch (verticalSlice.alignment) {
            case 0:
                verticalSlice.alignment = 2;
                break;
            case 1:
                verticalSlice.alignment = 0;
                break;
            case 2:
                verticalSlice.alignment = 1;
                break;
        }
        setChildrenConnections();
    }

    public void setColumnAlignment(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (charAt == 'L') {
                setColumnAlignment(i, 1);
            } else if (charAt == 'C') {
                setColumnAlignment(i, 0);
            } else if (charAt == 'F') {
                setColumnAlignment(i, 3);
            } else if (charAt == 'R') {
                setColumnAlignment(i, 2);
            } else {
                setColumnAlignment(i, 0);
            }
        }
    }

    public ArrayList<Guideline> getVerticalGuidelines() {
        return this.mVerticalGuidelines;
    }

    public ArrayList<Guideline> getHorizontalGuidelines() {
        return this.mHorizontalGuidelines;
    }

    public void addToSolver(LinearSystem linearSystem, int i) {
        super.addToSolver(linearSystem, i);
        int size = this.mChildren.size();
        if (size != 0) {
            setTableDimensions();
            if (linearSystem == this.mSystem) {
                int size2 = this.mVerticalGuidelines.size();
                int i2 = 0;
                while (true) {
                    boolean z = true;
                    if (i2 >= size2) {
                        break;
                    }
                    Guideline guideline = (Guideline) this.mVerticalGuidelines.get(i2);
                    if (getHorizontalDimensionBehaviour() != DimensionBehaviour.WRAP_CONTENT) {
                        z = false;
                    }
                    guideline.setPositionRelaxed(z);
                    guideline.addToSolver(linearSystem, i);
                    i2++;
                }
                int size3 = this.mHorizontalGuidelines.size();
                for (int i3 = 0; i3 < size3; i3++) {
                    Guideline guideline2 = (Guideline) this.mHorizontalGuidelines.get(i3);
                    guideline2.setPositionRelaxed(getVerticalDimensionBehaviour() == DimensionBehaviour.WRAP_CONTENT);
                    guideline2.addToSolver(linearSystem, i);
                }
                for (int i4 = 0; i4 < size; i4++) {
                    ((ConstraintWidget) this.mChildren.get(i4)).addToSolver(linearSystem, i);
                }
            }
        }
    }

    public void setTableDimensions() {
        int size = this.mChildren.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            i += ((ConstraintWidget) this.mChildren.get(i2)).getContainerItemSkip();
        }
        int i3 = size + i;
        if (this.mVerticalGrowth) {
            if (this.mNumCols == 0) {
                setNumCols(1);
            }
            int i4 = i3 / this.mNumCols;
            if (this.mNumCols * i4 < i3) {
                i4++;
            }
            if (this.mNumRows != i4 || this.mVerticalGuidelines.size() != this.mNumCols - 1) {
                this.mNumRows = i4;
                setHorizontalSlices();
            } else {
                return;
            }
        } else {
            if (this.mNumRows == 0) {
                setNumRows(1);
            }
            int i5 = i3 / this.mNumRows;
            if (this.mNumRows * i5 < i3) {
                i5++;
            }
            if (this.mNumCols != i5 || this.mHorizontalGuidelines.size() != this.mNumRows - 1) {
                this.mNumCols = i5;
                setVerticalSlices();
            } else {
                return;
            }
        }
        setChildrenConnections();
    }

    public void setDebugSolverName(LinearSystem linearSystem, String str) {
        this.system = linearSystem;
        super.setDebugSolverName(linearSystem, str);
        updateDebugSolverNames();
    }

    private void updateDebugSolverNames() {
        if (this.system != null) {
            int size = this.mVerticalGuidelines.size();
            for (int i = 0; i < size; i++) {
                Guideline guideline = (Guideline) this.mVerticalGuidelines.get(i);
                LinearSystem linearSystem = this.system;
                StringBuilder sb = new StringBuilder();
                sb.append(getDebugName());
                sb.append(".VG");
                sb.append(i);
                guideline.setDebugSolverName(linearSystem, sb.toString());
            }
            int size2 = this.mHorizontalGuidelines.size();
            for (int i2 = 0; i2 < size2; i2++) {
                Guideline guideline2 = (Guideline) this.mHorizontalGuidelines.get(i2);
                LinearSystem linearSystem2 = this.system;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(getDebugName());
                sb2.append(".HG");
                sb2.append(i2);
                guideline2.setDebugSolverName(linearSystem2, sb2.toString());
            }
        }
    }

    private void setVerticalSlices() {
        this.mVerticalSlices.clear();
        float f = 100.0f / ((float) this.mNumCols);
        int i = 0;
        ConstraintWidget constraintWidget = this;
        float f2 = f;
        while (i < this.mNumCols) {
            VerticalSlice verticalSlice = new VerticalSlice();
            verticalSlice.left = constraintWidget;
            if (i < this.mNumCols - 1) {
                Guideline guideline = new Guideline();
                guideline.setOrientation(1);
                guideline.setParent(this);
                guideline.setGuidePercent((int) f2);
                f2 += f;
                verticalSlice.right = guideline;
                this.mVerticalGuidelines.add(guideline);
            } else {
                verticalSlice.right = this;
            }
            ConstraintWidget constraintWidget2 = verticalSlice.right;
            this.mVerticalSlices.add(verticalSlice);
            i++;
            constraintWidget = constraintWidget2;
        }
        updateDebugSolverNames();
    }

    private void setHorizontalSlices() {
        this.mHorizontalSlices.clear();
        float f = 100.0f / ((float) this.mNumRows);
        ConstraintWidget constraintWidget = this;
        float f2 = f;
        int i = 0;
        while (i < this.mNumRows) {
            HorizontalSlice horizontalSlice = new HorizontalSlice();
            horizontalSlice.top = constraintWidget;
            if (i < this.mNumRows - 1) {
                Guideline guideline = new Guideline();
                guideline.setOrientation(0);
                guideline.setParent(this);
                guideline.setGuidePercent((int) f2);
                f2 += f;
                horizontalSlice.bottom = guideline;
                this.mHorizontalGuidelines.add(guideline);
            } else {
                horizontalSlice.bottom = this;
            }
            ConstraintWidget constraintWidget2 = horizontalSlice.bottom;
            this.mHorizontalSlices.add(horizontalSlice);
            i++;
            constraintWidget = constraintWidget2;
        }
        updateDebugSolverNames();
    }

    private void setChildrenConnections() {
        int size = this.mChildren.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            ConstraintWidget constraintWidget = (ConstraintWidget) this.mChildren.get(i2);
            int containerItemSkip = i + constraintWidget.getContainerItemSkip();
            HorizontalSlice horizontalSlice = (HorizontalSlice) this.mHorizontalSlices.get(containerItemSkip / this.mNumCols);
            VerticalSlice verticalSlice = (VerticalSlice) this.mVerticalSlices.get(containerItemSkip % this.mNumCols);
            ConstraintWidget constraintWidget2 = verticalSlice.left;
            ConstraintWidget constraintWidget3 = verticalSlice.right;
            ConstraintWidget constraintWidget4 = horizontalSlice.top;
            ConstraintWidget constraintWidget5 = horizontalSlice.bottom;
            constraintWidget.getAnchor(Type.LEFT).connect(constraintWidget2.getAnchor(Type.LEFT), this.mPadding);
            if (constraintWidget3 instanceof Guideline) {
                constraintWidget.getAnchor(Type.RIGHT).connect(constraintWidget3.getAnchor(Type.LEFT), this.mPadding);
            } else {
                constraintWidget.getAnchor(Type.RIGHT).connect(constraintWidget3.getAnchor(Type.RIGHT), this.mPadding);
            }
            switch (verticalSlice.alignment) {
                case 1:
                    constraintWidget.getAnchor(Type.LEFT).setStrength(Strength.STRONG);
                    constraintWidget.getAnchor(Type.RIGHT).setStrength(Strength.WEAK);
                    break;
                case 2:
                    constraintWidget.getAnchor(Type.LEFT).setStrength(Strength.WEAK);
                    constraintWidget.getAnchor(Type.RIGHT).setStrength(Strength.STRONG);
                    break;
                case 3:
                    constraintWidget.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
                    break;
            }
            constraintWidget.getAnchor(Type.TOP).connect(constraintWidget4.getAnchor(Type.TOP), this.mPadding);
            if (constraintWidget5 instanceof Guideline) {
                constraintWidget.getAnchor(Type.BOTTOM).connect(constraintWidget5.getAnchor(Type.TOP), this.mPadding);
            } else {
                constraintWidget.getAnchor(Type.BOTTOM).connect(constraintWidget5.getAnchor(Type.BOTTOM), this.mPadding);
            }
            i = containerItemSkip + 1;
        }
    }

    public void updateFromSolver(LinearSystem linearSystem, int i) {
        super.updateFromSolver(linearSystem, i);
        if (linearSystem == this.mSystem) {
            int size = this.mVerticalGuidelines.size();
            for (int i2 = 0; i2 < size; i2++) {
                ((Guideline) this.mVerticalGuidelines.get(i2)).updateFromSolver(linearSystem, i);
            }
            int size2 = this.mHorizontalGuidelines.size();
            for (int i3 = 0; i3 < size2; i3++) {
                ((Guideline) this.mHorizontalGuidelines.get(i3)).updateFromSolver(linearSystem, i);
            }
        }
    }

    public void computeGuidelinesPercentPositions() {
        int size = this.mVerticalGuidelines.size();
        for (int i = 0; i < size; i++) {
            ((Guideline) this.mVerticalGuidelines.get(i)).inferRelativePercentPosition();
        }
        int size2 = this.mHorizontalGuidelines.size();
        for (int i2 = 0; i2 < size2; i2++) {
            ((Guideline) this.mHorizontalGuidelines.get(i2)).inferRelativePercentPosition();
        }
    }
}
