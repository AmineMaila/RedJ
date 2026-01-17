package commands.HashCommands;

import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespInteger;
import client.resptypes.RespType;
import commands.Command;
import store.ByteArrayKey;
import store.DataStore;
import store.Entry;
import store.datatypes.HashValue;

public class HExistsCommand extends Command {

    private final ByteArrayKey key;
    private final byte[] field;

    public HExistsCommand(List<RespType> args) {
        super(args);

        if (args.size() != 3) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'hexists' command"
            );
        }

        this.key = new ByteArrayKey(((RespBulkString) args.get(1)).data());
        this.field = ((RespBulkString) args.get(2)).data();
    }

    @Override
    public RespType execute(DataStore store) {
        Entry entry = store.get(key);

        if (entry == null) {
            return new RespInteger(0);
        }

        if (!(entry.getValue() instanceof HashValue hash)) {
            throw new RespError(
                "WRONGTYPE",
                "Operation against a key holding the wrong kind of value"
            );
        }

        return new RespInteger(hash.hexists(field) ? 1 : 0);
    }
}
