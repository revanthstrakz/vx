package android.support.constraint.solver.widgets;

import android.support.constraint.solver.ArrayRow;
import android.support.constraint.solver.Cache;
import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintAnchor.ConnectionType;
import android.support.constraint.solver.widgets.ConstraintAnchor.Strength;
import android.support.constraint.solver.widgets.ConstraintAnchor.Type;
import java.util.ArrayList;
import p013io.virtualapp.utils.HanziToPinyin.Token;

public class ConstraintWidget {
    private static final boolean AUTOTAG_CENTER = false;
    public static final int CHAIN_PACKED = 2;
    public static final int CHAIN_SPREAD = 0;
    public static final int CHAIN_SPREAD_INSIDE = 1;
    public static float DEFAULT_BIAS = 0.5f;
    protected static final int DIRECT = 2;
    public static final int GONE = 8;
    public static final int HORIZONTAL = 0;
    public static final int INVISIBLE = 4;
    public static final int MATCH_CONSTRAINT_SPREAD = 0;
    public static final int MATCH_CONSTRAINT_WRAP = 1;
    protected static final int SOLVER = 1;
    public static final int UNKNOWN = -1;
    public static final int VERTICAL = 1;
    public static final int VISIBLE = 0;
    protected ArrayList<ConstraintAnchor> mAnchors;
    ConstraintAnchor mBaseline;
    int mBaselineDistance;
    ConstraintAnchor mBottom;
    boolean mBottomHasCentered;
    ConstraintAnchor mCenter;
    ConstraintAnchor mCenterX;
    ConstraintAnchor mCenterY;
    private Object mCompanionWidget;
    private int mContainerItemSkip;
    private String mDebugName;
    protected float mDimensionRatio;
    protected int mDimensionRatioSide;
    int mDistToBottom;
    int mDistToLeft;
    int mDistToRight;
    int mDistToTop;
    private int mDrawHeight;
    private int mDrawWidth;
    private int mDrawX;
    private int mDrawY;
    int mHeight;
    float mHorizontalBiasPercent;
    boolean mHorizontalChainFixedPosition;
    int mHorizontalChainStyle;
    DimensionBehaviour mHorizontalDimensionBehaviour;
    ConstraintWidget mHorizontalNextWidget;
    public int mHorizontalResolution;
    float mHorizontalWeight;
    boolean mHorizontalWrapVisited;
    ConstraintAnchor mLeft;
    boolean mLeftHasCentered;
    int mMatchConstraintDefaultHeight;
    int mMatchConstraintDefaultWidth;
    int mMatchConstraintMaxHeight;
    int mMatchConstraintMaxWidth;
    int mMatchConstraintMinHeight;
    int mMatchConstraintMinWidth;
    protected int mMinHeight;
    protected int mMinWidth;
    protected int mOffsetX;
    protected int mOffsetY;
    ConstraintWidget mParent;
    ConstraintAnchor mRight;
    boolean mRightHasCentered;
    private int mSolverBottom;
    private int mSolverLeft;
    private int mSolverRight;
    private int mSolverTop;
    ConstraintAnchor mTop;
    boolean mTopHasCentered;
    private String mType;
    float mVerticalBiasPercent;
    boolean mVerticalChainFixedPosition;
    int mVerticalChainStyle;
    DimensionBehaviour mVerticalDimensionBehaviour;
    ConstraintWidget mVerticalNextWidget;
    public int mVerticalResolution;
    float mVerticalWeight;
    boolean mVerticalWrapVisited;
    private int mVisibility;
    int mWidth;
    private int mWrapHeight;
    private int mWrapWidth;

    /* renamed from: mX */
    protected int f10mX;

    /* renamed from: mY */
    protected int f11mY;

    public enum ContentAlignment {
        BEGIN,
        MIDDLE,
        END,
        TOP,
        VERTICAL_MIDDLE,
        BOTTOM,
        LEFT,
        RIGHT
    }

    public enum DimensionBehaviour {
        FIXED,
        WRAP_CONTENT,
        MATCH_CONSTRAINT,
        MATCH_PARENT
    }

    public void connectedTo(ConstraintWidget constraintWidget) {
    }

    public void reset() {
        this.mLeft.reset();
        this.mTop.reset();
        this.mRight.reset();
        this.mBottom.reset();
        this.mBaseline.reset();
        this.mCenterX.reset();
        this.mCenterY.reset();
        this.mCenter.reset();
        this.mParent = null;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDimensionRatio = 0.0f;
        this.mDimensionRatioSide = -1;
        this.f10mX = 0;
        this.f11mY = 0;
        this.mDrawX = 0;
        this.mDrawY = 0;
        this.mDrawWidth = 0;
        this.mDrawHeight = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBaselineDistance = 0;
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mWrapWidth = 0;
        this.mWrapHeight = 0;
        this.mHorizontalBiasPercent = DEFAULT_BIAS;
        this.mVerticalBiasPercent = DEFAULT_BIAS;
        this.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mCompanionWidget = null;
        this.mContainerItemSkip = 0;
        this.mVisibility = 0;
        this.mDebugName = null;
        this.mType = null;
        this.mHorizontalWrapVisited = false;
        this.mVerticalWrapVisited = false;
        this.mHorizontalChainStyle = 0;
        this.mVerticalChainStyle = 0;
        this.mHorizontalChainFixedPosition = false;
        this.mVerticalChainFixedPosition = false;
        this.mHorizontalWeight = 0.0f;
        this.mVerticalWeight = 0.0f;
        this.mHorizontalResolution = -1;
        this.mVerticalResolution = -1;
    }

    public ConstraintWidget() {
        this.mHorizontalResolution = -1;
        this.mVerticalResolution = -1;
        this.mMatchConstraintDefaultWidth = 0;
        this.mMatchConstraintDefaultHeight = 0;
        this.mMatchConstraintMinWidth = 0;
        this.mMatchConstraintMaxWidth = 0;
        this.mMatchConstraintMinHeight = 0;
        this.mMatchConstraintMaxHeight = 0;
        this.mLeft = new ConstraintAnchor(this, Type.LEFT);
        this.mTop = new ConstraintAnchor(this, Type.TOP);
        this.mRight = new ConstraintAnchor(this, Type.RIGHT);
        this.mBottom = new ConstraintAnchor(this, Type.BOTTOM);
        this.mBaseline = new ConstraintAnchor(this, Type.BASELINE);
        this.mCenterX = new ConstraintAnchor(this, Type.CENTER_X);
        this.mCenterY = new ConstraintAnchor(this, Type.CENTER_Y);
        this.mCenter = new ConstraintAnchor(this, Type.CENTER);
        this.mAnchors = new ArrayList<>();
        this.mParent = null;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDimensionRatio = 0.0f;
        this.mDimensionRatioSide = -1;
        this.mSolverLeft = 0;
        this.mSolverTop = 0;
        this.mSolverRight = 0;
        this.mSolverBottom = 0;
        this.f10mX = 0;
        this.f11mY = 0;
        this.mDrawX = 0;
        this.mDrawY = 0;
        this.mDrawWidth = 0;
        this.mDrawHeight = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBaselineDistance = 0;
        this.mHorizontalBiasPercent = DEFAULT_BIAS;
        this.mVerticalBiasPercent = DEFAULT_BIAS;
        this.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mContainerItemSkip = 0;
        this.mVisibility = 0;
        this.mDebugName = null;
        this.mType = null;
        this.mHorizontalChainStyle = 0;
        this.mVerticalChainStyle = 0;
        this.mHorizontalWeight = 0.0f;
        this.mVerticalWeight = 0.0f;
        this.mHorizontalNextWidget = null;
        this.mVerticalNextWidget = null;
        addAnchors();
    }

    public ConstraintWidget(int i, int i2, int i3, int i4) {
        this.mHorizontalResolution = -1;
        this.mVerticalResolution = -1;
        this.mMatchConstraintDefaultWidth = 0;
        this.mMatchConstraintDefaultHeight = 0;
        this.mMatchConstraintMinWidth = 0;
        this.mMatchConstraintMaxWidth = 0;
        this.mMatchConstraintMinHeight = 0;
        this.mMatchConstraintMaxHeight = 0;
        this.mLeft = new ConstraintAnchor(this, Type.LEFT);
        this.mTop = new ConstraintAnchor(this, Type.TOP);
        this.mRight = new ConstraintAnchor(this, Type.RIGHT);
        this.mBottom = new ConstraintAnchor(this, Type.BOTTOM);
        this.mBaseline = new ConstraintAnchor(this, Type.BASELINE);
        this.mCenterX = new ConstraintAnchor(this, Type.CENTER_X);
        this.mCenterY = new ConstraintAnchor(this, Type.CENTER_Y);
        this.mCenter = new ConstraintAnchor(this, Type.CENTER);
        this.mAnchors = new ArrayList<>();
        this.mParent = null;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDimensionRatio = 0.0f;
        this.mDimensionRatioSide = -1;
        this.mSolverLeft = 0;
        this.mSolverTop = 0;
        this.mSolverRight = 0;
        this.mSolverBottom = 0;
        this.f10mX = 0;
        this.f11mY = 0;
        this.mDrawX = 0;
        this.mDrawY = 0;
        this.mDrawWidth = 0;
        this.mDrawHeight = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBaselineDistance = 0;
        this.mHorizontalBiasPercent = DEFAULT_BIAS;
        this.mVerticalBiasPercent = DEFAULT_BIAS;
        this.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mContainerItemSkip = 0;
        this.mVisibility = 0;
        this.mDebugName = null;
        this.mType = null;
        this.mHorizontalChainStyle = 0;
        this.mVerticalChainStyle = 0;
        this.mHorizontalWeight = 0.0f;
        this.mVerticalWeight = 0.0f;
        this.mHorizontalNextWidget = null;
        this.mVerticalNextWidget = null;
        this.f10mX = i;
        this.f11mY = i2;
        this.mWidth = i3;
        this.mHeight = i4;
        addAnchors();
        forceUpdateDrawPosition();
    }

    public ConstraintWidget(int i, int i2) {
        this(0, 0, i, i2);
    }

    public void resetSolverVariables(Cache cache) {
        this.mLeft.resetSolverVariable(cache);
        this.mTop.resetSolverVariable(cache);
        this.mRight.resetSolverVariable(cache);
        this.mBottom.resetSolverVariable(cache);
        this.mBaseline.resetSolverVariable(cache);
        this.mCenter.resetSolverVariable(cache);
        this.mCenterX.resetSolverVariable(cache);
        this.mCenterY.resetSolverVariable(cache);
    }

