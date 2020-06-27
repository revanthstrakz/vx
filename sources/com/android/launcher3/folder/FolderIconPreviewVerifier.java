package com.android.launcher3.folder;

import com.android.launcher3.FolderInfo;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.config.FeatureFlags;

public class FolderIconPreviewVerifier {
    private boolean mDisplayingUpperLeftQuadrant = false;
    private int mGridCountX;
    private final int[] mGridSize = new int[2];
    private final int mMaxGridCountX;
    private final int mMaxGridCountY;
    private final int mMaxItemsPerPage;

    public FolderIconPreviewVerifier(InvariantDeviceProfile invariantDeviceProfile) {
        this.mMaxGridCountX = invariantDeviceProfile.numFolderColumns;
        this.mMaxGridCountY = invariantDeviceProfile.numFolderRows;
        this.mMaxItemsPerPage = this.mMaxGridCountX * this.mMaxGridCountY;
    }

    public void setFolderInfo(FolderInfo folderInfo) {
        int size = folderInfo.contents.size();
        boolean z = false;
        FolderPagedView.calculateGridSize(size, 0, 0, this.mMaxGridCountX, this.mMaxGridCountY, this.mMaxItemsPerPage, this.mGridSize);
        this.mGridCountX = this.mGridSize[0];
        if (FeatureFlags.LAUNCHER3_NEW_FOLDER_ANIMATION && !FeatureFlags.LAUNCHER3_LEGACY_FOLDER_ICON && size > FolderIcon.NUM_ITEMS_IN_PREVIEW) {
            z = true;
        }
        this.mDisplayingUpperLeftQuadrant = z;
    }

    public boolean isItemInPreview(int i) {
        return isItemInPreview(0, i);
    }

    public boolean isItemInPreview(int i, int i2) {
        boolean z = true;
        if (i > 0 || this.mDisplayingUpperLeftQuadrant) {
            int i3 = i2 % this.mGridCountX;
            int i4 = i2 / this.mGridCountX;
            if (i3 >= 2 || i4 >= 2) {
                z = false;
            }
            return z;
        }
        if (i2 >= FolderIcon.NUM_ITEMS_IN_PREVIEW) {
            z = false;
        }
        return z;
    }
}
