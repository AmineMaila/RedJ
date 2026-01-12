package store;

import java.util.Arrays;

public class ByteArrayKey {
    private final byte[] data;

    public ByteArrayKey(byte[] data) {
        this.data = Arrays.copyOf(data, data.length);
    }

    public byte[] bytes() {
        return Arrays.copyOf(data, data.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ByteArrayKey other)) return false;
        return Arrays.equals(this.data, other.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}
