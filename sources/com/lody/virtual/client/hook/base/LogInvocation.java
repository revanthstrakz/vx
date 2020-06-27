package com.lody.virtual.client.hook.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LogInvocation {

    public enum Condition {
        NEVER {
            public int getLogLevel(boolean z, boolean z2) {
                return -1;
            }
        },
        ALWAYS {
            public int getLogLevel(boolean z, boolean z2) {
                return z2 ? 5 : 4;
            }
        },
        ON_ERROR {
            public int getLogLevel(boolean z, boolean z2) {
                return z2 ? 5 : -1;
            }
        },
        NOT_HOOKED {
            public int getLogLevel(boolean z, boolean z2) {
                if (z) {
                    return -1;
                }
                return z2 ? 5 : 4;
            }
        };

        public abstract int getLogLevel(boolean z, boolean z2);
    }

    Condition value() default Condition.ALWAYS;
}
