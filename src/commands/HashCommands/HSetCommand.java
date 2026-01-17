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

public class HSetCommand extends Command {
    private final ByteArrayKey key;

    public HSetCommand(List<RespType> args) {
        super(args);

        if (args.size() < 4 || ((args.size() - 2) & 1) != 0) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'hset' command"
            );
        }

        this.key = new ByteArrayKey(((RespBulkString) args.get(1)).data());
    }

    @Override
    public RespType execute(DataStore store) {
        Entry entry = store.get(key);
        HashValue hash;

        if (entry == null) {
            hash = new HashValue();
            entry = new Entry(hash);
            store.set(key, entry);
        } else {
            if (!(entry.getValue() instanceof HashValue)) {
                throw new RespError(
                    "WRONGTYPE",
                    "Operation against a key holding the wrong kind of value"
                );
            }
            hash = (HashValue) entry.getValue();
        }

        int added = 0;

        for (int i = 2; i < args.size(); i += 2) {
            byte[] field = ((RespBulkString) args.get(i)).data();
            byte[] value = ((RespBulkString) args.get(i + 1)).data();

            if (hash.hset(field, value)) {
                added++;
            }
        }

        return new RespInteger(added);
    }
}

