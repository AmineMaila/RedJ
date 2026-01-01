package store;

import java.time.Instant;

public class Entry {
    private final byte[] value;
    private final Instant expiresAt;
    private final EntryType type;

    public Entry(byte[] value) {
        this(value, null, EntryType.STRING);
    }

    public Entry(byte[] value, Instant expiresAt, EntryType type) {
        this.value = value;
        this.expiresAt = expiresAt;
        this.type = EntryType.STRING;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public byte[] getValue() {
        return this.value;
    }

    public EntryType getType() {
        return this.type;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }
}
