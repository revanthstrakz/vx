package jonathanfinerty.once;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class PersistedMap {
    private static final String DELIMITER = ",";
    private final Map<String, List<Long>> map = new ConcurrentHashMap();
    private final AsyncSharedPreferenceLoader preferenceLoader;
    private SharedPreferences preferences;

    public PersistedMap(Context context, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(PersistedMap.class.getSimpleName());
        sb.append(str);
        this.preferenceLoader = new AsyncSharedPreferenceLoader(context, sb.toString());
    }

    private void waitForLoad() {
        List list;
        if (this.preferences == null) {
            this.preferences = this.preferenceLoader.get();
            for (String str : this.preferences.getAll().keySet()) {
                try {
                    list = stringToList(this.preferences.getString(str, null));
                } catch (ClassCastException unused) {
                    list = loadFromLegacyStorageFormat(str);
                }
                this.map.put(str, list);
            }
        }
    }

    private List<Long> loadFromLegacyStorageFormat(String str) {
        long j = this.preferences.getLong(str, -1);
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(Long.valueOf(j));
        this.preferences.edit().putString(str, listToString(arrayList)).apply();
        return arrayList;
    }

    @NonNull
    public List<Long> get(String str) {
        waitForLoad();
        List<Long> list = (List) this.map.get(str);
        return list == null ? Collections.emptyList() : list;
    }

    public void put(String str, long j) {
        waitForLoad();
        List list = (List) this.map.get(str);
        if (list == null) {
            list = new ArrayList(1);
        }
        list.add(Long.valueOf(j));
        this.map.put(str, list);
        Editor edit = this.preferences.edit();
        edit.putString(str, listToString(list));
        edit.apply();
    }

    public void remove(String str) {
        waitForLoad();
        this.map.remove(str);
        Editor edit = this.preferences.edit();
        edit.remove(str);
        edit.apply();
    }

    public void clear() {
        waitForLoad();
        this.map.clear();
        Editor edit = this.preferences.edit();
        edit.clear();
        edit.apply();
    }

    private String listToString(List<Long> list) {
        StringBuilder sb = new StringBuilder();
        String str = "";
        for (Long l : list) {
            sb.append(str);
            sb.append(l);
            str = DELIMITER;
        }
        return sb.toString();
    }

    private List<Long> stringToList(String str) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }
        String[] split = str.split(DELIMITER);
        ArrayList arrayList = new ArrayList(split.length);
        for (String parseLong : split) {
            arrayList.add(Long.valueOf(Long.parseLong(parseLong)));
        }
        return arrayList;
    }
}
