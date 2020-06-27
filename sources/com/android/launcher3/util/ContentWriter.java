package com.android.launcher3.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings.BaseLauncherColumns;
import com.android.launcher3.LauncherSettings.Favorites;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.UserManagerCompat;

public class ContentWriter {
    private CommitParams mCommitParams;
    private final Context mContext;
    private Bitmap mIcon;
    private UserHandle mUser;
    private final ContentValues mValues;

    public static final class CommitParams {
        String[] mSelectionArgs;
        final Uri mUri = Favorites.CONTENT_URI;
        String mWhere;

        public CommitParams(String str, String[] strArr) {
            this.mWhere = str;
            this.mSelectionArgs = strArr;
        }
    }

    public ContentWriter(Context context, CommitParams commitParams) {
        this(context);
        this.mCommitParams = commitParams;
    }

    public ContentWriter(Context context) {
        this(new ContentValues(), context);
    }

    public ContentWriter(ContentValues contentValues, Context context) {
        this.mValues = contentValues;
        this.mContext = context;
    }

    public ContentWriter put(String str, Integer num) {
        this.mValues.put(str, num);
        return this;
    }

    public ContentWriter put(String str, Long l) {
        this.mValues.put(str, l);
        return this;
    }

    public ContentWriter put(String str, String str2) {
        this.mValues.put(str, str2);
        return this;
    }

    public ContentWriter put(String str, CharSequence charSequence) {
        this.mValues.put(str, charSequence == null ? null : charSequence.toString());
        return this;
    }

    public ContentWriter put(String str, Intent intent) {
        this.mValues.put(str, intent == null ? null : intent.toUri(0));
        return this;
    }

    public ContentWriter putIcon(Bitmap bitmap, UserHandle userHandle) {
        this.mIcon = bitmap;
        this.mUser = userHandle;
        return this;
    }

    public ContentWriter put(String str, UserHandle userHandle) {
        return put(str, Long.valueOf(UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(userHandle)));
    }

    public ContentValues getValues(Context context) {
        Preconditions.assertNonUiThread();
        if (this.mIcon != null && !LauncherAppState.getInstance(context).getIconCache().isDefaultIcon(this.mIcon, this.mUser)) {
            this.mValues.put(BaseLauncherColumns.ICON, Utilities.flattenBitmap(this.mIcon));
            this.mIcon = null;
        }
        return this.mValues;
    }

    public int commit() {
        if (this.mCommitParams != null) {
            return this.mContext.getContentResolver().update(this.mCommitParams.mUri, getValues(this.mContext), this.mCommitParams.mWhere, this.mCommitParams.mSelectionArgs);
        }
        return 0;
    }
}
