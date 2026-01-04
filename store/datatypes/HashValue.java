package store.datatypes;

import java.util.HashMap;
import java.util.Map;

public final class HashValue implements Value {
    private final Map<byte[], byte[]> fields = new HashMap<>();
    
    public void hset(byte[] key, byte[] value) {
        this.fields.put(key, value);
    }

    public byte[] hget(byte[] key) {
        return fields.get(key);
    }

    public Map<byte[], byte[]> getAll() {
        return this.fields;
    }
    
    @Override
    public EntryType type() {
        return EntryType.HASH;
    }
}
