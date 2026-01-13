package store.datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.resptypes.RespArray;
import client.resptypes.RespBulkString;
import client.resptypes.RespType;

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

    @Override
    public RespType toResp() {
        List<RespType> items = new ArrayList<>();
        for (var e : fields.entrySet()) {
            items.add(new RespBulkString(e.getKey()));
            items.add(new RespBulkString(e.getValue()));
        }
        return new RespArray(items);
    }
}
