package store.datatypes;

import client.resptypes.RespType;

public sealed interface Value permits StringValue, HashValue, ListValue, SetValue {
    EntryType type();
    RespType toResp();
}
