package store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    private final Map<String, Entry> STORE = new ConcurrentHashMap<>();

    public Entry get(String key) {
        Entry result = STORE.get(key);
        if (result == null || result.isExpired()) {
            STORE.remove(key);
            return null;
        }
        return result;
    }

    public void set(String key, Entry value) {
        STORE.put(key, value);
    }

    public void delete(String key) {
        STORE.remove(key);
    }
}
