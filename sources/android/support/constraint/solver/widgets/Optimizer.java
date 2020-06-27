package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour;

public class Optimizer {
    static void applyDirectResolutionHorizontalChain(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, ConstraintWidget constraintWidget) {
        int i2;
        float f;
        float f2;
        float f3;
        ConstraintWidgetContainer constraintWidgetContainer2 = constraintWidgetContainer;
        LinearSystem linearSystem2 = linearSystem;
        int i3 = i;
        ConstraintWidget constraintWidget2 = constraintWidget;
        ConstraintWidget constraintWidget3 = null;
        int i4 = 0;
        int i5 = 0;
        float f4 = 0.0f;
        while (true) {
            boolean z = true;
            if (constraintWidget2 == null) {
                break;
            }
            if (constraintWidget2.getVisibility() != 8) {
                z = false;
            }
            if (!z) {
                i4++;
                if (constraintWidget2.mHorizontalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
                    i5 = i5 + constraintWidget2.getWidth() + (constraintWidget2.mLeft.mTarget != null ? constraintWidget2.mLeft.getMargin() : 0) + (constraintWidget2.mRight.mTarget != null ? constraintWidget2.mRight.getMargin() : 0);
                } else {
                    f4 += constraintWidget2.mHorizontalWeight;
                }
            }
            ConstraintWidget constraintWidget4 = constraintWidget2.mRight.mTarget != null ? constraintWidget2.mRight.mTarget.mOwner : null;
            if (constraintWidget4 != null && (constraintWidget4.mLeft.mTarget == null || !(constraintWidget4.mLeft.mTarget == null || constraintWidget4.mLeft.mTarget.mOwner == constraintWidget2))) {
                constraintWidget4 = null;
            }
            ConstraintWidget constraintWidget5 = constraintWidget4;
            constraintWidget3 = constraintWidget2;
            constraintWidget2 = constraintWidget5;
        }
        if (constraintWidget3 != null) {
            i2 = constraintWidget3.mRight.mTarget != null ? constraintWidget3.mRight.mTarget.mOwner.getX() : 0;
            if (constraintWidget3.mRight.mTarget != null && constraintWidget3.mRight.mTarget.mOwner == constraintWidgetContainer2) {
                i2 = constraintWidgetContainer.getRight();
            }
        } else {
            i2 = 0;
        }
        float f5 = ((float) (i2 - 0)) - ((float) i5);
        float f6 = f5 / ((float) (i4 + 1));
        if (i3 == 0) {
            f2 = f6;
            f = f2;
        } else {
            f = f5 / ((float) i3);
            f2 = 0.0f;
        }
        ConstraintWidget constraintWidget6 = constraintWidget;
        while (constraintWidget6 != null) {
            int margin = constraintWidget6.mLeft.mTarget != null ? constraintWidget6.mLeft.getMargin() : 0;
            int margin2 = constraintWidget6.mRight.mTarget != null ? constraintWidget6.mRight.getMargin() : 0;
            if (constraintWidget6.getVisibility() != 8) {
                float f7 = (float) margin;
                float f8 = f2 + f7;
                linearSystem2.addEquality(constraintWidget6.mLeft.mSolverVariable, (int) (f8 + 0.5f));
                if (constraintWidget6.mHorizontalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
                    f3 = f8 + ((float) constraintWidget6.getWidth());
                } else if (f4 == 0.0f) {
                    f3 = f8 + ((f - f7) - ((float) margin2));
                } else {
                    f3 = f8 + ((((constraintWidget6.mHorizontalWeight * f5) / f4) - f7) - ((float) margin2));
                }
                linearSystem2.addEquality(constraintWidget6.mRight.mSolverVariable, (int) (0.5f + f3));
                if (i3 == 0) {
                    f3 += f;
                }
                f2 = f3 + ((float) margin2);
            } else {
                int i6 = (int) ((f2 - (f / 2.0f)) + 0.5f);
                linearSystem2.addEquality(constraintWidget6.mLeft.mSolverVariable, i6);
                linearSystem2.addEquality(constraintWidget6.mRight.mSolverVariable, i6);
            }
            ConstraintWidget constraintWidget7 = constraintWidget6.mRight.mTarget != null ? constraintWidget6.mRight.mTarget.mOwner : null;
            if (!(constraintWidget7 == null || constraintWidget7.mLeft.mTarget == null || constraintWidget7.mLeft.mTarget.mOwner == constraintWidget6)) {
                constraintWidget7 = null;
            }
            constraintWidget6 = constraintWidget7 == constraintWidgetContainer2 ? null : constraintWidget7;
        }
    }