    public void resetGroups() {
        int size = this.mAnchors.size();
        for (int i = 0; i < size; i++) {
            ((ConstraintAnchor) this.mAnchors.get(i)).mGroup = Integer.MAX_VALUE;
        }
    }

    private void addAnchors() {
        this.mAnchors.add(this.mLeft);
        this.mAnchors.add(this.mTop);
        this.mAnchors.add(this.mRight);
        this.mAnchors.add(this.mBottom);
        this.mAnchors.add(this.mCenterX);
        this.mAnchors.add(this.mCenterY);
        this.mAnchors.add(this.mBaseline);
    }

    public boolean isRoot() {
        return this.mParent == null;
    }

    public boolean isRootContainer() {
        return (this instanceof ConstraintWidgetContainer) && (this.mParent == null || !(this.mParent instanceof ConstraintWidgetContainer));
    }

    public boolean isInsideConstraintLayout() {
        ConstraintWidget parent = getParent();
        if (parent == null) {
            return false;
        }
        while (parent != null) {
            if (parent instanceof ConstraintWidgetContainer) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    public boolean hasAncestor(ConstraintWidget constraintWidget) {
        ConstraintWidget parent = getParent();
        if (parent == constraintWidget) {
            return true;
        }
        if (parent == constraintWidget.getParent()) {
            return false;
        }
        while (parent != null) {
            if (parent == constraintWidget || parent == constraintWidget.getParent()) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    public WidgetContainer getRootWidgetContainer() {
        ConstraintWidget constraintWidget = this;
        while (constraintWidget.getParent() != null) {
            constraintWidget = constraintWidget.getParent();
        }
        if (constraintWidget instanceof WidgetContainer) {
            return (WidgetContainer) constraintWidget;
        }
        return null;
    }

    public ConstraintWidget getParent() {
        return this.mParent;
    }

    public void setParent(ConstraintWidget constraintWidget) {
        this.mParent = constraintWidget;
    }

    public String getType() {
        return this.mType;
    }

    public void setType(String str) {
        this.mType = str;
    }

    public void setVisibility(int i) {
        this.mVisibility = i;
    }

    public int getVisibility() {
        return this.mVisibility;
    }

    public String getDebugName() {
        return this.mDebugName;
    }

    public void setDebugName(String str) {
        this.mDebugName = str;
    }

    public void setDebugSolverName(LinearSystem linearSystem, String str) {
        this.mDebugName = str;
        SolverVariable createObjectVariable = linearSystem.createObjectVariable(this.mLeft);
        SolverVariable createObjectVariable2 = linearSystem.createObjectVariable(this.mTop);
        SolverVariable createObjectVariable3 = linearSystem.createObjectVariable(this.mRight);
        SolverVariable createObjectVariable4 = linearSystem.createObjectVariable(this.mBottom);
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(".left");
        createObjectVariable.setName(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append(".top");
        createObjectVariable2.setName(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append(".right");
        createObjectVariable3.setName(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(str);
        sb4.append(".bottom");
        createObjectVariable4.setName(sb4.toString());
        if (this.mBaselineDistance > 0) {
            SolverVariable createObjectVariable5 = linearSystem.createObjectVariable(this.mBaseline);
            StringBuilder sb5 = new StringBuilder();
            sb5.append(str);
            sb5.append(".baseline");
            createObjectVariable5.setName(sb5.toString());
        }
    }

    public String toString() {
        String str;
        String str2;
        StringBuilder sb = new StringBuilder();
        if (this.mType != null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("type: ");
            sb2.append(this.mType);
            sb2.append(Token.SEPARATOR);
            str = sb2.toString();
        } else {
            str = "";
        }
        sb.append(str);
        if (this.mDebugName != null) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("id: ");
            sb3.append(this.mDebugName);
            sb3.append(Token.SEPARATOR);
            str2 = sb3.toString();
        } else {
            str2 = "";
        }
        sb.append(str2);
        sb.append("(");
        sb.append(this.f10mX);
        sb.append(", ");
        sb.append(this.f11mY);
        sb.append(") - (");
        sb.append(this.mWidth);
        sb.append(" x ");
        sb.append(this.mHeight);
        sb.append(")");
        sb.append(" wrap: (");
        sb.append(this.mWrapWidth);
        sb.append(" x ");
        sb.append(this.mWrapHeight);
        sb.append(")");
        return sb.toString();
    }

    /* access modifiers changed from: 0000 */
    public int getInternalDrawX() {
        return this.mDrawX;
    }

    /* access modifiers changed from: 0000 */
    public int getInternalDrawY() {
        return this.mDrawY;
    }

    public int getInternalDrawRight() {
        return this.mDrawX + this.mDrawWidth;
    }

    public int getInternalDrawBottom() {
        return this.mDrawY + this.mDrawHeight;
    }

    public int getX() {
        return this.f10mX;
    }

    public int getY() {
        return this.f11mY;
    }

    public int getWidth() {
        if (this.mVisibility == 8) {
            return 0;
        }
        return this.mWidth;
    }

    public int getOptimizerWrapWidth() {
        int i;
        int i2 = this.mWidth;
        if (this.mHorizontalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
            return i2;
        }
        if (this.mMatchConstraintDefaultWidth == 1) {
            i = Math.max(this.mMatchConstraintMinWidth, i2);
        } else if (this.mMatchConstraintMinWidth > 0) {
            i = this.mMatchConstraintMinWidth;
            this.mWidth = i;
        } else {
            i = 0;
        }
        return (this.mMatchConstraintMaxWidth <= 0 || this.mMatchConstraintMaxWidth >= i) ? i : this.mMatchConstraintMaxWidth;
    }

    public int getOptimizerWrapHeight() {
        int i;
        int i2 = this.mHeight;
        if (this.mVerticalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
            return i2;
        }
        if (this.mMatchConstraintDefaultHeight == 1) {
            i = Math.max(this.mMatchConstraintMinHeight, i2);
        } else if (this.mMatchConstraintMinHeight > 0) {
            i = this.mMatchConstraintMinHeight;
            this.mHeight = i;
        } else {
            i = 0;
        }
        return (this.mMatchConstraintMaxHeight <= 0 || this.mMatchConstraintMaxHeight >= i) ? i : this.mMatchConstraintMaxHeight;
    }

    public int getWrapWidth() {
        return this.mWrapWidth;
    }

    public int getHeight() {
        if (this.mVisibility == 8) {
            return 0;
        }
        return this.mHeight;
    }

    public int getWrapHeight() {
        return this.mWrapHeight;
    }

    public int getDrawX() {
        return this.mDrawX + this.mOffsetX;
    }

    public int getDrawY() {
        return this.mDrawY + this.mOffsetY;
    }

    public int getDrawWidth() {
        return this.mDrawWidth;
    }

    public int getDrawHeight() {
        return this.mDrawHeight;
    }

    public int getDrawBottom() {
        return getDrawY() + this.mDrawHeight;
    }

    public int getDrawRight() {
        return getDrawX() + this.mDrawWidth;
    }

    /* access modifiers changed from: protected */
    public int getRootX() {
        return this.f10mX + this.mOffsetX;
    }

    /* access modifiers changed from: protected */
    public int getRootY() {
        return this.f11mY + this.mOffsetY;
    }

    public int getMinWidth() {
        return this.mMinWidth;
    }

    public int getMinHeight() {
        return this.mMinHeight;
    }

    public int getLeft() {
        return getX();
    }

    public int getTop() {
        return getY();
    }

    public int getRight() {
        return getX() + this.mWidth;
    }

    public int getBottom() {
        return getY() + this.mHeight;
    }

    public float getHorizontalBiasPercent() {
        return this.mHorizontalBiasPercent;
    }

    public float getVerticalBiasPercent() {
        return this.mVerticalBiasPercent;
    }

    public boolean hasBaseline() {
        return this.mBaselineDistance > 0;
    }

    public int getBaselineDistance() {
        return this.mBaselineDistance;
    }

    public Object getCompanionWidget() {
        return this.mCompanionWidget;
    }

    public ArrayList<ConstraintAnchor> getAnchors() {
        return this.mAnchors;
    }

    public void setX(int i) {
        this.f10mX = i;
    }

    public void setY(int i) {
        this.f11mY = i;
    }

    public void setOrigin(int i, int i2) {
        this.f10mX = i;
        this.f11mY = i2;
    }

    public void setOffset(int i, int i2) {
        this.mOffsetX = i;
        this.mOffsetY = i2;
    }

    public void setGoneMargin(Type type, int i) {
        switch (type) {
            case LEFT:
                this.mLeft.mGoneMargin = i;
                return;
            case TOP:
                this.mTop.mGoneMargin = i;
                return;
            case RIGHT:
                this.mRight.mGoneMargin = i;
                return;
            case BOTTOM:
                this.mBottom.mGoneMargin = i;
                return;
            default:
                return;
        }
    }

    public void updateDrawPosition() {
        int i = this.f10mX;
        int i2 = this.f11mY;
        int i3 = this.f10mX + this.mWidth;
        int i4 = this.f11mY + this.mHeight;
        this.mDrawX = i;
        this.mDrawY = i2;
        this.mDrawWidth = i3 - i;
        this.mDrawHeight = i4 - i2;
    }

    public void forceUpdateDrawPosition() {
        int i = this.f10mX;
        int i2 = this.f11mY;
        int i3 = this.f10mX + this.mWidth;
        int i4 = this.f11mY + this.mHeight;
        this.mDrawX = i;
        this.mDrawY = i2;
        this.mDrawWidth = i3 - i;
        this.mDrawHeight = i4 - i2;
    }

    public void setDrawOrigin(int i, int i2) {
        this.mDrawX = i - this.mOffsetX;
        this.mDrawY = i2 - this.mOffsetY;
        this.f10mX = this.mDrawX;
        this.f11mY = this.mDrawY;
    }

    public void setDrawX(int i) {
        this.mDrawX = i - this.mOffsetX;
        this.f10mX = this.mDrawX;
    }

    public void setDrawY(int i) {
        this.mDrawY = i - this.mOffsetY;
        this.f11mY = this.mDrawY;
    }

    public void setDrawWidth(int i) {
        this.mDrawWidth = i;
    }

    public void setDrawHeight(int i) {
        this.mDrawHeight = i;
    }

    public void setWidth(int i) {
        this.mWidth = i;
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
    }

    public void setHeight(int i) {
        this.mHeight = i;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
    }

    public void setHorizontalMatchStyle(int i, int i2, int i3) {
        this.mMatchConstraintDefaultWidth = i;
        this.mMatchConstraintMinWidth = i2;
        this.mMatchConstraintMaxWidth = i3;
    }

    public void setVerticalMatchStyle(int i, int i2, int i3) {
        this.mMatchConstraintDefaultHeight = i;
        this.mMatchConstraintMinHeight = i2;
        this.mMatchConstraintMaxHeight = i3;
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x0089  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setDimensionRatio(java.lang.String r9) {
        /*
            r8 = this;
            r0 = 0
            if (r9 == 0) goto L_0x008e
            int r1 = r9.length()
            if (r1 != 0) goto L_0x000b
            goto L_0x008e
        L_0x000b:
            r1 = -1
            int r2 = r9.length()
            r3 = 44
            int r3 = r9.indexOf(r3)
            r4 = 0
            r5 = 1
            if (r3 <= 0) goto L_0x0037
            int r6 = r2 + -1
            if (r3 >= r6) goto L_0x0037
            java.lang.String r6 = r9.substring(r4, r3)
            java.lang.String r7 = "W"
            boolean r7 = r6.equalsIgnoreCase(r7)
            if (r7 == 0) goto L_0x002c
            r1 = 0
            goto L_0x0035
        L_0x002c:
            java.lang.String r4 = "H"
            boolean r4 = r6.equalsIgnoreCase(r4)
            if (r4 == 0) goto L_0x0035
            r1 = 1
        L_0x0035:
            int r4 = r3 + 1
        L_0x0037:
            r3 = 58
            int r3 = r9.indexOf(r3)
            if (r3 < 0) goto L_0x0075
            int r2 = r2 - r5
            if (r3 >= r2) goto L_0x0075
            java.lang.String r2 = r9.substring(r4, r3)
            int r3 = r3 + r5
            java.lang.String r9 = r9.substring(r3)
            int r3 = r2.length()
            if (r3 <= 0) goto L_0x0084
            int r3 = r9.length()
            if (r3 <= 0) goto L_0x0084
            float r2 = java.lang.Float.parseFloat(r2)     // Catch:{ NumberFormatException -> 0x0084 }
            float r9 = java.lang.Float.parseFloat(r9)     // Catch:{ NumberFormatException -> 0x0084 }
            int r3 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r3 <= 0) goto L_0x0084
            int r3 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r3 <= 0) goto L_0x0084
            if (r1 != r5) goto L_0x006f
            float r9 = r9 / r2
            float r9 = java.lang.Math.abs(r9)     // Catch:{ NumberFormatException -> 0x0084 }
            goto L_0x0085
        L_0x006f:
            float r2 = r2 / r9
            float r9 = java.lang.Math.abs(r2)     // Catch:{ NumberFormatException -> 0x0084 }
            goto L_0x0085
        L_0x0075:
            java.lang.String r9 = r9.substring(r4)
            int r2 = r9.length()
            if (r2 <= 0) goto L_0x0084
            float r9 = java.lang.Float.parseFloat(r9)     // Catch:{ NumberFormatException -> 0x0084 }
            goto L_0x0085
        L_0x0084:
            r9 = 0
        L_0x0085:
            int r0 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x008d
            r8.mDimensionRatio = r9
            r8.mDimensionRatioSide = r1
        L_0x008d:
            return
        L_0x008e:
            r8.mDimensionRatio = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.ConstraintWidget.setDimensionRatio(java.lang.String):void");
    }

    public void setDimensionRatio(float f, int i) {
        this.mDimensionRatio = f;
        this.mDimensionRatioSide = i;
    }

    public float getDimensionRatio() {
        return this.mDimensionRatio;
    }

    public int getDimensionRatioSide() {
        return this.mDimensionRatioSide;
    }

    public void setHorizontalBiasPercent(float f) {
        this.mHorizontalBiasPercent = f;
    }

    public void setVerticalBiasPercent(float f) {
        this.mVerticalBiasPercent = f;
    }

    public void setMinWidth(int i) {
        if (i < 0) {
            this.mMinWidth = 0;
        } else {
            this.mMinWidth = i;
        }
    }

    public void setMinHeight(int i) {
        if (i < 0) {
            this.mMinHeight = 0;
        } else {
            this.mMinHeight = i;
        }
    }

    public void setWrapWidth(int i) {
        this.mWrapWidth = i;
    }

    public void setWrapHeight(int i) {
        this.mWrapHeight = i;
    }

    public void setDimension(int i, int i2) {
        this.mWidth = i;
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
        this.mHeight = i2;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
    }

    public void setFrame(int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        int i6 = i4 - i2;
        this.f10mX = i;
        this.f11mY = i2;
        if (this.mVisibility == 8) {
            this.mWidth = 0;
            this.mHeight = 0;
            return;
        }
        if (this.mHorizontalDimensionBehaviour == DimensionBehaviour.FIXED && i5 < this.mWidth) {
            i5 = this.mWidth;
        }
        if (this.mVerticalDimensionBehaviour == DimensionBehaviour.FIXED && i6 < this.mHeight) {
            i6 = this.mHeight;
        }
        this.mWidth = i5;
        this.mHeight = i6;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
    }

    public void setHorizontalDimension(int i, int i2) {
        this.f10mX = i;
        this.mWidth = i2 - i;
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
    }

    public void setVerticalDimension(int i, int i2) {
        this.f11mY = i;
        this.mHeight = i2 - i;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
    }

    public void setBaselineDistance(int i) {
        this.mBaselineDistance = i;
    }

    public void setCompanionWidget(Object obj) {
        this.mCompanionWidget = obj;
    }

    public void setContainerItemSkip(int i) {
        if (i >= 0) {
            this.mContainerItemSkip = i;
        } else {
            this.mContainerItemSkip = 0;
        }
    }

    public int getContainerItemSkip() {
        return this.mContainerItemSkip;
    }

    public void setHorizontalWeight(float f) {
        this.mHorizontalWeight = f;
    }

    public void setVerticalWeight(float f) {
        this.mVerticalWeight = f;
    }

    public void setHorizontalChainStyle(int i) {
        this.mHorizontalChainStyle = i;
    }

    public int getHorizontalChainStyle() {
        return this.mHorizontalChainStyle;
    }

    public void setVerticalChainStyle(int i) {
        this.mVerticalChainStyle = i;
    }

    public int getVerticalChainStyle() {
        return this.mVerticalChainStyle;
    }

    public void immediateConnect(Type type, ConstraintWidget constraintWidget, Type type2, int i, int i2) {
        getAnchor(type).connect(constraintWidget.getAnchor(type2), i, i2, Strength.STRONG, 0, true);
    }

    public void connect(ConstraintAnchor constraintAnchor, ConstraintAnchor constraintAnchor2, int i, int i2) {
        connect(constraintAnchor, constraintAnchor2, i, Strength.STRONG, i2);
    }

    public void connect(ConstraintAnchor constraintAnchor, ConstraintAnchor constraintAnchor2, int i) {
        connect(constraintAnchor, constraintAnchor2, i, Strength.STRONG, 0);
    }

    public void connect(ConstraintAnchor constraintAnchor, ConstraintAnchor constraintAnchor2, int i, Strength strength, int i2) {
        if (constraintAnchor.getOwner() == this) {
            connect(constraintAnchor.getType(), constraintAnchor2.getOwner(), constraintAnchor2.getType(), i, strength, i2);
        }
    }

    public void connect(Type type, ConstraintWidget constraintWidget, Type type2, int i) {
        connect(type, constraintWidget, type2, i, Strength.STRONG);
    }

    public void connect(Type type, ConstraintWidget constraintWidget, Type type2) {
        connect(type, constraintWidget, type2, 0, Strength.STRONG);
    }

    public void connect(Type type, ConstraintWidget constraintWidget, Type type2, int i, Strength strength) {
        connect(type, constraintWidget, type2, i, strength, 0);
    }

    public void connect(Type type, ConstraintWidget constraintWidget, Type type2, int i, Strength strength, int i2) {
        boolean z;
        Type type3 = type;
        ConstraintWidget constraintWidget2 = constraintWidget;
        Type type4 = type2;
        int i3 = i2;
        int i4 = 0;
        if (type3 == Type.CENTER) {
            if (type4 == Type.CENTER) {
                ConstraintAnchor anchor = getAnchor(Type.LEFT);
                ConstraintAnchor anchor2 = getAnchor(Type.RIGHT);
                ConstraintAnchor anchor3 = getAnchor(Type.TOP);
                ConstraintAnchor anchor4 = getAnchor(Type.BOTTOM);
                boolean z2 = true;
                if ((anchor == null || !anchor.isConnected()) && (anchor2 == null || !anchor2.isConnected())) {
                    ConstraintWidget constraintWidget3 = constraintWidget;
                    Strength strength2 = strength;
                    int i5 = i2;
                    connect(Type.LEFT, constraintWidget3, Type.LEFT, 0, strength2, i5);
                    connect(Type.RIGHT, constraintWidget3, Type.RIGHT, 0, strength2, i5);
                    z = true;
                } else {
                    z = false;
                }
                if ((anchor3 == null || !anchor3.isConnected()) && (anchor4 == null || !anchor4.isConnected())) {
                    ConstraintWidget constraintWidget4 = constraintWidget;
                    Strength strength3 = strength;
                    int i6 = i2;
                    connect(Type.TOP, constraintWidget4, Type.TOP, 0, strength3, i6);
                    connect(Type.BOTTOM, constraintWidget4, Type.BOTTOM, 0, strength3, i6);
                } else {
                    z2 = false;
                }
                if (z && z2) {
                    getAnchor(Type.CENTER).connect(constraintWidget2.getAnchor(Type.CENTER), 0, i3);
                } else if (z) {
                    getAnchor(Type.CENTER_X).connect(constraintWidget2.getAnchor(Type.CENTER_X), 0, i3);
                } else if (z2) {
                    getAnchor(Type.CENTER_Y).connect(constraintWidget2.getAnchor(Type.CENTER_Y), 0, i3);
                }
            } else if (type4 == Type.LEFT || type4 == Type.RIGHT) {
                connect(Type.LEFT, constraintWidget, type2, 0, strength, i2);
                try {
                    connect(Type.RIGHT, constraintWidget, type2, 0, strength, i2);
                    getAnchor(Type.CENTER).connect(constraintWidget.getAnchor(type2), 0, i3);
                } catch (Throwable th) {
                    throw th;
                }
            } else if (type4 == Type.TOP || type4 == Type.BOTTOM) {
                ConstraintWidget constraintWidget5 = constraintWidget;
                Type type5 = type2;
                Strength strength4 = strength;
                int i7 = i2;
                connect(Type.TOP, constraintWidget5, type5, 0, strength4, i7);
                connect(Type.BOTTOM, constraintWidget5, type5, 0, strength4, i7);
                getAnchor(Type.CENTER).connect(constraintWidget.getAnchor(type2), 0, i3);
            }
        } else if (type3 == Type.CENTER_X && (type4 == Type.LEFT || type4 == Type.RIGHT)) {
            ConstraintAnchor anchor5 = getAnchor(Type.LEFT);
            ConstraintAnchor anchor6 = constraintWidget.getAnchor(type2);
            ConstraintAnchor anchor7 = getAnchor(Type.RIGHT);
            anchor5.connect(anchor6, 0, i3);
            anchor7.connect(anchor6, 0, i3);
            getAnchor(Type.CENTER_X).connect(anchor6, 0, i3);
        } else if (type3 == Type.CENTER_Y && (type4 == Type.TOP || type4 == Type.BOTTOM)) {
            ConstraintAnchor anchor8 = constraintWidget.getAnchor(type2);
            getAnchor(Type.TOP).connect(anchor8, 0, i3);
            getAnchor(Type.BOTTOM).connect(anchor8, 0, i3);
            getAnchor(Type.CENTER_Y).connect(anchor8, 0, i3);
        } else if (type3 == Type.CENTER_X && type4 == Type.CENTER_X) {
            getAnchor(Type.LEFT).connect(constraintWidget2.getAnchor(Type.LEFT), 0, i3);
            getAnchor(Type.RIGHT).connect(constraintWidget2.getAnchor(Type.RIGHT), 0, i3);
            getAnchor(Type.CENTER_X).connect(constraintWidget.getAnchor(type2), 0, i3);
        } else if (type3 == Type.CENTER_Y && type4 == Type.CENTER_Y) {
            getAnchor(Type.TOP).connect(constraintWidget2.getAnchor(Type.TOP), 0, i3);
            getAnchor(Type.BOTTOM).connect(constraintWidget2.getAnchor(Type.BOTTOM), 0, i3);
            getAnchor(Type.CENTER_Y).connect(constraintWidget.getAnchor(type2), 0, i3);
        } else {
            ConstraintAnchor anchor9 = getAnchor(type);
            ConstraintAnchor anchor10 = constraintWidget.getAnchor(type2);
            if (anchor9.isValidConnection(anchor10)) {
                if (type3 == Type.BASELINE) {
                    ConstraintAnchor anchor11 = getAnchor(Type.TOP);
                    ConstraintAnchor anchor12 = getAnchor(Type.BOTTOM);
                    if (anchor11 != null) {
                        anchor11.reset();
                    }
                    if (anchor12 != null) {
                        anchor12.reset();
                    }
                } else {
                    if (type3 == Type.TOP || type3 == Type.BOTTOM) {
                        ConstraintAnchor anchor13 = getAnchor(Type.BASELINE);
                        if (anchor13 != null) {
                            anchor13.reset();
                        }
                        ConstraintAnchor anchor14 = getAnchor(Type.CENTER);
                        if (anchor14.getTarget() != anchor10) {
                            anchor14.reset();
                        }
                        ConstraintAnchor opposite = getAnchor(type).getOpposite();
                        ConstraintAnchor anchor15 = getAnchor(Type.CENTER_Y);
                        if (anchor15.isConnected()) {
                            opposite.reset();
                            anchor15.reset();
                        }
                    } else if (type3 == Type.LEFT || type3 == Type.RIGHT) {
                        ConstraintAnchor anchor16 = getAnchor(Type.CENTER);
                        if (anchor16.getTarget() != anchor10) {
                            anchor16.reset();
                        }
                        ConstraintAnchor opposite2 = getAnchor(type).getOpposite();
                        ConstraintAnchor anchor17 = getAnchor(Type.CENTER_X);
                        if (anchor17.isConnected()) {
                            opposite2.reset();
                            anchor17.reset();
                        }
                    }
                    i4 = i;
                }
                anchor9.connect(anchor10, i4, strength, i3);
                anchor10.getOwner().connectedTo(anchor9.getOwner());
            }
        }
    }

    public void resetAllConstraints() {
        resetAnchors();
        setVerticalBiasPercent(DEFAULT_BIAS);
        setHorizontalBiasPercent(DEFAULT_BIAS);
        if (!(this instanceof ConstraintWidgetContainer)) {
            if (getHorizontalDimensionBehaviour() == DimensionBehaviour.MATCH_CONSTRAINT) {
                if (getWidth() == getWrapWidth()) {
                    setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
                } else if (getWidth() > getMinWidth()) {
                    setHorizontalDimensionBehaviour(DimensionBehaviour.FIXED);
                }
            }
            if (getVerticalDimensionBehaviour() == DimensionBehaviour.MATCH_CONSTRAINT) {
                if (getHeight() == getWrapHeight()) {
                    setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
                } else if (getHeight() > getMinHeight()) {
                    setVerticalDimensionBehaviour(DimensionBehaviour.FIXED);
                }
            }
        }
    }

    public void resetAnchor(ConstraintAnchor constraintAnchor) {
        if (getParent() == null || !(getParent() instanceof ConstraintWidgetContainer) || !((ConstraintWidgetContainer) getParent()).handlesInternalConstraints()) {
            ConstraintAnchor anchor = getAnchor(Type.LEFT);
            ConstraintAnchor anchor2 = getAnchor(Type.RIGHT);
            ConstraintAnchor anchor3 = getAnchor(Type.TOP);
            ConstraintAnchor anchor4 = getAnchor(Type.BOTTOM);
            ConstraintAnchor anchor5 = getAnchor(Type.CENTER);
            ConstraintAnchor anchor6 = getAnchor(Type.CENTER_X);
            ConstraintAnchor anchor7 = getAnchor(Type.CENTER_Y);
            if (constraintAnchor == anchor5) {
                if (anchor.isConnected() && anchor2.isConnected() && anchor.getTarget() == anchor2.getTarget()) {
                    anchor.reset();
                    anchor2.reset();
                }
                if (anchor3.isConnected() && anchor4.isConnected() && anchor3.getTarget() == anchor4.getTarget()) {
                    anchor3.reset();
                    anchor4.reset();
                }
                this.mHorizontalBiasPercent = 0.5f;
                this.mVerticalBiasPercent = 0.5f;
            } else if (constraintAnchor == anchor6) {
                if (anchor.isConnected() && anchor2.isConnected() && anchor.getTarget().getOwner() == anchor2.getTarget().getOwner()) {
                    anchor.reset();
                    anchor2.reset();
                }
                this.mHorizontalBiasPercent = 0.5f;
            } else if (constraintAnchor == anchor7) {
                if (anchor3.isConnected() && anchor4.isConnected() && anchor3.getTarget().getOwner() == anchor4.getTarget().getOwner()) {
                    anchor3.reset();
                    anchor4.reset();
                }
                this.mVerticalBiasPercent = 0.5f;
            } else if (constraintAnchor == anchor || constraintAnchor == anchor2) {
                if (anchor.isConnected() && anchor.getTarget() == anchor2.getTarget()) {
                    anchor5.reset();
                }
            } else if ((constraintAnchor == anchor3 || constraintAnchor == anchor4) && anchor3.isConnected() && anchor3.getTarget() == anchor4.getTarget()) {
                anchor5.reset();
            }
            constraintAnchor.reset();
        }
    }

    public void resetAnchors() {
        ConstraintWidget parent = getParent();
        if (parent == null || !(parent instanceof ConstraintWidgetContainer) || !((ConstraintWidgetContainer) getParent()).handlesInternalConstraints()) {
            int size = this.mAnchors.size();
            for (int i = 0; i < size; i++) {
                ((ConstraintAnchor) this.mAnchors.get(i)).reset();
            }
        }
    }

    public void resetAnchors(int i) {
        ConstraintWidget parent = getParent();
        if (parent == null || !(parent instanceof ConstraintWidgetContainer) || !((ConstraintWidgetContainer) getParent()).handlesInternalConstraints()) {
            int size = this.mAnchors.size();
            for (int i2 = 0; i2 < size; i2++) {
                ConstraintAnchor constraintAnchor = (ConstraintAnchor) this.mAnchors.get(i2);
                if (i == constraintAnchor.getConnectionCreator()) {
                    if (constraintAnchor.isVerticalAnchor()) {
                        setVerticalBiasPercent(DEFAULT_BIAS);
                    } else {
                        setHorizontalBiasPercent(DEFAULT_BIAS);
                    }
                    constraintAnchor.reset();
                }
            }
        }
    }

    public void disconnectWidget(ConstraintWidget constraintWidget) {
        ArrayList anchors = getAnchors();
        int size = anchors.size();
        for (int i = 0; i < size; i++) {
            ConstraintAnchor constraintAnchor = (ConstraintAnchor) anchors.get(i);
            if (constraintAnchor.isConnected() && constraintAnchor.getTarget().getOwner() == constraintWidget) {
                constraintAnchor.reset();
            }
        }
    }

    public void disconnectUnlockedWidget(ConstraintWidget constraintWidget) {
        ArrayList anchors = getAnchors();
        int size = anchors.size();
        for (int i = 0; i < size; i++) {
            ConstraintAnchor constraintAnchor = (ConstraintAnchor) anchors.get(i);
            if (constraintAnchor.isConnected() && constraintAnchor.getTarget().getOwner() == constraintWidget && constraintAnchor.getConnectionCreator() == 2) {
                constraintAnchor.reset();
            }
        }
    }

    public ConstraintAnchor getAnchor(Type type) {
        switch (type) {
            case LEFT:
                return this.mLeft;
            case TOP:
                return this.mTop;
            case RIGHT:
                return this.mRight;
            case BOTTOM:
                return this.mBottom;
            case BASELINE:
                return this.mBaseline;
            case CENTER_X:
                return this.mCenterX;
            case CENTER_Y:
                return this.mCenterY;
            case CENTER:
                return this.mCenter;
            default:
                return null;
        }
    }

    public DimensionBehaviour getHorizontalDimensionBehaviour() {
        return this.mHorizontalDimensionBehaviour;
    }

    public DimensionBehaviour getVerticalDimensionBehaviour() {
        return this.mVerticalDimensionBehaviour;
    }

    public void setHorizontalDimensionBehaviour(DimensionBehaviour dimensionBehaviour) {
        this.mHorizontalDimensionBehaviour = dimensionBehaviour;
        if (this.mHorizontalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
            setWidth(this.mWrapWidth);
        }
    }

    public void setVerticalDimensionBehaviour(DimensionBehaviour dimensionBehaviour) {
        this.mVerticalDimensionBehaviour = dimensionBehaviour;
        if (this.mVerticalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
            setHeight(this.mWrapHeight);
        }
    }

    public boolean isInHorizontalChain() {
        return (this.mLeft.mTarget != null && this.mLeft.mTarget.mTarget == this.mLeft) || (this.mRight.mTarget != null && this.mRight.mTarget.mTarget == this.mRight);
    }

    public ConstraintWidget getHorizontalChainControlWidget() {
        ConstraintAnchor constraintAnchor;
        ConstraintWidget constraintWidget;
        ConstraintAnchor constraintAnchor2;
        if (!isInHorizontalChain()) {
            return null;
        }
        ConstraintWidget constraintWidget2 = this;
        ConstraintWidget constraintWidget3 = null;
        while (constraintWidget3 == null && constraintWidget2 != null) {
            ConstraintAnchor anchor = constraintWidget2.getAnchor(Type.LEFT);
            if (anchor == null) {
                constraintAnchor = null;
            } else {
                constraintAnchor = anchor.getTarget();
            }
            if (constraintAnchor == null) {
                constraintWidget = null;
            } else {
                constraintWidget = constraintAnchor.getOwner();
            }
            if (constraintWidget == getParent()) {
                return constraintWidget2;
            }
            if (constraintWidget == null) {
                constraintAnchor2 = null;
            } else {
                constraintAnchor2 = constraintWidget.getAnchor(Type.RIGHT).getTarget();
            }
            if (constraintAnchor2 == null || constraintAnchor2.getOwner() == constraintWidget2) {
                constraintWidget2 = constraintWidget;
            } else {
                constraintWidget3 = constraintWidget2;
            }
        }
        return constraintWidget3;
    }

    public boolean isInVerticalChain() {
        return (this.mTop.mTarget != null && this.mTop.mTarget.mTarget == this.mTop) || (this.mBottom.mTarget != null && this.mBottom.mTarget.mTarget == this.mBottom);
    }

    public ConstraintWidget getVerticalChainControlWidget() {
        ConstraintAnchor constraintAnchor;
        ConstraintWidget constraintWidget;
        ConstraintAnchor constraintAnchor2;
        if (!isInVerticalChain()) {
            return null;
        }
        ConstraintWidget constraintWidget2 = this;
        ConstraintWidget constraintWidget3 = null;
        while (constraintWidget3 == null && constraintWidget2 != null) {
            ConstraintAnchor anchor = constraintWidget2.getAnchor(Type.TOP);
            if (anchor == null) {
                constraintAnchor = null;
            } else {
                constraintAnchor = anchor.getTarget();
            }
            if (constraintAnchor == null) {
                constraintWidget = null;
            } else {
                constraintWidget = constraintAnchor.getOwner();
            }
            if (constraintWidget == getParent()) {
                return constraintWidget2;
            }
            if (constraintWidget == null) {
                constraintAnchor2 = null;
            } else {
                constraintAnchor2 = constraintWidget.getAnchor(Type.BOTTOM).getTarget();
            }
            if (constraintAnchor2 == null || constraintAnchor2.getOwner() == constraintWidget2) {
                constraintWidget2 = constraintWidget;
            } else {
                constraintWidget3 = constraintWidget2;
            }
        }
        return constraintWidget3;
    }

    public void addToSolver(LinearSystem linearSystem) {
        addToSolver(linearSystem, Integer.MAX_VALUE);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:215:0x0419, code lost:
        if (r14 == -1) goto L_0x041d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x054a, code lost:
        if (r9.mBottom.mGroup == r12) goto L_0x0555;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x05fc, code lost:
        if (r8.mRight.mGroup == r1) goto L_0x0601;
     */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x02d8 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x02e7  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x02f4  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x03ec  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0403 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x0404  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x0428  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x052d  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x05e5  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0673  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addToSolver(android.support.constraint.solver.LinearSystem r44, int r45) {
        /*
            r43 = this;
            r15 = r43
            r14 = r44
            r13 = r45
            r0 = 0
            r12 = 2147483647(0x7fffffff, float:NaN)
            if (r13 == r12) goto L_0x0015
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mLeft
            int r1 = r1.mGroup
            if (r1 != r13) goto L_0x0013
            goto L_0x0015
        L_0x0013:
            r11 = r0
            goto L_0x001c
        L_0x0015:
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mLeft
            android.support.constraint.solver.SolverVariable r1 = r14.createObjectVariable(r1)
            r11 = r1
        L_0x001c:
            if (r13 == r12) goto L_0x0027
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mRight
            int r1 = r1.mGroup
            if (r1 != r13) goto L_0x0025
            goto L_0x0027
        L_0x0025:
            r10 = r0
            goto L_0x002e
        L_0x0027:
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mRight
            android.support.constraint.solver.SolverVariable r1 = r14.createObjectVariable(r1)
            r10 = r1
        L_0x002e:
            if (r13 == r12) goto L_0x0039
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mTop
            int r1 = r1.mGroup
            if (r1 != r13) goto L_0x0037
            goto L_0x0039
        L_0x0037:
            r9 = r0
            goto L_0x0040
        L_0x0039:
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mTop
            android.support.constraint.solver.SolverVariable r1 = r14.createObjectVariable(r1)
            r9 = r1
        L_0x0040:
            if (r13 == r12) goto L_0x004b
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mBottom
            int r1 = r1.mGroup
            if (r1 != r13) goto L_0x0049
            goto L_0x004b
        L_0x0049:
            r8 = r0
            goto L_0x0052
        L_0x004b:
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mBottom
            android.support.constraint.solver.SolverVariable r1 = r14.createObjectVariable(r1)
            r8 = r1
        L_0x0052:
            if (r13 == r12) goto L_0x005d
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mBaseline
            int r1 = r1.mGroup
            if (r1 != r13) goto L_0x005b
            goto L_0x005d
        L_0x005b:
            r7 = r0
            goto L_0x0064
        L_0x005d:
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mBaseline
            android.support.constraint.solver.SolverVariable r0 = r14.createObjectVariable(r0)
            goto L_0x005b
        L_0x0064:
            android.support.constraint.solver.widgets.ConstraintWidget r0 = r15.mParent
            r6 = 0
            r5 = 1
            if (r0 == 0) goto L_0x01d7
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            if (r0 == 0) goto L_0x007a
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mLeft
            if (r0 == r1) goto L_0x008a
        L_0x007a:
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            if (r0 == 0) goto L_0x0093
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mRight
            if (r0 != r1) goto L_0x0093
        L_0x008a:
            android.support.constraint.solver.widgets.ConstraintWidget r0 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r0 = (android.support.constraint.solver.widgets.ConstraintWidgetContainer) r0
            r0.addChain(r15, r6)
            r0 = 1
            goto L_0x0094
        L_0x0093:
            r0 = 0
        L_0x0094:
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r1.mTarget
            if (r1 == 0) goto L_0x00a4
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r1.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r1.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mTop
            if (r1 == r2) goto L_0x00b4
        L_0x00a4:
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r1.mTarget
            if (r1 == 0) goto L_0x00bd
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r1.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r1.mTarget
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mBottom
            if (r1 != r2) goto L_0x00bd
        L_0x00b4:
            android.support.constraint.solver.widgets.ConstraintWidget r1 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintWidgetContainer r1 = (android.support.constraint.solver.widgets.ConstraintWidgetContainer) r1
            r1.addChain(r15, r5)
            r1 = 1
            goto L_0x00be
        L_0x00bd:
            r1 = 0
        L_0x00be:
            android.support.constraint.solver.widgets.ConstraintWidget r2 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r2 = r2.getHorizontalDimensionBehaviour()
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r3 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r2 != r3) goto L_0x0148
            if (r0 != 0) goto L_0x0148
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            if (r2 == 0) goto L_0x00f3
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r2 = r2.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r3 = r15.mParent
            if (r2 == r3) goto L_0x00db
            goto L_0x00f3
        L_0x00db:
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            if (r2 == 0) goto L_0x0109
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r2 = r2.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r3 = r15.mParent
            if (r2 != r3) goto L_0x0109
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor$ConnectionType r3 = android.support.constraint.solver.widgets.ConstraintAnchor.ConnectionType.STRICT
            r2.setConnectionType(r3)
            goto L_0x0109
        L_0x00f3:
            android.support.constraint.solver.widgets.ConstraintWidget r2 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mLeft
            android.support.constraint.solver.SolverVariable r2 = r14.createObjectVariable(r2)
            android.support.constraint.solver.ArrayRow r3 = r44.createRow()
            android.support.constraint.solver.SolverVariable r4 = r44.createSlackVariable()
            r3.createRowGreaterThan(r11, r2, r4, r6)
            r14.addConstraint(r3)
        L_0x0109:
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            if (r2 == 0) goto L_0x0132
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r2 = r2.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r3 = r15.mParent
            if (r2 == r3) goto L_0x011a
            goto L_0x0132
        L_0x011a:
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            if (r2 == 0) goto L_0x0148
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r2 = r2.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r3 = r15.mParent
            if (r2 != r3) goto L_0x0148
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor$ConnectionType r3 = android.support.constraint.solver.widgets.ConstraintAnchor.ConnectionType.STRICT
            r2.setConnectionType(r3)
            goto L_0x0148
        L_0x0132:
            android.support.constraint.solver.widgets.ConstraintWidget r2 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mRight
            android.support.constraint.solver.SolverVariable r2 = r14.createObjectVariable(r2)
            android.support.constraint.solver.ArrayRow r3 = r44.createRow()
            android.support.constraint.solver.SolverVariable r4 = r44.createSlackVariable()
            r3.createRowGreaterThan(r2, r10, r4, r6)
            r14.addConstraint(r3)
        L_0x0148:
            android.support.constraint.solver.widgets.ConstraintWidget r2 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r2 = r2.getVerticalDimensionBehaviour()
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r3 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r2 != r3) goto L_0x01d2
            if (r1 != 0) goto L_0x01d2
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            if (r2 == 0) goto L_0x017d
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r2 = r2.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r3 = r15.mParent
            if (r2 == r3) goto L_0x0165
            goto L_0x017d
        L_0x0165:
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            if (r2 == 0) goto L_0x0193
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r2 = r2.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r3 = r15.mParent
            if (r2 != r3) goto L_0x0193
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor$ConnectionType r3 = android.support.constraint.solver.widgets.ConstraintAnchor.ConnectionType.STRICT
            r2.setConnectionType(r3)
            goto L_0x0193
        L_0x017d:
            android.support.constraint.solver.widgets.ConstraintWidget r2 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTop
            android.support.constraint.solver.SolverVariable r2 = r14.createObjectVariable(r2)
            android.support.constraint.solver.ArrayRow r3 = r44.createRow()
            android.support.constraint.solver.SolverVariable r4 = r44.createSlackVariable()
            r3.createRowGreaterThan(r9, r2, r4, r6)
            r14.addConstraint(r3)
        L_0x0193:
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            if (r2 == 0) goto L_0x01bc
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r2 = r2.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r3 = r15.mParent
            if (r2 == r3) goto L_0x01a4
            goto L_0x01bc
        L_0x01a4:
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            if (r2 == 0) goto L_0x01d2
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mTarget
            android.support.constraint.solver.widgets.ConstraintWidget r2 = r2.mOwner
            android.support.constraint.solver.widgets.ConstraintWidget r3 = r15.mParent
            if (r2 != r3) goto L_0x01d2
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor$ConnectionType r3 = android.support.constraint.solver.widgets.ConstraintAnchor.ConnectionType.STRICT
            r2.setConnectionType(r3)
            goto L_0x01d2
        L_0x01bc:
            android.support.constraint.solver.widgets.ConstraintWidget r2 = r15.mParent
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.mBottom
            android.support.constraint.solver.SolverVariable r2 = r14.createObjectVariable(r2)
            android.support.constraint.solver.ArrayRow r3 = r44.createRow()
            android.support.constraint.solver.SolverVariable r4 = r44.createSlackVariable()
            r3.createRowGreaterThan(r2, r8, r4, r6)
            r14.addConstraint(r3)
        L_0x01d2:
            r16 = r0
            r17 = r1
            goto L_0x01db
        L_0x01d7:
            r16 = 0
            r17 = 0
        L_0x01db:
            int r0 = r15.mWidth
            int r1 = r15.mMinWidth
            if (r0 >= r1) goto L_0x01e3
            int r0 = r15.mMinWidth
        L_0x01e3:
            int r1 = r15.mHeight
            int r2 = r15.mMinHeight
            if (r1 >= r2) goto L_0x01eb
            int r1 = r15.mMinHeight
        L_0x01eb:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r2 = r15.mHorizontalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r3 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r2 == r3) goto L_0x01f3
            r2 = 1
            goto L_0x01f4
        L_0x01f3:
            r2 = 0
        L_0x01f4:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r3 = r15.mVerticalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r4 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r3 == r4) goto L_0x01fc
            r3 = 1
            goto L_0x01fd
        L_0x01fc:
            r3 = 0
        L_0x01fd:
            if (r2 != 0) goto L_0x0214
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mLeft
            if (r4 == 0) goto L_0x0214
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mRight
            if (r4 == 0) goto L_0x0214
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x0213
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 != 0) goto L_0x0214
        L_0x0213:
            r2 = 1
        L_0x0214:
            if (r3 != 0) goto L_0x023f
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mTop
            if (r4 == 0) goto L_0x023f
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mBottom
            if (r4 == 0) goto L_0x023f
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x022a
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 != 0) goto L_0x023f
        L_0x022a:
            int r4 = r15.mBaselineDistance
            if (r4 == 0) goto L_0x023e
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mBaseline
            if (r4 == 0) goto L_0x023f
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 == 0) goto L_0x023e
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mBaseline
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r4.mTarget
            if (r4 != 0) goto L_0x023f
        L_0x023e:
            r3 = 1
        L_0x023f:
            int r4 = r15.mDimensionRatioSide
            float r5 = r15.mDimensionRatio
            float r6 = r15.mDimensionRatio
            r20 = 0
            r21 = r8
            r8 = -1
            int r6 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1))
            if (r6 <= 0) goto L_0x02cb
            int r6 = r15.mVisibility
            r12 = 8
            if (r6 == r12) goto L_0x02cb
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r6 = r15.mHorizontalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r12 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            r20 = 1065353216(0x3f800000, float:1.0)
            if (r6 != r12) goto L_0x0295
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r6 = r15.mVerticalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r12 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r6 != r12) goto L_0x0295
            if (r2 == 0) goto L_0x026f
            if (r3 != 0) goto L_0x026f
            r23 = r0
            r6 = r1
            r24 = r3
            r25 = r5
            r12 = 0
            goto L_0x0292
        L_0x026f:
            if (r2 != 0) goto L_0x028a
            if (r3 == 0) goto L_0x028a
            int r4 = r15.mDimensionRatioSide
            if (r4 != r8) goto L_0x0281
            float r20 = r20 / r5
            r23 = r0
            r6 = r1
            r24 = r3
            r25 = r20
            goto L_0x0288
        L_0x0281:
            r23 = r0
            r6 = r1
            r24 = r3
            r25 = r5
        L_0x0288:
            r12 = 1
            goto L_0x0292
        L_0x028a:
            r23 = r0
            r6 = r1
            r24 = r3
            r12 = r4
            r25 = r5
        L_0x0292:
            r20 = 1
            goto L_0x02d5
        L_0x0295:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r6 = r15.mHorizontalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r12 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r6 != r12) goto L_0x02ad
            int r0 = r15.mHeight
            float r0 = (float) r0
            float r0 = r0 * r5
            int r0 = (int) r0
            r23 = r0
            r6 = r1
            r24 = r3
            r25 = r5
            r3 = 1
            r12 = 0
            r20 = 0
            goto L_0x02d6
        L_0x02ad:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r6 = r15.mVerticalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r12 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            if (r6 != r12) goto L_0x02cb
            int r1 = r15.mDimensionRatioSide
            if (r1 != r8) goto L_0x02b9
            float r5 = r20 / r5
        L_0x02b9:
            int r1 = r15.mWidth
            float r1 = (float) r1
            float r1 = r1 * r5
            int r1 = (int) r1
            r23 = r0
            r6 = r1
            r3 = r2
            r25 = r5
            r12 = 1
            r20 = 0
            r24 = 1
            goto L_0x02d6
        L_0x02cb:
            r23 = r0
            r6 = r1
            r24 = r3
            r12 = r4
            r25 = r5
            r20 = 0
        L_0x02d5:
            r3 = r2
        L_0x02d6:
            if (r20 == 0) goto L_0x02df
            if (r12 == 0) goto L_0x02dc
            if (r12 != r8) goto L_0x02df
        L_0x02dc:
            r26 = 1
            goto L_0x02e1
        L_0x02df:
            r26 = 0
        L_0x02e1:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r0 = r15.mHorizontalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r1 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r0 != r1) goto L_0x02ed
            boolean r0 = r15 instanceof android.support.constraint.solver.widgets.ConstraintWidgetContainer
            if (r0 == 0) goto L_0x02ed
            r2 = 1
            goto L_0x02ee
        L_0x02ed:
            r2 = 0
        L_0x02ee:
            int r0 = r15.mHorizontalResolution
            r5 = 2
            r4 = 3
            if (r0 == r5) goto L_0x03ec
            r1 = 2147483647(0x7fffffff, float:NaN)
            if (r13 == r1) goto L_0x0318
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mLeft
            int r0 = r0.mGroup
            if (r0 != r13) goto L_0x0306
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mRight
            int r0 = r0.mGroup
            if (r0 != r13) goto L_0x0306
            goto L_0x0318
        L_0x0306:
            r28 = r6
            r29 = r7
            r32 = r9
            r33 = r10
            r34 = r11
            r35 = r12
            r30 = r21
            r18 = 0
            goto L_0x03fe
        L_0x0318:
            if (r26 == 0) goto L_0x03a3
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            if (r0 == 0) goto L_0x03a3
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            if (r0 == 0) goto L_0x03a3
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mLeft
            android.support.constraint.solver.SolverVariable r2 = r14.createObjectVariable(r0)
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mRight
            android.support.constraint.solver.SolverVariable r3 = r14.createObjectVariable(r0)
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.getTarget()
            android.support.constraint.solver.SolverVariable r0 = r14.createObjectVariable(r0)
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mRight
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r1.getTarget()
            android.support.constraint.solver.SolverVariable r1 = r14.createObjectVariable(r1)
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mLeft
            int r5 = r5.getMargin()
            r14.addGreaterThan(r2, r0, r5, r4)
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mRight
            int r5 = r5.getMargin()
            int r5 = r5 * -1
            r14.addLowerThan(r3, r1, r5, r4)
            if (r16 != 0) goto L_0x038f
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mLeft
            int r5 = r5.getMargin()
            float r4 = r15.mHorizontalBiasPercent
            android.support.constraint.solver.widgets.ConstraintAnchor r8 = r15.mRight
            int r8 = r8.getMargin()
            r16 = 4
            r22 = r0
            r0 = r44
            r23 = r1
            r27 = 2147483647(0x7fffffff, float:NaN)
            r1 = r2
            r2 = r22
            r22 = r3
            r3 = r5
            r5 = 3
            r5 = r23
            r28 = r6
            r18 = 0
            r6 = r22
            r29 = r7
            r7 = r8
            r30 = r21
            r8 = r16
            r0.addCentering(r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x039a
        L_0x038f:
            r28 = r6
            r29 = r7
            r30 = r21
            r18 = 0
            r27 = 2147483647(0x7fffffff, float:NaN)
        L_0x039a:
            r32 = r9
            r33 = r10
            r34 = r11
            r35 = r12
            goto L_0x03fe
        L_0x03a3:
            r28 = r6
            r29 = r7
            r30 = r21
            r18 = 0
            r27 = 2147483647(0x7fffffff, float:NaN)
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mLeft
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r15.mRight
            int r6 = r15.f10mX
            int r0 = r15.f10mX
            int r7 = r0 + r23
            int r8 = r15.mMinWidth
            float r1 = r15.mHorizontalBiasPercent
            int r0 = r15.mMatchConstraintDefaultWidth
            int r14 = r15.mMatchConstraintMinWidth
            r31 = r14
            int r14 = r15.mMatchConstraintMaxWidth
            r19 = r0
            r0 = r43
            r21 = r1
            r1 = r44
            r22 = r8
            r8 = r23
            r32 = r9
            r9 = r22
            r33 = r10
            r10 = r21
            r34 = r11
            r11 = r26
            r35 = r12
            r12 = r16
            r13 = r19
            r16 = r14
            r14 = r31
            r15 = r16
            r0.applyConstraints(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x03fc
        L_0x03ec:
            r28 = r6
            r29 = r7
            r32 = r9
            r33 = r10
            r34 = r11
            r35 = r12
            r30 = r21
            r18 = 0
        L_0x03fc:
            r15 = r43
        L_0x03fe:
            int r0 = r15.mVerticalResolution
            r1 = 2
            if (r0 != r1) goto L_0x0404
            return
        L_0x0404:
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r0 = r15.mVerticalDimensionBehaviour
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r1 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT
            if (r0 != r1) goto L_0x0410
            boolean r0 = r15 instanceof android.support.constraint.solver.widgets.ConstraintWidgetContainer
            if (r0 == 0) goto L_0x0410
            r2 = 1
            goto L_0x0411
        L_0x0410:
            r2 = 0
        L_0x0411:
            if (r20 == 0) goto L_0x041f
            r14 = r35
            r13 = 1
            if (r14 == r13) goto L_0x041c
            r0 = -1
            if (r14 != r0) goto L_0x0423
            goto L_0x041d
        L_0x041c:
            r0 = -1
        L_0x041d:
            r11 = 1
            goto L_0x0424
        L_0x041f:
            r14 = r35
            r0 = -1
            r13 = 1
        L_0x0423:
            r11 = 0
        L_0x0424:
            int r1 = r15.mBaselineDistance
            if (r1 <= 0) goto L_0x052d
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mBottom
            r12 = 5
            r9 = 2147483647(0x7fffffff, float:NaN)
            r10 = r45
            if (r10 == r9) goto L_0x0444
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mBottom
            int r3 = r3.mGroup
            if (r3 != r10) goto L_0x043f
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mBaseline
            int r3 = r3.mGroup
            if (r3 != r10) goto L_0x043f
            goto L_0x0444
        L_0x043f:
            r7 = r32
            r8 = r44
            goto L_0x0451
        L_0x0444:
            int r3 = r43.getBaselineDistance()
            r4 = r29
            r7 = r32
            r8 = r44
            r8.addEquality(r4, r7, r3, r12)
        L_0x0451:
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mBaseline
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.mTarget
            if (r3 == 0) goto L_0x045f
            int r1 = r15.mBaselineDistance
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mBaseline
            r16 = r1
            r5 = r3
            goto L_0x0462
        L_0x045f:
            r5 = r1
            r16 = r28
        L_0x0462:
            if (r10 == r9) goto L_0x0477
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mTop
            int r1 = r1.mGroup
            if (r1 != r10) goto L_0x046f
            int r1 = r5.mGroup
            if (r1 != r10) goto L_0x046f
            goto L_0x0477
        L_0x046f:
            r15 = r8
            r39 = r14
            r13 = r30
            r14 = r7
            goto L_0x054d
        L_0x0477:
            if (r11 == 0) goto L_0x04e4
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r1.mTarget
            if (r1 == 0) goto L_0x04e4
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r1.mTarget
            if (r1 == 0) goto L_0x04e4
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r15.mTop
            android.support.constraint.solver.SolverVariable r1 = r8.createObjectVariable(r1)
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mBottom
            android.support.constraint.solver.SolverVariable r6 = r8.createObjectVariable(r2)
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r15.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.getTarget()
            android.support.constraint.solver.SolverVariable r2 = r8.createObjectVariable(r2)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.getTarget()
            android.support.constraint.solver.SolverVariable r5 = r8.createObjectVariable(r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mTop
            int r3 = r3.getMargin()
            r11 = 3
            r8.addGreaterThan(r1, r2, r3, r11)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r15.mBottom
            int r3 = r3.getMargin()
            int r3 = r3 * -1
            r8.addLowerThan(r6, r5, r3, r11)
            if (r17 != 0) goto L_0x04d8
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mTop
            int r3 = r0.getMargin()
            float r4 = r15.mVerticalBiasPercent
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r15.mBottom
            int r12 = r0.getMargin()
            r16 = 4
            r0 = r44
            r36 = r7
            r7 = r12
            r12 = r8
            r8 = r16
            r0.addCentering(r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x04db
        L_0x04d8:
            r36 = r7
            r12 = r8
        L_0x04db:
            r15 = r12
            r39 = r14
            r13 = r30
            r14 = r36
            goto L_0x054d
        L_0x04e4:
            r36 = r7
            r7 = 3
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r15.mTop
            int r6 = r15.f11mY
            int r0 = r15.f11mY
            int r18 = r0 + r16
            int r3 = r15.mMinHeight
            float r1 = r15.mVerticalBiasPercent
            int r0 = r15.mMatchConstraintDefaultHeight
            r37 = r14
            int r14 = r15.mMatchConstraintMinHeight
            r38 = r14
            int r14 = r15.mMatchConstraintMaxHeight
            r19 = r0
            r0 = r43
            r21 = r1
            r1 = r44
            r22 = r3
            r3 = r24
            r7 = r18
            r8 = r16
            r9 = r22
            r10 = r21
            r12 = r17
            r13 = r19
            r16 = r14
            r39 = r37
            r14 = r38
            r15 = r16
            r0.applyConstraints(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
            r8 = r28
            r13 = r30
            r14 = r36
            r0 = 5
            r15 = r44
            r15.addEquality(r13, r14, r8, r0)
            goto L_0x054d
        L_0x052d:
            r39 = r14
            r8 = r28
            r13 = r30
            r14 = r32
            r10 = 2147483647(0x7fffffff, float:NaN)
            r12 = r45
            r15 = r44
            if (r12 == r10) goto L_0x0553
            r9 = r43
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r9.mTop
            int r1 = r1.mGroup
            if (r1 != r12) goto L_0x054d
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r9.mBottom
            int r1 = r1.mGroup
            if (r1 != r12) goto L_0x054d
            goto L_0x0555
        L_0x054d:
            r41 = r13
            r42 = r14
            goto L_0x05e3
        L_0x0553:
            r9 = r43
        L_0x0555:
            if (r11 == 0) goto L_0x05af
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r9.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r1.mTarget
            if (r1 == 0) goto L_0x05af
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r9.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r1.mTarget
            if (r1 == 0) goto L_0x05af
            android.support.constraint.solver.widgets.ConstraintAnchor r1 = r9.mTop
            android.support.constraint.solver.SolverVariable r1 = r15.createObjectVariable(r1)
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r9.mBottom
            android.support.constraint.solver.SolverVariable r6 = r15.createObjectVariable(r2)
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r9.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r2.getTarget()
            android.support.constraint.solver.SolverVariable r2 = r15.createObjectVariable(r2)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r9.mBottom
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r3.getTarget()
            android.support.constraint.solver.SolverVariable r5 = r15.createObjectVariable(r3)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r9.mTop
            int r3 = r3.getMargin()
            r11 = 3
            r15.addGreaterThan(r1, r2, r3, r11)
            android.support.constraint.solver.widgets.ConstraintAnchor r3 = r9.mBottom
            int r3 = r3.getMargin()
            int r3 = r3 * -1
            r15.addLowerThan(r6, r5, r3, r11)
            if (r17 != 0) goto L_0x054d
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r9.mTop
            int r3 = r0.getMargin()
            float r4 = r9.mVerticalBiasPercent
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r9.mBottom
            int r7 = r0.getMargin()
            r8 = 4
            r0 = r44
            r0.addCentering(r1, r2, r3, r4, r5, r6, r7, r8)
            goto L_0x054d
        L_0x05af:
            r7 = 3
            android.support.constraint.solver.widgets.ConstraintAnchor r4 = r9.mTop
            android.support.constraint.solver.widgets.ConstraintAnchor r5 = r9.mBottom
            int r6 = r9.f11mY
            int r0 = r9.f11mY
            int r16 = r0 + r8
            int r3 = r9.mMinHeight
            float r1 = r9.mVerticalBiasPercent
            int r0 = r9.mMatchConstraintDefaultHeight
            r40 = r14
            int r14 = r9.mMatchConstraintMinHeight
            int r15 = r9.mMatchConstraintMaxHeight
            r18 = r0
            r0 = r43
            r19 = r1
            r1 = r44
            r21 = r3
            r3 = r24
            r7 = r16
            r9 = r21
            r10 = r19
            r12 = r17
            r41 = r13
            r13 = r18
            r42 = r40
            r0.applyConstraints(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
        L_0x05e3:
            if (r20 == 0) goto L_0x0673
            android.support.constraint.solver.ArrayRow r0 = r44.createRow()
            r1 = r45
            r2 = 2147483647(0x7fffffff, float:NaN)
            if (r1 == r2) goto L_0x05ff
            r8 = r43
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r8.mLeft
            int r2 = r2.mGroup
            if (r2 != r1) goto L_0x0675
            android.support.constraint.solver.widgets.ConstraintAnchor r2 = r8.mRight
            int r2 = r2.mGroup
            if (r2 != r1) goto L_0x0675
            goto L_0x0601
        L_0x05ff:
            r8 = r43
        L_0x0601:
            r4 = r39
            if (r4 != 0) goto L_0x061a
            r2 = r0
            r3 = r33
            r4 = r34
            r5 = r41
            r6 = r42
            r7 = r25
            android.support.constraint.solver.ArrayRow r0 = r2.createRowDimensionRatio(r3, r4, r5, r6, r7)
            r1 = r44
            r1.addConstraint(r0)
            goto L_0x0675
        L_0x061a:
            r1 = r44
            r2 = 1
            if (r4 != r2) goto L_0x0632
            r2 = r0
            r3 = r41
            r4 = r42
            r5 = r33
            r6 = r34
            r7 = r25
            android.support.constraint.solver.ArrayRow r0 = r2.createRowDimensionRatio(r3, r4, r5, r6, r7)
            r1.addConstraint(r0)
            goto L_0x0675
        L_0x0632:
            int r2 = r8.mMatchConstraintMinWidth
            if (r2 <= 0) goto L_0x0641
            int r2 = r8.mMatchConstraintMinWidth
            r3 = r33
            r4 = r34
            r5 = 3
            r1.addGreaterThan(r3, r4, r2, r5)
            goto L_0x0646
        L_0x0641:
            r3 = r33
            r4 = r34
            r5 = 3
        L_0x0646:
            int r2 = r8.mMatchConstraintMinHeight
            if (r2 <= 0) goto L_0x0654
            int r2 = r8.mMatchConstraintMinHeight
            r7 = r41
            r6 = r42
            r1.addGreaterThan(r7, r6, r2, r5)
            goto L_0x0658
        L_0x0654:
            r7 = r41
            r6 = r42
        L_0x0658:
            r9 = 4
            r2 = r0
            r5 = r7
            r7 = r25
            r2.createRowDimensionRatio(r3, r4, r5, r6, r7)
            android.support.constraint.solver.SolverVariable r2 = r44.createErrorVariable()
            android.support.constraint.solver.SolverVariable r3 = r44.createErrorVariable()
            r2.strength = r9
            r3.strength = r9
            r0.addError(r2, r3)
            r1.addConstraint(r0)
            goto L_0x0675
        L_0x0673:
            r8 = r43
        L_0x0675:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.ConstraintWidget.addToSolver(android.support.constraint.solver.LinearSystem, int):void");
    }

    private void applyConstraints(LinearSystem linearSystem, boolean z, boolean z2, ConstraintAnchor constraintAnchor, ConstraintAnchor constraintAnchor2, int i, int i2, int i3, int i4, float f, boolean z3, boolean z4, int i5, int i6, int i7) {
        int i8;
        boolean z5;
        LinearSystem linearSystem2 = linearSystem;
        int i9 = i;
        int i10 = i2;
        int i11 = i4;
        int i12 = i6;
        int i13 = i7;
        SolverVariable createObjectVariable = linearSystem2.createObjectVariable(constraintAnchor);
        SolverVariable createObjectVariable2 = linearSystem2.createObjectVariable(constraintAnchor2);
        SolverVariable createObjectVariable3 = linearSystem2.createObjectVariable(constraintAnchor.getTarget());
        SolverVariable createObjectVariable4 = linearSystem2.createObjectVariable(constraintAnchor2.getTarget());
        int margin = constraintAnchor.getMargin();
        int margin2 = constraintAnchor2.getMargin();
        if (this.mVisibility == 8) {
            z5 = true;
            i8 = 0;
        } else {
            z5 = z2;
            i8 = i3;
        }
        if (createObjectVariable3 == null && createObjectVariable4 == null) {
            linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable, i9));
            if (z3) {
                return;
            }
            if (z) {
                linearSystem2.addConstraint(LinearSystem.createRowEquals(linearSystem2, createObjectVariable2, createObjectVariable, i11, true));
            } else if (z5) {
                linearSystem2.addConstraint(LinearSystem.createRowEquals(linearSystem2, createObjectVariable2, createObjectVariable, i8, false));
            } else {
                linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, i10));
            }
        } else if (createObjectVariable3 != null && createObjectVariable4 == null) {
            linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable, createObjectVariable3, margin));
            if (z) {
                linearSystem2.addConstraint(LinearSystem.createRowEquals(linearSystem2, createObjectVariable2, createObjectVariable, i11, true));
            } else if (z3) {
            } else {
                if (z5) {
                    linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, createObjectVariable, i8));
                } else {
                    linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, i10));
                }
            }
        } else if (createObjectVariable3 == null && createObjectVariable4 != null) {
            linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, createObjectVariable4, margin2 * -1));
            if (z) {
                linearSystem2.addConstraint(LinearSystem.createRowEquals(linearSystem2, createObjectVariable2, createObjectVariable, i11, true));
            } else if (z3) {
            } else {
                if (z5) {
                    linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, createObjectVariable, i8));
                } else {
                    linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable, i9));
                }
            }
        } else if (z5) {
            if (z) {
                linearSystem2.addConstraint(LinearSystem.createRowEquals(linearSystem2, createObjectVariable2, createObjectVariable, i11, true));
            } else {
                linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, createObjectVariable, i8));
            }
            if (constraintAnchor.getStrength() != constraintAnchor2.getStrength()) {
                if (constraintAnchor.getStrength() == Strength.STRONG) {
                    linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable, createObjectVariable3, margin));
                    SolverVariable createSlackVariable = linearSystem.createSlackVariable();
                    ArrayRow createRow = linearSystem.createRow();
                    createRow.createRowLowerThan(createObjectVariable2, createObjectVariable4, createSlackVariable, margin2 * -1);
                    linearSystem2.addConstraint(createRow);
                    return;
                }
                SolverVariable createSlackVariable2 = linearSystem.createSlackVariable();
                ArrayRow createRow2 = linearSystem.createRow();
                createRow2.createRowGreaterThan(createObjectVariable, createObjectVariable3, createSlackVariable2, margin);
                linearSystem2.addConstraint(createRow2);
                linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, createObjectVariable4, margin2 * -1));
            } else if (createObjectVariable3 == createObjectVariable4) {
                linearSystem2.addConstraint(LinearSystem.createRowCentering(linearSystem, createObjectVariable, createObjectVariable3, 0, 0.5f, createObjectVariable4, createObjectVariable2, 0, true));
            } else if (!z4) {
                linearSystem2.addConstraint(LinearSystem.createRowGreaterThan(linearSystem2, createObjectVariable, createObjectVariable3, margin, constraintAnchor.getConnectionType() != ConnectionType.STRICT));
                linearSystem2.addConstraint(LinearSystem.createRowLowerThan(linearSystem2, createObjectVariable2, createObjectVariable4, margin2 * -1, constraintAnchor2.getConnectionType() != ConnectionType.STRICT));
                linearSystem2.addConstraint(LinearSystem.createRowCentering(linearSystem, createObjectVariable, createObjectVariable3, margin, f, createObjectVariable4, createObjectVariable2, margin2, false));
            }
        } else if (z3) {
            linearSystem2.addGreaterThan(createObjectVariable, createObjectVariable3, margin, 3);
            linearSystem2.addLowerThan(createObjectVariable2, createObjectVariable4, margin2 * -1, 3);
            linearSystem2.addConstraint(LinearSystem.createRowCentering(linearSystem, createObjectVariable, createObjectVariable3, margin, f, createObjectVariable4, createObjectVariable2, margin2, true));
        } else if (z4) {
        } else {
            if (i5 == 1) {
                if (i12 <= i8) {
                    i12 = i8;
                }
                int i14 = i7;
                if (i14 > 0) {
                    if (i14 >= i12) {
                        linearSystem2.addLowerThan(createObjectVariable2, createObjectVariable, i14, 3);
                    }
                    linearSystem2.addEquality(createObjectVariable2, createObjectVariable, i14, 3);
                    linearSystem2.addGreaterThan(createObjectVariable, createObjectVariable3, margin, 2);
                    linearSystem2.addLowerThan(createObjectVariable2, createObjectVariable4, -margin2, 2);
                    linearSystem.addCentering(createObjectVariable, createObjectVariable3, margin, f, createObjectVariable4, createObjectVariable2, margin2, 4);
                    return;
                }
                i14 = i12;
                linearSystem2.addEquality(createObjectVariable2, createObjectVariable, i14, 3);
                linearSystem2.addGreaterThan(createObjectVariable, createObjectVariable3, margin, 2);
                linearSystem2.addLowerThan(createObjectVariable2, createObjectVariable4, -margin2, 2);
                linearSystem.addCentering(createObjectVariable, createObjectVariable3, margin, f, createObjectVariable4, createObjectVariable2, margin2, 4);
                return;
            }
            int i15 = i7;
            if (i12 == 0 && i15 == 0) {
                linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable, createObjectVariable3, margin));
                linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, createObjectVariable4, margin2 * -1));
                return;
            }
            if (i15 > 0) {
                linearSystem2.addLowerThan(createObjectVariable2, createObjectVariable, i15, 3);
            }
            linearSystem2.addGreaterThan(createObjectVariable, createObjectVariable3, margin, 2);
            linearSystem2.addLowerThan(createObjectVariable2, createObjectVariable4, -margin2, 2);
            linearSystem.addCentering(createObjectVariable, createObjectVariable3, margin, f, createObjectVariable4, createObjectVariable2, margin2, 4);
        }
    }

    public void updateFromSolver(LinearSystem linearSystem, int i) {
        if (i == Integer.MAX_VALUE) {
            setFrame(linearSystem.getObjectVariableValue(this.mLeft), linearSystem.getObjectVariableValue(this.mTop), linearSystem.getObjectVariableValue(this.mRight), linearSystem.getObjectVariableValue(this.mBottom));
        } else if (i == -2) {
            setFrame(this.mSolverLeft, this.mSolverTop, this.mSolverRight, this.mSolverBottom);
        } else {
            if (this.mLeft.mGroup == i) {
                this.mSolverLeft = linearSystem.getObjectVariableValue(this.mLeft);
            }
            if (this.mTop.mGroup == i) {
                this.mSolverTop = linearSystem.getObjectVariableValue(this.mTop);
            }
            if (this.mRight.mGroup == i) {
                this.mSolverRight = linearSystem.getObjectVariableValue(this.mRight);
            }
            if (this.mBottom.mGroup == i) {
                this.mSolverBottom = linearSystem.getObjectVariableValue(this.mBottom);
            }
        }
    }

    public void updateFromSolver(LinearSystem linearSystem) {
        updateFromSolver(linearSystem, Integer.MAX_VALUE);
    }
}
