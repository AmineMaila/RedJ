package store;

import java.util.HashMap;
import java.util.Map;

public class DataStore {
    private final Map<byte[], Entry> STORE = new HashMap<>();

    public Entry get(byte[] key) {
        Entry result = STORE.get(key);
        if (result == null || result.isExpired()) {
            STORE.remove(key);
            return null;
        }
        return result;
    }

    public void set(byte[] key, Entry value) {
        STORE.put(key, value);
    }

    public void delete(byte[] key) {
        STORE.remove(key);
    }
}
