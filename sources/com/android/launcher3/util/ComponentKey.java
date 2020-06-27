package com.android.launcher3.util;

import android.content.ComponentName;
import android.content.Context;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.compat.UserManagerCompat;
import java.util.Arrays;

public class ComponentKey {
    public final ComponentName componentName;
    private final int mHashCode;
    public final UserHandle user;

    public ComponentKey(ComponentName componentName2, UserHandle userHandle) {
        Preconditions.assertNotNull(componentName2);
        Preconditions.assertNotNull(userHandle);
        this.componentName = componentName2;
        this.user = userHandle;
        this.mHashCode = Arrays.hashCode(new Object[]{componentName2, userHandle});
    }

    public ComponentKey(Context context, String str) {
        int indexOf = str.indexOf("#");
        if (indexOf != -1) {
            String substring = str.substring(0, indexOf);
            Long valueOf = Long.valueOf(str.substring(indexOf + 1));
            this.componentName = ComponentName.unflattenFromString(substring);
            this.user = UserManagerCompat.getInstance(context).getUserForSerialNumber(valueOf.longValue());
        } else {
            this.componentName = ComponentName.unflattenFromString(str);
            this.user = Process.myUserHandle();
        }
        Preconditions.assertNotNull(this.componentName);
        Preconditions.assertNotNull(this.user);
        this.mHashCode = Arrays.hashCode(new Object[]{this.componentName, this.user});
    }

    public int hashCode() {
        return this.mHashCode;
    }

    public boolean equals(Object obj) {
        ComponentKey componentKey = (ComponentKey) obj;
        return componentKey.componentName.equals(this.componentName) && componentKey.user.equals(this.user);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.componentName.flattenToString());
        sb.append("#");
        sb.append(this.user.toString().replaceAll("\\D+", ""));
        return sb.toString();
    }
}
