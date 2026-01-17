package commands.SetCommands;

import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespInteger;
import client.resptypes.RespType;
import commands.Command;
import store.ByteArrayKey;
import store.DataStore;
import store.Entry;
import store.datatypes.SetValue;

public class SCardCommand extends Command {

    private final ByteArrayKey key;

    public SCardCommand(List<RespType> args) {
        super(args);

        if (args.size() != 2) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'scard' command"
            );
        }

        this.key = new ByteArrayKey(((RespBulkString) args.get(1)).data());
    }

    @Override
    public RespType execute(DataStore store) {
        Entry entry = store.get(key);

        if (entry == null) {
            return new RespInteger(0);
        }

        if (!(entry.getValue() instanceof SetValue set)) {
            throw new RespError(
                "WRONGTYPE",
                "Operation against a key holding the wrong kind of value"
            );
        }

        return new RespInteger(set.size());
    }
}

