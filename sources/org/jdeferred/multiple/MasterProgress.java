package org.jdeferred.multiple;

public class MasterProgress {
    private final int done;
    private final int fail;
    private final int total;

    public MasterProgress(int i, int i2, int i3) {
        this.done = i;
        this.fail = i2;
        this.total = i3;
    }

    public int getDone() {
        return this.done;
    }

    public int getFail() {
        return this.fail;
    }

    public int getTotal() {
        return this.total;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MasterProgress [done=");
        sb.append(this.done);
        sb.append(", fail=");
        sb.append(this.fail);
        sb.append(", total=");
        sb.append(this.total);
        sb.append("]");
        return sb.toString();
    }
}
