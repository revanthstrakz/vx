package com.lody.virtual.client.hook.delegate;

import android.app.ActivityManager.TaskDescription;

public interface TaskDescriptionDelegate {
    TaskDescription getTaskDescription(TaskDescription taskDescription);
}
