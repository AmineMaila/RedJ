package commands.StringCommands;

import java.util.Arrays;
import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespInteger;
import client.resptypes.RespType;
import commands.Command;
import store.ByteArrayKey;
import store.DataStore;
import store.Entry;
import store.datatypes.StringValue;

public class AppendCommand extends Command {
    private final ByteArrayKey key;
    private final StringValue appendBytes;

    public AppendCommand(List<RespType> args) {
        super(args);

        if (args.size() != 3) {
            throw new RespError("ERR", "wrong number of arguments for 'append' command");
        }

        key = new ByteArrayKey(((RespBulkString) args.get(1)).data());
        appendBytes = new StringValue(((RespBulkString)args.get(2)).data());
    }

    @Override
    public RespType execute(DataStore store) {
        Entry oldVal = store.get(key);
        if (oldVal == null) {
            store.set(key, new Entry(appendBytes));
            return new RespInteger(appendBytes.size());
        }

        if (!(oldVal.getValue() instanceof StringValue oldBytes)) {
            throw new RespError("WRONGTYPE", "Operation against a key holding the wrong kind of value");
        }
        
        byte[] newBytes = Arrays.copyOf(oldBytes.get(), oldBytes.size() + appendBytes.size());
        System.arraycopy(appendBytes, 0, newBytes, oldBytes.size(), appendBytes.size());

        oldVal.setValue(new StringValue(newBytes));

        return new RespInteger(newBytes.length);
    }
}
