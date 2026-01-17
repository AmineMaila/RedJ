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

public class SAddCommand extends Command {

    private final ByteArrayKey key;

    public SAddCommand(List<RespType> args) {
        super(args);

        if (args.size() < 3) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'sadd' command"
            );
        }

        this.key = new ByteArrayKey(((RespBulkString) args.get(1)).data());
    }

    @Override
    public RespType execute(DataStore store) {
        Entry entry = store.get(key);
        SetValue set;

        if (entry == null) {
            set = new SetValue();
            entry = new Entry(set);
            store.set(key, entry);
        } else {
            if (!(entry.getValue() instanceof SetValue)) {
                throw new RespError(
                    "WRONGTYPE",
                    "Operation against a key holding the wrong kind of value"
                );
            }
            set = (SetValue) entry.getValue();
        }

        int added = 0;
        for (int i = 2; i < args.size(); i++) {
            byte[] member = ((RespBulkString)args.get(i)).data();
            if (set.sadd(member)) {
                added++;
            }
        }

        return new RespInteger(added);
    }
}

