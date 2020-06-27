package com.android.launcher3.logging;

import android.os.Process;
import android.text.TextUtils;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.model.nano.LauncherDumpProto.ContainerType;
import com.android.launcher3.model.nano.LauncherDumpProto.DumpTarget;
import com.android.launcher3.model.nano.LauncherDumpProto.ItemType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DumpTargetWrapper {
    ArrayList<DumpTargetWrapper> children;
    DumpTarget node;

    public DumpTargetWrapper() {
        this.children = new ArrayList<>();
    }

    public DumpTargetWrapper(int i, int i2) {
        this();
        this.node = newContainerTarget(i, i2);
    }

    public DumpTargetWrapper(ItemInfo itemInfo) {
        this();
        this.node = newItemTarget(itemInfo);
    }

    public DumpTarget getDumpTarget() {
        return this.node;
    }

    public void add(DumpTargetWrapper dumpTargetWrapper) {
        this.children.add(dumpTargetWrapper);
    }

    public List<DumpTarget> getFlattenedList() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.node);
        if (!this.children.isEmpty()) {
            Iterator it = this.children.iterator();
            while (it.hasNext()) {
                arrayList.addAll(((DumpTargetWrapper) it.next()).getFlattenedList());
            }
            arrayList.add(this.node);
        }
        return arrayList;
    }

    public DumpTarget newItemTarget(ItemInfo itemInfo) {
        DumpTarget dumpTarget = new DumpTarget();
        dumpTarget.type = 1;
        int i = itemInfo.itemType;
        if (i == 4) {
            dumpTarget.itemType = 2;
        } else if (i != 6) {
            switch (i) {
                case 0:
                    dumpTarget.itemType = 1;
                    break;
                case 1:
                    dumpTarget.itemType = 0;
                    break;
            }
        } else {
            dumpTarget.itemType = 3;
        }
        return dumpTarget;
    }

    public DumpTarget newContainerTarget(int i, int i2) {
        DumpTarget dumpTarget = new DumpTarget();
        dumpTarget.type = 2;
        dumpTarget.containerType = i;
        dumpTarget.pageId = i2;
        return dumpTarget;
    }

    public static String getDumpTargetStr(DumpTarget dumpTarget) {
        if (dumpTarget == null) {
            return "";
        }
        switch (dumpTarget.type) {
            case 1:
                return getItemStr(dumpTarget);
            case 2:
                String fieldName = LoggerUtils.getFieldName(dumpTarget.containerType, ContainerType.class);
                if (dumpTarget.containerType == 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(fieldName);
                    sb.append(" id=");
                    sb.append(dumpTarget.pageId);
                    fieldName = sb.toString();
                } else if (dumpTarget.containerType == 3) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(fieldName);
                    sb2.append(" grid(");
                    sb2.append(dumpTarget.gridX);
                    sb2.append(",");
                    sb2.append(dumpTarget.gridY);
                    sb2.append(")");
                    fieldName = sb2.toString();
                }
                return fieldName;
            default:
                return "UNKNOWN TARGET TYPE";
        }
    }

    private static String getItemStr(DumpTarget dumpTarget) {
        String fieldName = LoggerUtils.getFieldName(dumpTarget.itemType, ItemType.class);
        if (!TextUtils.isEmpty(dumpTarget.packageName)) {
            StringBuilder sb = new StringBuilder();
            sb.append(fieldName);
            sb.append(", package=");
            sb.append(dumpTarget.packageName);
            fieldName = sb.toString();
        }
        if (!TextUtils.isEmpty(dumpTarget.component)) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(fieldName);
            sb2.append(", component=");
            sb2.append(dumpTarget.component);
            fieldName = sb2.toString();
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(fieldName);
        sb3.append(", grid(");
        sb3.append(dumpTarget.gridX);
        sb3.append(",");
        sb3.append(dumpTarget.gridY);
        sb3.append("), span(");
        sb3.append(dumpTarget.spanX);
        sb3.append(",");
        sb3.append(dumpTarget.spanY);
        sb3.append("), pageIdx=");
        sb3.append(dumpTarget.pageId);
        sb3.append(" user=");
        sb3.append(dumpTarget.userType);
        return sb3.toString();
    }

    public DumpTarget writeToDumpTarget(ItemInfo itemInfo) {
        String str;
        String str2;
        DumpTarget dumpTarget = this.node;
        if (itemInfo.getTargetComponent() == null) {
            str = "";
        } else {
            str = itemInfo.getTargetComponent().flattenToString();
        }
        dumpTarget.component = str;
        DumpTarget dumpTarget2 = this.node;
        if (itemInfo.getTargetComponent() == null) {
            str2 = "";
        } else {
            str2 = itemInfo.getTargetComponent().getPackageName();
        }
        dumpTarget2.packageName = str2;
        if (itemInfo instanceof LauncherAppWidgetInfo) {
            LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInfo;
            this.node.component = launcherAppWidgetInfo.providerName.flattenToString();
            this.node.packageName = launcherAppWidgetInfo.providerName.getPackageName();
        }
        this.node.gridX = itemInfo.cellX;
        this.node.gridY = itemInfo.cellY;
        this.node.spanX = itemInfo.spanX;
        this.node.spanY = itemInfo.spanY;
        this.node.userType = itemInfo.user.equals(Process.myUserHandle()) ^ true ? 1 : 0;
        return this.node;
    }
}
