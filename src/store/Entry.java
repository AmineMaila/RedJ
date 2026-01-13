package store;

import store.datatypes.Value;

public class Entry {
    private Value value;
    private Long expiresAt; // ms

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
        return this.expiresAt;
    }

    public void setValue(Value newVal) {
        this.value = newVal;
    }

    public Value getValue() {
        return this.value;
    }
}
