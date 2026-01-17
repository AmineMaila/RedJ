package commands.HashCommands;

import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespType;
import commands.Command;
import store.ByteArrayKey;
import store.DataStore;
import store.Entry;
import store.datatypes.HashValue;

public class HGetCommand extends Command {

    private final ByteArrayKey key;
    private final byte[] field;

    public HGetCommand(List<RespType> args) {
        super(args);

        if (args.size() != 3) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'hget' command"
            );
        }

        this.key = new ByteArrayKey(((RespBulkString) args.get(1)).data());
        this.field = ((RespBulkString) args.get(2)).data();
    }

    @Override
    public RespType execute(DataStore store) {
        Entry entry = store.get(key);

        if (entry == null) {
            return new RespBulkString(null);
        }

        if (!(entry.getValue() instanceof HashValue hash)) {
            throw new RespError(
                "WRONGTYPE",
                "Operation against a key holding the wrong kind of value"
            );
        }

        byte[] value = hash.hget(field);
        return new RespBulkString(value);
    }
}

