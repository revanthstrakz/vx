package com.android.launcher3.logging;

import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.View;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.DeleteDropTarget;
import com.android.launcher3.InfoDropTarget;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.UninstallDropTarget;
import com.android.launcher3.userevent.nano.LauncherLogExtensions.TargetExtension;
import com.android.launcher3.userevent.nano.LauncherLogProto.Action;
import com.android.launcher3.userevent.nano.LauncherLogProto.Action.Command;
import com.android.launcher3.userevent.nano.LauncherLogProto.Action.Direction;
import com.android.launcher3.userevent.nano.LauncherLogProto.Action.Touch;
import com.android.launcher3.userevent.nano.LauncherLogProto.ContainerType;
import com.android.launcher3.userevent.nano.LauncherLogProto.ControlType;
import com.android.launcher3.userevent.nano.LauncherLogProto.ItemType;
import com.android.launcher3.userevent.nano.LauncherLogProto.LauncherEvent;
import com.android.launcher3.userevent.nano.LauncherLogProto.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class LoggerUtils {
    private static final String UNKNOWN = "UNKNOWN";
    private static final ArrayMap<Class, SparseArray<String>> sNameCache = new ArrayMap<>();

    public static String getFieldName(int i, Class cls) {
        SparseArray sparseArray;
        Field[] declaredFields;
        synchronized (sNameCache) {
            sparseArray = (SparseArray) sNameCache.get(cls);
            if (sparseArray == null) {
                sparseArray = new SparseArray();
                for (Field field : cls.getDeclaredFields()) {
                    if (field.getType() == Integer.TYPE && Modifier.isStatic(field.getModifiers())) {
                        try {
                            field.setAccessible(true);
                            sparseArray.put(field.getInt(null), field.getName());
                        } catch (IllegalAccessException unused) {
                        }
                    }
                }
                sNameCache.put(cls, sparseArray);
            }
        }
        String str = (String) sparseArray.get(i);
        return str != null ? str : UNKNOWN;
    }

    public static String getActionStr(Action action) {
        String str = "";
        int i = action.type;
        if (i == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(getFieldName(action.touch, Touch.class));
            String sb2 = sb.toString();
            if (action.touch == 3) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(sb2);
                sb3.append(" direction=");
                sb3.append(getFieldName(action.dir, Direction.class));
                sb2 = sb3.toString();
            }
            return sb2;
        } else if (i != 2) {
            return UNKNOWN;
        } else {
            return getFieldName(action.command, Command.class);
        }
    }

    public static String getTargetStr(Target target) {
        if (target == null) {
            return "";
        }
        switch (target.type) {
            case 1:
                return getItemStr(target);
            case 2:
                return getFieldName(target.controlType, ControlType.class);
            case 3:
                String fieldName = getFieldName(target.containerType, ContainerType.class);
                if (target.containerType == 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(fieldName);
                    sb.append(" id=");
                    sb.append(target.pageIndex);
                    fieldName = sb.toString();
                } else if (target.containerType == 3) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(fieldName);
                    sb2.append(" grid(");
                    sb2.append(target.gridX);
                    sb2.append(",");
                    sb2.append(target.gridY);
                    sb2.append(")");
                    fieldName = sb2.toString();
                }
                return fieldName;
            default:
                return "UNKNOWN TARGET TYPE";
        }
    }

    private static String getItemStr(Target target) {
        String fieldName = getFieldName(target.itemType, ItemType.class);
        if (target.packageNameHash != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(fieldName);
            sb.append(", packageHash=");
            sb.append(target.packageNameHash);
            sb.append(", predictiveRank=");
            sb.append(target.predictedRank);
            fieldName = sb.toString();
        }
        if (target.componentHash != 0) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(fieldName);
            sb2.append(", componentHash=");
            sb2.append(target.componentHash);
            sb2.append(", predictiveRank=");
            sb2.append(target.predictedRank);
            fieldName = sb2.toString();
        }
        if (target.intentHash != 0) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(fieldName);
            sb3.append(", intentHash=");
            sb3.append(target.intentHash);
            sb3.append(", predictiveRank=");
            sb3.append(target.predictedRank);
            fieldName = sb3.toString();
        }
        StringBuilder sb4 = new StringBuilder();
        sb4.append(fieldName);
        sb4.append(", grid(");
        sb4.append(target.gridX);
        sb4.append(",");
        sb4.append(target.gridY);
        sb4.append("), span(");
        sb4.append(target.spanX);
        sb4.append(",");
        sb4.append(target.spanY);
        sb4.append("), pageIdx=");
        sb4.append(target.pageIndex);
        return sb4.toString();
    }

    public static Target newItemTarget(View view) {
        if (view.getTag() instanceof ItemInfo) {
            return newItemTarget((ItemInfo) view.getTag());
        }
        return newTarget(1);
    }

    public static Target newItemTarget(ItemInfo itemInfo) {
        Target newTarget = newTarget(1);
        int i = itemInfo.itemType;
        if (i == 4) {
            newTarget.itemType = 3;
        } else if (i != 6) {
            switch (i) {
                case 0:
                    newTarget.itemType = 1;
                    newTarget.predictedRank = -100;
                    break;
                case 1:
                    newTarget.itemType = 2;
                    break;
                case 2:
                    newTarget.itemType = 4;
                    break;
            }
        } else {
            newTarget.itemType = 5;
        }
        return newTarget;
    }

    public static Target newDropTarget(View view) {
        if (!(view instanceof ButtonDropTarget)) {
            return newTarget(3);
        }
        Target newTarget = newTarget(2);
        if (view instanceof InfoDropTarget) {
            newTarget.controlType = 7;
        } else if (view instanceof UninstallDropTarget) {
            newTarget.controlType = 6;
        } else if (view instanceof DeleteDropTarget) {
            newTarget.controlType = 5;
        }
        return newTarget;
    }

    public static Target newTarget(int i, TargetExtension targetExtension) {
        Target target = new Target();
        target.type = i;
        target.extension = targetExtension;
        return target;
    }

    public static Target newTarget(int i) {
        Target target = new Target();
        target.type = i;
        return target;
    }

    public static Target newContainerTarget(int i) {
        Target newTarget = newTarget(3);
        newTarget.containerType = i;
        return newTarget;
    }

    public static Action newAction(int i) {
        Action action = new Action();
        action.type = i;
        return action;
    }

    public static Action newCommandAction(int i) {
        Action newAction = newAction(2);
        newAction.command = i;
        return newAction;
    }

    public static Action newTouchAction(int i) {
        Action newAction = newAction(0);
        newAction.touch = i;
        return newAction;
    }

    public static LauncherEvent newLauncherEvent(Action action, Target... targetArr) {
        LauncherEvent launcherEvent = new LauncherEvent();
        launcherEvent.srcTarget = targetArr;
        launcherEvent.action = action;
        return launcherEvent;
    }
}
