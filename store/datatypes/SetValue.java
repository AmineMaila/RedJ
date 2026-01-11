package store.datatypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import client.resptypes.RespArray;
import client.resptypes.RespBulkString;
import client.resptypes.RespType;

public final class SetValue implements Value {
    private final Set<byte[]> members = new HashSet<>();

    public boolean add(byte[] member) {
        return members.add(member);
    }

    public Set<byte[]> members() {
        return members;
    }

    @Override
    public EntryType type() {
        return EntryType.SET;
    }

    @Override
    public RespType toResp() {
        List<RespType> items = new ArrayList<>();
        for (var m : members) {
            items.add(new RespBulkString(m));
        }
        return new RespArray(items);
    }
}
