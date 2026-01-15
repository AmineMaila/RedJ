package commands.ListCommands;

import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespType;
import commands.Command;
import store.ByteArrayKey;
import store.DataStore;
import store.Entry;
import store.datatypes.ListValue;

public class RPopCommand extends Command {
    private final ByteArrayKey key;

    public RPopCommand(List<RespType> args) {
        super(args);

        if (args.size() != 2) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'rpop' command"
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
            return new RespBulkString(null);
        }

        byte[] element = list.rpop();
        if (list.isEmpty()) {
            store.delete(key);
        }
        return new RespBulkString(element);
    }
}
