package jonathanfinerty.once;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Once {
    public static final int THIS_APP_INSTALL = 0;
    public static final int THIS_APP_VERSION = 1;
    private static long lastAppUpdatedTime = -1;
    private static PersistedMap tagLastSeenMap;
    private static PersistedSet toDoSet;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Scope {
    }

    private Once() {
    }

    public static void initialise(Context context) {
        if (tagLastSeenMap == null) {
            tagLastSeenMap = new PersistedMap(context, "TagLastSeenMap");
        }
        if (toDoSet == null) {
            toDoSet = new PersistedSet(context, "ToDoSet");
        }
        try {
            lastAppUpdatedTime = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).lastUpdateTime;
        } catch (NameNotFoundException unused) {
        }
    }

    public static void toDo(int i, String str) {
        List list = tagLastSeenMap.get(str);
        if (list.isEmpty()) {
            toDoSet.put(str);
            return;
        }
        Long l = (Long) list.get(list.size() - 1);
        if (i == 1 && l.longValue() <= lastAppUpdatedTime) {
            toDoSet.put(str);
        }
    }

    public static void toDo(String str) {
        toDoSet.put(str);
    }

    public static boolean needToDo(String str) {
        return toDoSet.contains(str);
    }

    public static boolean beenDone(String str) {
        return beenDone(0, str, Amount.moreThan(0));
    }

    public static boolean beenDone(String str, CountChecker countChecker) {
        return beenDone(0, str, countChecker);
    }

    public static boolean beenDone(int i, String str) {
        return beenDone(i, str, Amount.moreThan(0));
    }

    public static boolean beenDone(int i, String str, CountChecker countChecker) {
        List<Long> list = tagLastSeenMap.get(str);
        int i2 = 0;
        if (list.isEmpty()) {
            return false;
        }
        if (i == 0) {
            return countChecker.check(list.size());
        }
        for (Long longValue : list) {
            if (longValue.longValue() > lastAppUpdatedTime) {
                i2++;
            }
        }
        return countChecker.check(i2);
    }

    public static boolean beenDone(TimeUnit timeUnit, long j, String str) {
        return beenDone(timeUnit, j, str, Amount.moreThan(0));
    }

    public static boolean beenDone(TimeUnit timeUnit, long j, String str, CountChecker countChecker) {
        return beenDone(timeUnit.toMillis(j), str, countChecker);
    }

    public static boolean beenDone(long j, String str) {
        return beenDone(j, str, Amount.moreThan(0));
    }

    public static boolean beenDone(long j, String str, CountChecker countChecker) {
        List<Long> list = tagLastSeenMap.get(str);
        int i = 0;
        if (list.isEmpty()) {
            return false;
        }
        for (Long longValue : list) {
            if (longValue.longValue() > new Date().getTime() - j) {
                i++;
            }
        }
        return countChecker.check(i);
    }

    public static void markDone(String str) {
        tagLastSeenMap.put(str, new Date().getTime());
        toDoSet.remove(str);
    }

    public static void clearDone(String str) {
        tagLastSeenMap.remove(str);
    }

    public static void clearToDo(String str) {
        toDoSet.remove(str);
    }

    public static void clearAll() {
        tagLastSeenMap.clear();
    }

    public static void clearAllToDos() {
        toDoSet.clear();
    }
}
