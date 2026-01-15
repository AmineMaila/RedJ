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

public class LLenCommand extends Command {
    private final ByteArrayKey key;

    public LLenCommand(List<RespType> args) {
        super(args);

        if (args.size() != 2) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'llen' command"
            );
        }

        key = new ByteArrayKey(((RespBulkString)args.get(1)).data());
    }
    
    @Override
    public RespType execute(DataStore store) {
        Entry entry = store.get(key);
        if (entry != null) {
            if (!(entry.getValue() instanceof ListValue listValue))
                throw new RespError("WRONGTYPE", "Operation against a key holding the wrong kind of value");
            return new RespInteger(listValue.size());
        } else {
            return new RespInteger(0);
        }
    }
}
