package store;

import store.datatypes.Value;

public class Entry {
    private final Value value;
    private Long expiresAt;

    public Entry(Value value) {
        this(value, null);
    }

    public Entry(Value value, Long expiresAt) {
        this.value = value;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return expiresAt != null && System.currentTimeMillis() > expiresAt;
    }

    public void setExpiresAt(Long newExpiresAT) {
        this.expiresAt = newExpiresAT;
    }

    public Long getExpiresAt() {
        return Long.valueOf(this.expiresAt);
    }


    public Value value() {
        return this.value;
    }
}
