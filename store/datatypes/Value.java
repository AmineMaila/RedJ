package store.datatypes;

public sealed interface Value permits StringValue, HashValue, ListValue, SetValue {
    EntryType type();
}
