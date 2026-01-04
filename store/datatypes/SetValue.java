package store.datatypes;

import java.util.HashSet;
import java.util.Set;

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
}
