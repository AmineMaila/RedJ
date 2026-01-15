package store.datatypes;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

    public List<byte[]> range(long start, long end) {
        int n = data.size();

        if (n == 0) {
            return List.of();
        }

        if (start < 0) {
            start = start + n;
        }
        if (end < 0) {
            end = end + n;
        }

        // we do it again incase we wrap around twice, we bound it to 0
        if (start < 0) {
            start = 0;
        }

        if (end >= n) {
            end = n - 1;
        }

        if (start > end || start >= n) {
            return List.of();
        }
        return data.stream()
            .skip(start)
            .limit(end - start + 1)
            .toList();
    }

    public byte[] get(long index) {
        int n = data.size();

        if (n == 0) {
            return null;
        }

        if (index < 0)
            index += n;

        if (index < 0 || index >= n) {
            return null;
        }

        Iterator<byte[]> it = data.iterator();
        for (int i = 0; i < index; i++) {
            it.next();
        }
        return it.next();
    }

    public void set(long index, byte[] value) {
        int n = data.size();

        if (index < 0)
            index += n;

        if (index < 0 || index >= n) {
            throw new IndexOutOfBoundsException();
        }

        ListIterator<byte[]> it = new ArrayList<>(data).listIterator();
        for (int i = 0; i < index; i++) {
            it.next();
        }
        it.set(value);
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
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
