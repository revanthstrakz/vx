package jonathanfinerty.once;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import java.util.concurrent.ExecutionException;

class AsyncSharedPreferenceLoader {
    private final AsyncTask<String, Void, SharedPreferences> asyncTask = new AsyncTask<String, Void, SharedPreferences>() {
        /* access modifiers changed from: protected */
        public SharedPreferences doInBackground(String... strArr) {
            return AsyncSharedPreferenceLoader.this.context.getSharedPreferences(strArr[0], 0);
        }
    };
    /* access modifiers changed from: private */
    public final Context context;

    public AsyncSharedPreferenceLoader(Context context2, String str) {
        this.context = context2;
        this.asyncTask.execute(new String[]{str});
    }

    public SharedPreferences get() {
        try {
            return (SharedPreferences) this.asyncTask.get();
        } catch (InterruptedException | ExecutionException unused) {
            return null;
        }
    }
}
