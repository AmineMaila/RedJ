package command;

import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespType;
import store.Entry;
import store.datatypes.EntryType;
import store.datatypes.StringValue;
import store.datatypes.Value;

public class GetCommand extends Command {
    private final byte[] key;

    public GetCommand(List<RespType> args) {
        super(args);
        if (args.size() != 2) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'get' command"
            );
        }
        this.key = ((RespBulkString) args.get(1)).data();
    }

    @Override
    public RespType execute(CommandContext ctx) {
        Entry entry = ctx.store().get(key);

        if (entry == null) {
            return new RespBulkString(null);
        }

        if (entry.isExpired()) {
            ctx.store().delete(key);
            return new RespBulkString(null);
        }

        Value val = entry.value();
        if (val.type() != EntryType.STRING) {
            throw new RespError("WRONGTYPE", "Operation against a key holding the wrong kind of value");
        }
        StringValue sv = (StringValue) val;
        return new RespBulkString(sv.get());
    }
}