    static void applyDirectResolutionVerticalChain(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, ConstraintWidget constraintWidget) {
        int i2;
        float f;
        float f2;
        float f3;
        ConstraintWidgetContainer constraintWidgetContainer2 = constraintWidgetContainer;
        LinearSystem linearSystem2 = linearSystem;
        int i3 = i;
        ConstraintWidget constraintWidget2 = constraintWidget;
        ConstraintWidget constraintWidget3 = null;
        int i4 = 0;
        int i5 = 0;
        float f4 = 0.0f;
        while (true) {
            boolean z = true;
            if (constraintWidget2 == null) {
                break;
            }
            if (constraintWidget2.getVisibility() != 8) {
                z = false;
            }
            if (!z) {
                i4++;
                if (constraintWidget2.mVerticalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
                    i5 = i5 + constraintWidget2.getHeight() + (constraintWidget2.mTop.mTarget != null ? constraintWidget2.mTop.getMargin() : 0) + (constraintWidget2.mBottom.mTarget != null ? constraintWidget2.mBottom.getMargin() : 0);
                } else {
                    f4 += constraintWidget2.mVerticalWeight;
                }
            }
            ConstraintWidget constraintWidget4 = constraintWidget2.mBottom.mTarget != null ? constraintWidget2.mBottom.mTarget.mOwner : null;
            if (constraintWidget4 != null && (constraintWidget4.mTop.mTarget == null || !(constraintWidget4.mTop.mTarget == null || constraintWidget4.mTop.mTarget.mOwner == constraintWidget2))) {
                constraintWidget4 = null;
            }
            ConstraintWidget constraintWidget5 = constraintWidget4;
            constraintWidget3 = constraintWidget2;
            constraintWidget2 = constraintWidget5;
        }
        if (constraintWidget3 != null) {
            i2 = constraintWidget3.mBottom.mTarget != null ? constraintWidget3.mBottom.mTarget.mOwner.getX() : 0;
            if (constraintWidget3.mBottom.mTarget != null && constraintWidget3.mBottom.mTarget.mOwner == constraintWidgetContainer2) {
                i2 = constraintWidgetContainer.getBottom();
            }
        } else {
            i2 = 0;
        }
        float f5 = ((float) (i2 - 0)) - ((float) i5);
        float f6 = f5 / ((float) (i4 + 1));
        if (i3 == 0) {
            f2 = f6;
            f = f2;
        } else {
            f = f5 / ((float) i3);
            f2 = 0.0f;
        }
        ConstraintWidget constraintWidget6 = constraintWidget;
        while (constraintWidget6 != null) {
            int margin = constraintWidget6.mTop.mTarget != null ? constraintWidget6.mTop.getMargin() : 0;
            int margin2 = constraintWidget6.mBottom.mTarget != null ? constraintWidget6.mBottom.getMargin() : 0;
            if (constraintWidget6.getVisibility() != 8) {
                float f7 = (float) margin;
                float f8 = f2 + f7;
                linearSystem2.addEquality(constraintWidget6.mTop.mSolverVariable, (int) (f8 + 0.5f));
                if (constraintWidget6.mVerticalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
                    f3 = f8 + ((float) constraintWidget6.getHeight());
                } else if (f4 == 0.0f) {
                    f3 = f8 + ((f - f7) - ((float) margin2));
                } else {
                    f3 = f8 + ((((constraintWidget6.mVerticalWeight * f5) / f4) - f7) - ((float) margin2));
                }
                linearSystem2.addEquality(constraintWidget6.mBottom.mSolverVariable, (int) (0.5f + f3));
                if (i3 == 0) {
                    f3 += f;
                }
                f2 = f3 + ((float) margin2);
            } else {
                int i6 = (int) ((f2 - (f / 2.0f)) + 0.5f);
                linearSystem2.addEquality(constraintWidget6.mTop.mSolverVariable, i6);
                linearSystem2.addEquality(constraintWidget6.mBottom.mSolverVariable, i6);
            }
            ConstraintWidget constraintWidget7 = constraintWidget6.mBottom.mTarget != null ? constraintWidget6.mBottom.mTarget.mOwner : null;
            if (!(constraintWidget7 == null || constraintWidget7.mTop.mTarget == null || constraintWidget7.mTop.mTarget.mOwner == constraintWidget6)) {
                constraintWidget7 = null;
            }
            constraintWidget6 = constraintWidget7 == constraintWidgetContainer2 ? null : constraintWidget7;
        }
    }

