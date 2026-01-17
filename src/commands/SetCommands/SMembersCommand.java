package commands.SetCommands;

import java.util.ArrayList;
import java.util.List;

import client.resptypes.RespArray;
import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespType;
import commands.Command;
import store.ByteArrayKey;
import store.DataStore;
import store.Entry;
import store.datatypes.SetValue;

public class SMembersCommand extends Command {

    private final ByteArrayKey key;

    public SMembersCommand(List<RespType> args) {
        super(args);

        if (args.size() != 2) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'smembers' command"
            );
        }

        this.key = new ByteArrayKey(((RespBulkString) args.get(1)).data());
    }

    @Override
    public RespType execute(DataStore store) {
        Entry entry = store.get(key);

        if (entry == null) {
            return new RespArray(List.of());
        }

        if (!(entry.getValue() instanceof SetValue set)) {
            throw new RespError(
                "WRONGTYPE",
                "Operation against a key holding the wrong kind of value"
            );
        }

        List<RespType> items = new ArrayList<>(set.size());
        for (var m : set.members()) {
            items.add(new RespBulkString(m.bytes()));
        }

        return new RespArray(items);
    }
}

