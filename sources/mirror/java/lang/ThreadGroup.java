package mirror.java.lang;

import java.util.List;
import mirror.RefClass;
import mirror.RefObject;

public class ThreadGroup {
    public static Class<?> TYPE = RefClass.load(ThreadGroup.class, ThreadGroup.class);
    public static RefObject<List<ThreadGroup>> groups;
    public static RefObject<ThreadGroup> parent;
}
