package store;

import java.util.HashMap;
import java.util.Map;

public class DataStore {
    private final Map<ByteArrayKey, Entry> STORE = new HashMap<>();

    public Entry get(ByteArrayKey key) {
        Entry result = STORE.get(key);
        if (result == null || result.isExpired()) {
            STORE.remove(key);
            return null;
        }
        return result;
    }

    public void set(ByteArrayKey key, Entry value) {
        STORE.put(key, value);
    }

    public Entry delete(ByteArrayKey key) {
        return STORE.remove(key);
    }

    public Map<ByteArrayKey, Entry> store() {
        return this.STORE;
    }
}
