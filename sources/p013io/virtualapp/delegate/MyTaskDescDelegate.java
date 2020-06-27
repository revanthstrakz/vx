package p013io.virtualapp.delegate;

import android.annotation.TargetApi;
import android.app.ActivityManager.TaskDescription;
import com.lody.virtual.client.hook.delegate.TaskDescriptionDelegate;
import com.lody.virtual.p007os.VUserManager;

@TargetApi(21)
/* renamed from: io.virtualapp.delegate.MyTaskDescDelegate */
public class MyTaskDescDelegate implements TaskDescriptionDelegate {
    public TaskDescription getTaskDescription(TaskDescription taskDescription) {
        if (taskDescription == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(VUserManager.get().getUserName());
        sb.append("] ");
        String sb2 = sb.toString();
        if ((taskDescription.getLabel() != null ? taskDescription.getLabel() : "").startsWith(sb2)) {
            return taskDescription;
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append(sb2);
        sb3.append(taskDescription.getLabel());
        return new TaskDescription(sb3.toString(), taskDescription.getIcon(), taskDescription.getPrimaryColor());
    }
}
