package org.slf4j.helpers;

import java.io.PrintStream;

public class Util {
    public static final void report(String str, Throwable th) {
        System.err.println(str);
        System.err.println("Reported exception:");
        th.printStackTrace();
    }

    public static final void report(String str) {
        PrintStream printStream = System.err;
        StringBuilder sb = new StringBuilder();
        sb.append("SLF4J: ");
        sb.append(str);
        printStream.println(sb.toString());
    }
}
