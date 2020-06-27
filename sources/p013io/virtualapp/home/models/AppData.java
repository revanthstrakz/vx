package p013io.virtualapp.home.models;

import android.graphics.drawable.Drawable;

/* renamed from: io.virtualapp.home.models.AppData */
public interface AppData {
    boolean canCreateShortcut();

    boolean canDelete();

    boolean canLaunch();

    boolean canReorder();

    Drawable getIcon();

    String getName();

    boolean isFirstOpen();

    boolean isInstalling();

    boolean isLoading();
}
