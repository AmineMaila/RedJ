package store.datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.resptypes.RespArray;
import client.resptypes.RespBulkString;
import client.resptypes.RespType;
import store.ByteArrayKey;

public final class HashValue implements Value {
    private final Map<ByteArrayKey, byte[]> fields = new HashMap<>();

    public boolean hset(byte[] field, byte[] value) {
        ByteArrayKey k = new ByteArrayKey(field);
        boolean isNew = !fields.containsKey(k);
        fields.put(k, value);
        return isNew;
    }

    public byte[] hget(byte[] field) {
        return fields.get(new ByteArrayKey(field));
    }

    public boolean hdel(byte[] field) {
        return fields.remove(new ByteArrayKey(field)) != null;
    }

    public boolean hexists(byte[] field) {
        return fields.containsKey(new ByteArrayKey(field));
    }

    public int size() {
        return fields.size();
    }

    public Map<ByteArrayKey, byte[]> getAll() {
        return fields;
    }

    @Override
    public EntryType type() {
        return EntryType.HASH;
    }

    @Override
    public RespType toResp() {
        List<RespType> items = new ArrayList<>(fields.size() * 2);
        for (var e : fields.entrySet()) {
            items.add(new RespBulkString(e.getKey().bytes()));
            items.add(new RespBulkString(e.getValue()));
        }
        return new RespArray(items);
    }
}

