package mirror.java.lang;

import mirror.RefClass;
import mirror.RefObject;

public class ThreadGroupN {
    public static Class<?> Class = RefClass.load(ThreadGroupN.class, ThreadGroup.class);
    public static RefObject<ThreadGroup[]> groups;
    public static RefObject<Integer> ngroups;
    public static RefObject<ThreadGroup> parent;
}
