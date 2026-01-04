package store.datatypes;

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
    
    @Override
    public EntryType type() {
        return EntryType.STRING;
    }
}