    static void checkMatchParent(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, ConstraintWidget constraintWidget) {
        if (constraintWidgetContainer.mHorizontalDimensionBehaviour != DimensionBehaviour.WRAP_CONTENT && constraintWidget.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_PARENT) {
            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
            int i = constraintWidget.mLeft.mMargin;
            int width = constraintWidgetContainer.getWidth() - constraintWidget.mRight.mMargin;
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, i);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width);
            constraintWidget.setHorizontalDimension(i, width);
            constraintWidget.mHorizontalResolution = 2;
        }
        if (constraintWidgetContainer.mVerticalDimensionBehaviour != DimensionBehaviour.WRAP_CONTENT && constraintWidget.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_PARENT) {
            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
            int i2 = constraintWidget.mTop.mMargin;
            int height = constraintWidgetContainer.getHeight() - constraintWidget.mBottom.mMargin;
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, i2);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height);
            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + i2);
            }
            constraintWidget.setVerticalDimension(i2, height);
            constraintWidget.mVerticalResolution = 2;
        }
    }

    static void checkHorizontalSimpleDependency(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, ConstraintWidget constraintWidget) {
        float f;
        int i;
        if (constraintWidget.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
            constraintWidget.mHorizontalResolution = 1;
        } else if (constraintWidgetContainer.mHorizontalDimensionBehaviour != DimensionBehaviour.WRAP_CONTENT && constraintWidget.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_PARENT) {
            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
            int i2 = constraintWidget.mLeft.mMargin;
            int width = constraintWidgetContainer.getWidth() - constraintWidget.mRight.mMargin;
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, i2);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width);
            constraintWidget.setHorizontalDimension(i2, width);
            constraintWidget.mHorizontalResolution = 2;
        } else if (constraintWidget.mLeft.mTarget == null || constraintWidget.mRight.mTarget == null) {
            if (constraintWidget.mLeft.mTarget != null && constraintWidget.mLeft.mTarget.mOwner == constraintWidgetContainer) {
                int margin = constraintWidget.mLeft.getMargin();
                int width2 = constraintWidget.getWidth() + margin;
                constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, margin);
                linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width2);
                constraintWidget.mHorizontalResolution = 2;
                constraintWidget.setHorizontalDimension(margin, width2);
            } else if (constraintWidget.mRight.mTarget != null && constraintWidget.mRight.mTarget.mOwner == constraintWidgetContainer) {
                constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                int width3 = constraintWidgetContainer.getWidth() - constraintWidget.mRight.getMargin();
                int width4 = width3 - constraintWidget.getWidth();
                linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, width4);
                linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width3);
                constraintWidget.mHorizontalResolution = 2;
                constraintWidget.setHorizontalDimension(width4, width3);
            } else if (constraintWidget.mLeft.mTarget != null && constraintWidget.mLeft.mTarget.mOwner.mHorizontalResolution == 2) {
                SolverVariable solverVariable = constraintWidget.mLeft.mTarget.mSolverVariable;
                constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                int margin2 = (int) (solverVariable.computedValue + ((float) constraintWidget.mLeft.getMargin()) + 0.5f);
                int width5 = constraintWidget.getWidth() + margin2;
                linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, margin2);
                linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width5);
                constraintWidget.mHorizontalResolution = 2;
                constraintWidget.setHorizontalDimension(margin2, width5);
            } else if (constraintWidget.mRight.mTarget == null || constraintWidget.mRight.mTarget.mOwner.mHorizontalResolution != 2) {
                boolean z = constraintWidget.mLeft.mTarget != null;
                boolean z2 = constraintWidget.mRight.mTarget != null;
                if (!z && !z2) {
                    if (constraintWidget instanceof Guideline) {
                        Guideline guideline = (Guideline) constraintWidget;
                        if (guideline.getOrientation() == 1) {
                            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                            if (guideline.getRelativeBegin() != -1) {
                                f = (float) guideline.getRelativeBegin();
                            } else if (guideline.getRelativeEnd() != -1) {
                                f = (float) (constraintWidgetContainer.getWidth() - guideline.getRelativeEnd());
                            } else {
                                f = guideline.getRelativePercent() * ((float) constraintWidgetContainer.getWidth());
                            }
                            int i3 = (int) (f + 0.5f);
                            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, i3);
                            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, i3);
                            constraintWidget.mHorizontalResolution = 2;
                            constraintWidget.mVerticalResolution = 2;
                            constraintWidget.setHorizontalDimension(i3, i3);
                            constraintWidget.setVerticalDimension(0, constraintWidgetContainer.getHeight());
                        }
                    } else {
                        constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                        constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                        int x = constraintWidget.getX();
                        int width6 = constraintWidget.getWidth() + x;
                        linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, x);
                        linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width6);
                        constraintWidget.mHorizontalResolution = 2;
                    }
                }
            } else {
                SolverVariable solverVariable2 = constraintWidget.mRight.mTarget.mSolverVariable;
                constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                int margin3 = (int) ((solverVariable2.computedValue - ((float) constraintWidget.mRight.getMargin())) + 0.5f);
                int width7 = margin3 - constraintWidget.getWidth();
                linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, width7);
                linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, margin3);
                constraintWidget.mHorizontalResolution = 2;
                constraintWidget.setHorizontalDimension(width7, margin3);
            }
        } else if (constraintWidget.mLeft.mTarget.mOwner == constraintWidgetContainer && constraintWidget.mRight.mTarget.mOwner == constraintWidgetContainer) {
            int margin4 = constraintWidget.mLeft.getMargin();
            int margin5 = constraintWidget.mRight.getMargin();
            if (constraintWidgetContainer.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
                i = constraintWidgetContainer.getWidth() - margin5;
            } else {
                margin4 += (int) ((((float) (((constraintWidgetContainer.getWidth() - margin4) - margin5) - constraintWidget.getWidth())) * constraintWidget.mHorizontalBiasPercent) + 0.5f);
                i = constraintWidget.getWidth() + margin4;
            }
            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, margin4);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, i);
            constraintWidget.mHorizontalResolution = 2;
            constraintWidget.setHorizontalDimension(margin4, i);
        } else {
            constraintWidget.mHorizontalResolution = 1;
        }
    }

    static void checkVerticalSimpleDependency(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, ConstraintWidget constraintWidget) {
        float f;
        int i;
        boolean z = true;
        if (constraintWidget.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
            constraintWidget.mVerticalResolution = 1;
        } else if (constraintWidgetContainer.mVerticalDimensionBehaviour != DimensionBehaviour.WRAP_CONTENT && constraintWidget.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_PARENT) {
            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
            int i2 = constraintWidget.mTop.mMargin;
            int height = constraintWidgetContainer.getHeight() - constraintWidget.mBottom.mMargin;
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, i2);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height);
            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + i2);
            }
            constraintWidget.setVerticalDimension(i2, height);
            constraintWidget.mVerticalResolution = 2;
        } else if (constraintWidget.mTop.mTarget == null || constraintWidget.mBottom.mTarget == null) {
            if (constraintWidget.mTop.mTarget != null && constraintWidget.mTop.mTarget.mOwner == constraintWidgetContainer) {
                int margin = constraintWidget.mTop.getMargin();
                int height2 = constraintWidget.getHeight() + margin;
                constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, margin);
                linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height2);
                if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                    constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                    linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + margin);
                }
                constraintWidget.mVerticalResolution = 2;
                constraintWidget.setVerticalDimension(margin, height2);
            } else if (constraintWidget.mBottom.mTarget != null && constraintWidget.mBottom.mTarget.mOwner == constraintWidgetContainer) {
                constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                int height3 = constraintWidgetContainer.getHeight() - constraintWidget.mBottom.getMargin();
                int height4 = height3 - constraintWidget.getHeight();
                linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, height4);
                linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height3);
                if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                    constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                    linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + height4);
                }
                constraintWidget.mVerticalResolution = 2;
                constraintWidget.setVerticalDimension(height4, height3);
            } else if (constraintWidget.mTop.mTarget != null && constraintWidget.mTop.mTarget.mOwner.mVerticalResolution == 2) {
                SolverVariable solverVariable = constraintWidget.mTop.mTarget.mSolverVariable;
                constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                int margin2 = (int) (solverVariable.computedValue + ((float) constraintWidget.mTop.getMargin()) + 0.5f);
                int height5 = constraintWidget.getHeight() + margin2;
                linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, margin2);
                linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height5);
                if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                    constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                    linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + margin2);
                }
                constraintWidget.mVerticalResolution = 2;
                constraintWidget.setVerticalDimension(margin2, height5);
            } else if (constraintWidget.mBottom.mTarget != null && constraintWidget.mBottom.mTarget.mOwner.mVerticalResolution == 2) {
                SolverVariable solverVariable2 = constraintWidget.mBottom.mTarget.mSolverVariable;
                constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                int margin3 = (int) ((solverVariable2.computedValue - ((float) constraintWidget.mBottom.getMargin())) + 0.5f);
                int height6 = margin3 - constraintWidget.getHeight();
                linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, height6);
                linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, margin3);
                if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                    constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                    linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + height6);
                }
                constraintWidget.mVerticalResolution = 2;
                constraintWidget.setVerticalDimension(height6, margin3);
            } else if (constraintWidget.mBaseline.mTarget == null || constraintWidget.mBaseline.mTarget.mOwner.mVerticalResolution != 2) {
                boolean z2 = constraintWidget.mBaseline.mTarget != null;
                boolean z3 = constraintWidget.mTop.mTarget != null;
                if (constraintWidget.mBottom.mTarget == null) {
                    z = false;
                }
                if (!z2 && !z3 && !z) {
                    if (constraintWidget instanceof Guideline) {
                        Guideline guideline = (Guideline) constraintWidget;
                        if (guideline.getOrientation() == 0) {
                            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                            if (guideline.getRelativeBegin() != -1) {
                                f = (float) guideline.getRelativeBegin();
                            } else if (guideline.getRelativeEnd() != -1) {
                                f = (float) (constraintWidgetContainer.getHeight() - guideline.getRelativeEnd());
                            } else {
                                f = guideline.getRelativePercent() * ((float) constraintWidgetContainer.getHeight());
                            }
                            int i3 = (int) (f + 0.5f);
                            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, i3);
                            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, i3);
                            constraintWidget.mVerticalResolution = 2;
                            constraintWidget.mHorizontalResolution = 2;
                            constraintWidget.setVerticalDimension(i3, i3);
                            constraintWidget.setHorizontalDimension(0, constraintWidgetContainer.getWidth());
                        }
                    } else {
                        constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                        constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                        int y = constraintWidget.getY();
                        int height7 = constraintWidget.getHeight() + y;
                        linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, y);
                        linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height7);
                        if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                            constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                            linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, y + constraintWidget.mBaselineDistance);
                        }
                        constraintWidget.mVerticalResolution = 2;
                    }
                }
            } else {
                SolverVariable solverVariable3 = constraintWidget.mBaseline.mTarget.mSolverVariable;
                constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                int i4 = (int) ((solverVariable3.computedValue - ((float) constraintWidget.mBaselineDistance)) + 0.5f);
                int height8 = constraintWidget.getHeight() + i4;
                linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, i4);
                linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height8);
                constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + i4);
                constraintWidget.mVerticalResolution = 2;
                constraintWidget.setVerticalDimension(i4, height8);
            }
        } else if (constraintWidget.mTop.mTarget.mOwner == constraintWidgetContainer && constraintWidget.mBottom.mTarget.mOwner == constraintWidgetContainer) {
            int margin4 = constraintWidget.mTop.getMargin();
            int margin5 = constraintWidget.mBottom.getMargin();
            if (constraintWidgetContainer.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
                i = constraintWidget.getHeight() + margin4;
            } else {
                margin4 = (int) (((float) margin4) + (((float) (((constraintWidgetContainer.getHeight() - margin4) - margin5) - constraintWidget.getHeight())) * constraintWidget.mVerticalBiasPercent) + 0.5f);
                i = constraintWidget.getHeight() + margin4;
            }
            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, margin4);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, i);
            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + margin4);
            }
            constraintWidget.mVerticalResolution = 2;
            constraintWidget.setVerticalDimension(margin4, i);
        } else {
            constraintWidget.mVerticalResolution = 1;
        }
    }
}
