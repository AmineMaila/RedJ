package store;

import java.util.HashMap;
import java.util.Map;

public class DataStore {
    private final Map<ByteArrayKey, Entry> STORE = new HashMap<>();

    public Entry get(ByteArrayKey key) {
        Entry result = STORE.get(key);
        if (result != null) {
            System.out.println("[" + key + ", " + result.getValue() + ", ex: " + result.getExpiresAt() + "]");
            System.out.println("expiresAt: " + result.getExpiresAt() +  " | currentTimeMillis: " + System.currentTimeMillis());
            System.out.println("isExpired(): " + result.isExpired());

        }
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
        Entry result = STORE.remove(key);
        if (result == null || result.isExpired())
            return null;
        return result;
    }

    public Map<ByteArrayKey, Entry> store() {
        return this.STORE;
    }
}
