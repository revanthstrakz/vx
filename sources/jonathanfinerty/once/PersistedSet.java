package jonathanfinerty.once;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class PersistedSet {
    private static final String DELIMITER = ",";
    private static final String STRING_SET_KEY = "PersistedSetValues";
    private final AsyncSharedPreferenceLoader preferenceLoader;
    private SharedPreferences preferences;
    private Set<String> set = new HashSet();

    public PersistedSet(Context context, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(PersistedSet.class.getSimpleName());
        sb.append(str);
        this.preferenceLoader = new AsyncSharedPreferenceLoader(context, sb.toString());
    }

    private void waitForLoad() {
        if (this.preferences == null) {
            this.preferences = this.preferenceLoader.get();
            if (VERSION.SDK_INT >= 11) {
                this.set = this.preferences.getStringSet(STRING_SET_KEY, new HashSet());
            } else {
                this.set = new HashSet(StringToStringSet(this.preferences.getString(STRING_SET_KEY, null)));
            }
        }
    }

    public void put(String str) {
        waitForLoad();
        this.set.add(str);
        updatePreferences();
    }

    public boolean contains(String str) {
        waitForLoad();
        return this.set.contains(str);
    }

    public void remove(String str) {
        waitForLoad();
        this.set.remove(str);
        updatePreferences();
    }

    public void clear() {
        waitForLoad();
        this.set.clear();
        updatePreferences();
    }

    private void updatePreferences() {
        Editor edit = this.preferences.edit();
        if (VERSION.SDK_INT >= 11) {
            edit.putStringSet(STRING_SET_KEY, this.set);
        } else {
            edit.putString(STRING_SET_KEY, StringSetToString(this.set));
        }
        edit.apply();
    }

    private String StringSetToString(Set<String> set2) {
        StringBuilder sb = new StringBuilder();
        String str = "";
        for (String str2 : set2) {
            sb.append(str);
            sb.append(str2);
            str = DELIMITER;
        }
        return sb.toString();
    }

    @NonNull
    private Set<String> StringToStringSet(@Nullable String str) {
        if (str == null) {
            return new HashSet();
        }
        return new HashSet(Arrays.asList(str.split(DELIMITER)));
    }
}
