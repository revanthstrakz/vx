package com.android.launcher3.shortcuts;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;

@TargetApi(24)
public class ShortcutInfoCompat {
    public static final String EXTRA_SHORTCUT_ID = "shortcut_id";
    private static final String INTENT_CATEGORY = "com.android.launcher3.DEEP_SHORTCUT";
    private ShortcutInfo mShortcutInfo;

    public ShortcutInfoCompat(ShortcutInfo shortcutInfo) {
        this.mShortcutInfo = shortcutInfo;
    }

    @TargetApi(24)
    public Intent makeIntent() {
        return new Intent("android.intent.action.MAIN").addCategory(INTENT_CATEGORY).setComponent(getActivity()).setPackage(getPackage()).setFlags(270532608).putExtra(EXTRA_SHORTCUT_ID, getId());
    }

    public ShortcutInfo getShortcutInfo() {
        return this.mShortcutInfo;
    }

    public String getPackage() {
        return this.mShortcutInfo.getPackage();
    }

    public String getId() {
        return this.mShortcutInfo.getId();
    }

    public CharSequence getShortLabel() {
        return this.mShortcutInfo.getShortLabel();
    }

    public CharSequence getLongLabel() {
        return this.mShortcutInfo.getLongLabel();
    }

    public long getLastChangedTimestamp() {
        return this.mShortcutInfo.getLastChangedTimestamp();
    }

    public ComponentName getActivity() {
        return this.mShortcutInfo.getActivity();
    }

    public UserHandle getUserHandle() {
        return this.mShortcutInfo.getUserHandle();
    }

    public boolean hasKeyFieldsOnly() {
        return this.mShortcutInfo.hasKeyFieldsOnly();
    }

    public boolean isPinned() {
        return this.mShortcutInfo.isPinned();
    }

    public boolean isDeclaredInManifest() {
        return this.mShortcutInfo.isDeclaredInManifest();
    }

    public boolean isEnabled() {
        return this.mShortcutInfo.isEnabled();
    }

    public boolean isDynamic() {
        return this.mShortcutInfo.isDynamic();
    }

    public int getRank() {
        return this.mShortcutInfo.getRank();
    }

    public CharSequence getDisabledMessage() {
        return this.mShortcutInfo.getDisabledMessage();
    }

    public String toString() {
        return this.mShortcutInfo.toString();
    }
}
