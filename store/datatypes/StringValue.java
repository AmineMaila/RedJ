package store.datatypes;

import client.resptypes.RespBulkString;
import client.resptypes.RespType;

public final class StringValue implements Value {
    private byte[] data;

    public StringValue(byte[] data) {
        this.data = data;
    }

    public byte[] get() {
        return data;
    }

    public void set(byte[] data) {
        this.data = data;
    }

    public int size() {
        return this.data.length;
    }
    
    public RespBulkString toRespBulk() {
        return new RespBulkString(data);
    }

    @Override
    public EntryType type() {
        return EntryType.STRING;
    }

    @Override
    public RespType toResp() {
        return new RespBulkString(data);
    }
}
