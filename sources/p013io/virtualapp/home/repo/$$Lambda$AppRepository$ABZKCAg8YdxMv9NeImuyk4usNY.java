package p013io.virtualapp.home.repo;

import java.util.Comparator;
import p013io.virtualapp.utils.HanziToPinyin;

/* renamed from: io.virtualapp.home.repo.-$$Lambda$AppRepository$ABZKCAg8YdxMv-9NeImuyk4usNY reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AppRepository$ABZKCAg8YdxMv9NeImuyk4usNY implements Comparator {
    public static final /* synthetic */ $$Lambda$AppRepository$ABZKCAg8YdxMv9NeImuyk4usNY INSTANCE = new $$Lambda$AppRepository$ABZKCAg8YdxMv9NeImuyk4usNY();

    private /* synthetic */ $$Lambda$AppRepository$ABZKCAg8YdxMv9NeImuyk4usNY() {
    }

    public final int compare(Object obj, Object obj2) {
        return HanziToPinyin.getInstance();
    }
}
