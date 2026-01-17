package commands.HashCommands;

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
import store.datatypes.HashValue;

public class HGetAllCommand extends Command {

    private final ByteArrayKey key;

    public HGetAllCommand(List<RespType> args) {
        super(args);

        if (args.size() != 2) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'hgetall' command"
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

        if (!(entry.getValue() instanceof HashValue hash)) {
            throw new RespError(
                "WRONGTYPE",
                "Operation against a key holding the wrong kind of value"
            );
        }

        List<RespType> items = new ArrayList<>(hash.size() * 2);
        for (var e : hash.getAll().entrySet()) {
            items.add(new RespBulkString(e.getKey().bytes()));
            items.add(new RespBulkString(e.getValue()));
        }

        return new RespArray(items);
    }
}
