package store;

import java.time.Instant;

import store.datatypes.Value;

public class Entry {
    private final Value value;
    private final Instant expiresAt;

    public Entry(Value value) {
        this(value, null);
    }

    public Entry(Value value, Instant expiresAt) {
        this.value = value;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public Value value() {
        return this.value;
    }
}
