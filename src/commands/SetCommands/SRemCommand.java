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

public class SRemCommand extends Command {

    private final ByteArrayKey key;

    public SRemCommand(List<RespType> args) {
        super(args);

        if (args.size() < 3) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'srem' command"
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

        int removed = 0;
        for (int i = 2; i < args.size(); i++) {
            byte[] member = ((RespBulkString) args.get(i)).data();
            if (set.srem(member)) {
                removed++;
            }
        }

        if (set.size() == 0) {
            store.delete(key);
        }

        return new RespInteger(removed);
    }
}

