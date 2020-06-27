package com.android.launcher3;

import android.os.Process;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.util.ContentWriter;
import java.util.ArrayList;

public class FolderInfo extends ItemInfo {
    public static final int FLAG_ITEMS_SORTED = 1;
    public static final int FLAG_MULTI_PAGE_ANIMATION = 4;
    public static final int FLAG_WORK_FOLDER = 2;
    public static final int NO_FLAGS = 0;
    public ArrayList<ShortcutInfo> contents;
    ArrayList<FolderListener> listeners;
    public int options;

    public interface FolderListener {
        void onAdd(ShortcutInfo shortcutInfo, int i);

        void onItemsChanged(boolean z);

        void onRemove(ShortcutInfo shortcutInfo);

        void onTitleChanged(CharSequence charSequence);

        void prepareAutoUpdate();
    }

    public FolderInfo() {
        this.contents = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.itemType = 2;
        this.user = Process.myUserHandle();
    }

    public void add(ShortcutInfo shortcutInfo, boolean z) {
        add(shortcutInfo, this.contents.size(), z);
    }

    public void add(ShortcutInfo shortcutInfo, int i, boolean z) {
        int boundToRange = Utilities.boundToRange(i, 0, this.contents.size());
        this.contents.add(boundToRange, shortcutInfo);
        for (int i2 = 0; i2 < this.listeners.size(); i2++) {
            ((FolderListener) this.listeners.get(i2)).onAdd(shortcutInfo, boundToRange);
        }
        itemsChanged(z);
    }

    public void remove(ShortcutInfo shortcutInfo, boolean z) {
        this.contents.remove(shortcutInfo);
        for (int i = 0; i < this.listeners.size(); i++) {
            ((FolderListener) this.listeners.get(i)).onRemove(shortcutInfo);
        }
        itemsChanged(z);
    }

    public void setTitle(CharSequence charSequence) {
        this.title = charSequence;
        for (int i = 0; i < this.listeners.size(); i++) {
            ((FolderListener) this.listeners.get(i)).onTitleChanged(charSequence);
        }
    }

    public void onAddToDatabase(ContentWriter contentWriter) {
        super.onAddToDatabase(contentWriter);
        contentWriter.put(BaseLauncherColumns.TITLE, this.title).put(Favorites.OPTIONS, Integer.valueOf(this.options));
    }

    public void addListener(FolderListener folderListener) {
        this.listeners.add(folderListener);
    }

    public void removeListener(FolderListener folderListener) {
        this.listeners.remove(folderListener);
    }

    public void itemsChanged(boolean z) {
        for (int i = 0; i < this.listeners.size(); i++) {
            ((FolderListener) this.listeners.get(i)).onItemsChanged(z);
        }
    }

    public void prepareAutoUpdate() {
        for (int i = 0; i < this.listeners.size(); i++) {
            ((FolderListener) this.listeners.get(i)).prepareAutoUpdate();
        }
    }

    public boolean hasOption(int i) {
        return (i & this.options) != 0;
    }

    public void setOption(int i, boolean z, ModelWriter modelWriter) {
        int i2 = this.options;
        if (z) {
            this.options = i | this.options;
        } else {
            this.options = (~i) & this.options;
        }
        if (modelWriter != null && i2 != this.options) {
            modelWriter.updateItemInDatabase(this);
        }
    }
}
