package com.android.launcher3.util;

public abstract class FlagOp {
    public static FlagOp NO_OP = new FlagOp() {
    };

    public int apply(int i) {
        return i;
    }

    private FlagOp() {
    }

    public static FlagOp addFlag(final int i) {
        return new FlagOp() {
            public int apply(int i) {
                return i | i;
            }
        };
    }

    public static FlagOp removeFlag(final int i) {
        return new FlagOp() {
            public int apply(int i) {
                return i & (~i);
            }
        };
    }
}
