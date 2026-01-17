package store.datatypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import client.resptypes.RespArray;
import client.resptypes.RespBulkString;
import client.resptypes.RespType;
import store.ByteArrayKey;

public final class SetValue implements Value {

    private final Set<ByteArrayKey> members = new HashSet<>();

    public boolean sadd(byte[] member) {
        return members.add(new ByteArrayKey(member));
    }

    public boolean srem(byte[] member) {
        return members.remove(new ByteArrayKey(member));
    }

    public boolean contains(byte[] member) {
        return members.contains(new ByteArrayKey(member));
    }

    public int size() {
        return members.size();
    }

    public Set<ByteArrayKey> members() {
        return members;
    }

    @Override
    public EntryType type() {
        return EntryType.SET;
    }

    @Override
    public RespType toResp() {
        List<RespType> items = new ArrayList<>(members.size());
        for (var m : members) {
            items.add(new RespBulkString(m.bytes()));
        }
        return new RespArray(items);
    }
}

