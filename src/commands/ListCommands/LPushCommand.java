package commands.ListCommands;

import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespInteger;
import client.resptypes.RespType;
import commands.Command;
import store.ByteArrayKey;
import store.DataStore;
import store.Entry;
import store.datatypes.ListValue;

public class LPushCommand extends Command {
    private final ByteArrayKey key;

    public LPushCommand(List<RespType> args) {
        super(args);

        if (args.size() < 3) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'lpush' command"
            );
        }

        key = new ByteArrayKey(((RespBulkString)args.get(1)).data());
    }
    
    @Override
    public RespType execute(DataStore store) {
        Entry entry = store.get(key);
        ListValue list;
        if (entry != null) {
            if (!(entry.getValue() instanceof ListValue listValue))
                throw new RespError("WRONGTYPE", "Operation against a key holding the wrong kind of value");
            list = listValue;
        } else {
            list = new ListValue();
            entry = new Entry(list);
            store.set(key, entry);
        }

        for (int i = 2; i < args.size(); i++) {
            byte[] element = ((RespBulkString)args.get(i)).data();
            list.lpush(element);
        }

        return new RespInteger(list.size());
    }
}
