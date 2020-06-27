package com.android.launcher3;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;

public class LauncherSettings {

    public interface BaseLauncherColumns extends ChangeLogColumns {
        public static final String ICON = "icon";
        public static final String ICON_PACKAGE = "iconPackage";
        public static final String ICON_RESOURCE = "iconResource";
        public static final String INTENT = "intent";
        public static final String ITEM_TYPE = "itemType";
        public static final int ITEM_TYPE_APPLICATION = 0;
        public static final int ITEM_TYPE_SHORTCUT = 1;
        public static final String TITLE = "title";
    }

    interface ChangeLogColumns extends BaseColumns {
        public static final String MODIFIED = "modified";
    }

    public static final class Favorites implements BaseLauncherColumns {
        public static final String APPWIDGET_ID = "appWidgetId";
        public static final String APPWIDGET_PROVIDER = "appWidgetProvider";
        public static final String CELLX = "cellX";
        public static final String CELLY = "cellY";
        public static final String CONTAINER = "container";
        public static final int CONTAINER_DESKTOP = -100;
        public static final int CONTAINER_HOTSEAT = -101;
        public static final Uri CONTENT_URI;
        public static final int ITEM_TYPE_APPWIDGET = 4;
        public static final int ITEM_TYPE_CUSTOM_APPWIDGET = 5;
        public static final int ITEM_TYPE_DEEP_SHORTCUT = 6;
        public static final int ITEM_TYPE_FOLDER = 2;
        public static final String OPTIONS = "options";
        public static final String PROFILE_ID = "profileId";
        public static final String RANK = "rank";
        public static final String RESTORED = "restored";
        public static final String SCREEN = "screen";
        public static final String SPANX = "spanX";
        public static final String SPANY = "spanY";
        public static final String TABLE_NAME = "favorites";

        static {
            StringBuilder sb = new StringBuilder();
            sb.append("content://");
            sb.append(LauncherProvider.AUTHORITY);
            sb.append("/");
            sb.append(TABLE_NAME);
            CONTENT_URI = Uri.parse(sb.toString());
        }

        public static Uri getContentUri(long j) {
            StringBuilder sb = new StringBuilder();
            sb.append("content://");
            sb.append(LauncherProvider.AUTHORITY);
            sb.append("/");
            sb.append(TABLE_NAME);
            sb.append("/");
            sb.append(j);
            return Uri.parse(sb.toString());
        }

        static final String containerToString(int i) {
            switch (i) {
                case -101:
                    return "hotseat";
                case -100:
                    return "desktop";
                default:
                    return String.valueOf(i);
            }
        }

        static final String itemTypeToString(int i) {
            switch (i) {
                case 0:
                    return "APP";
                case 1:
                    return "SHORTCUT";
                case 2:
                    return "FOLDER";
                case 4:
                    return "WIDGET";
                case 5:
                    return "CUSTOMWIDGET";
                case 6:
                    return "DEEPSHORTCUT";
                default:
                    return String.valueOf(i);
            }
        }

        public static void addTableToDb(SQLiteDatabase sQLiteDatabase, long j, boolean z) {
            String str = z ? " IF NOT EXISTS " : "";
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE ");
            sb.append(str);
            sb.append(TABLE_NAME);
            sb.append(" (");
            sb.append("_id INTEGER PRIMARY KEY,");
            sb.append("title TEXT,");
            sb.append("intent TEXT,");
            sb.append("container INTEGER,");
            sb.append("screen INTEGER,");
            sb.append("cellX INTEGER,");
            sb.append("cellY INTEGER,");
            sb.append("spanX INTEGER,");
            sb.append("spanY INTEGER,");
            sb.append("itemType INTEGER,");
            sb.append("appWidgetId INTEGER NOT NULL DEFAULT -1,");
            sb.append("iconPackage TEXT,");
            sb.append("iconResource TEXT,");
            sb.append("icon BLOB,");
            sb.append("appWidgetProvider TEXT,");
            sb.append("modified INTEGER NOT NULL DEFAULT 0,");
            sb.append("restored INTEGER NOT NULL DEFAULT 0,");
            sb.append("profileId INTEGER DEFAULT ");
            sb.append(j);
            sb.append(",");
            sb.append("rank INTEGER NOT NULL DEFAULT 0,");
            sb.append("options INTEGER NOT NULL DEFAULT 0");
            sb.append(");");
            sQLiteDatabase.execSQL(sb.toString());
        }
    }

    public static final class Settings {
        public static final Uri CONTENT_URI;
        public static final String EXTRA_EXTRACTED_COLORS = "extra_extractedColors";
        public static final String EXTRA_VALUE = "value";
        public static final String EXTRA_WALLPAPER_ID = "extra_wallpaperId";
        public static final String METHOD_CLEAR_EMPTY_DB_FLAG = "clear_empty_db_flag";
        public static final String METHOD_CREATE_EMPTY_DB = "create_empty_db";
        public static final String METHOD_DELETE_EMPTY_FOLDERS = "delete_empty_folders";
        public static final String METHOD_LOAD_DEFAULT_FAVORITES = "load_default_favorites";
        public static final String METHOD_NEW_ITEM_ID = "generate_new_item_id";
        public static final String METHOD_NEW_SCREEN_ID = "generate_new_screen_id";
        public static final String METHOD_REMOVE_GHOST_WIDGETS = "remove_ghost_widgets";
        public static final String METHOD_SET_EXTRACTED_COLORS_AND_WALLPAPER_ID = "set_extracted_colors_and_wallpaper_id_setting";
        public static final String METHOD_WAS_EMPTY_DB_CREATED = "get_empty_db_flag";

        static {
            StringBuilder sb = new StringBuilder();
            sb.append("content://");
            sb.append(LauncherProvider.AUTHORITY);
            sb.append("/settings");
            CONTENT_URI = Uri.parse(sb.toString());
        }

        public static Bundle call(ContentResolver contentResolver, String str) {
            return contentResolver.call(CONTENT_URI, str, null, null);
        }
    }

    public static final class WorkspaceScreens implements ChangeLogColumns {
        public static final Uri CONTENT_URI;
        public static final String SCREEN_RANK = "screenRank";
        public static final String TABLE_NAME = "workspaceScreens";

        static {
            StringBuilder sb = new StringBuilder();
            sb.append("content://");
            sb.append(LauncherProvider.AUTHORITY);
            sb.append("/");
            sb.append(TABLE_NAME);
            CONTENT_URI = Uri.parse(sb.toString());
        }
    }
}
