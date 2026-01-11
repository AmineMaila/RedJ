package store.datatypes;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import client.resptypes.RespArray;
import client.resptypes.RespBulkString;
import client.resptypes.RespType;

public final class ListValue implements Value {
    private Deque<byte[]> data = new ArrayDeque<>();

    public void lpush(byte[] element) {
        data.addFirst(element);
    }

    public void rpush(byte[] element) {
        data.addLast(element);
    }

    public byte[] lpop() {
        return data.pollFirst();
    }

    public byte[] rpop() {
        return data.pollLast();
    }

    public List<byte[]> range(int start, int end) {
        return data.stream()
            .skip(start)
            .limit(end - start + 1L)
            .toList();
    }

    public int size() {
        return data.size();
    }
    
    @Override
    public EntryType type() {
        return EntryType.LIST;
    }

    @Override
    public RespType toResp() {
        List<RespType> items = new ArrayList<>();
        for (var e : data) {
            items.add(new RespBulkString(e));
        }
        return new RespArray(items);
    }
}
