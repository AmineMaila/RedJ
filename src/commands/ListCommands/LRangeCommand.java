package commands.ListCommands;

import java.util.List;

import client.resptypes.RespArray;
import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespType;
import commands.Command;
import store.ByteArrayKey;
import store.DataStore;
import store.Entry;
import store.datatypes.ListValue;

public class LRangeCommand extends Command {
    private final ByteArrayKey key;
    private long start;
    private long end;

    public LRangeCommand(List<RespType> args) {
        super(args);

        if (args.size() != 4) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'lrange' command"
            );
        }

        key = new ByteArrayKey(((RespBulkString)args.get(1)).data());
        try {
            start = Long.parseLong(((RespBulkString)args.get(2)).toString());
            end = Long.parseLong(((RespBulkString)args.get(3)).toString());
        } catch (NumberFormatException ne) {
            throw new RespError("ERR", "value is not an integer or out of range");
        }
    }
    
    @Override
    public RespType execute(DataStore store) {
        Entry entry = store.get(key);
        if (entry == null){
            return new RespArray(List.of());
        }
        if (!(entry.getValue() instanceof ListValue list))
            throw new RespError("WRONGTYPE", "Operation against a key holding the wrong kind of value");

        List<byte[]> values = list.range(start, end);
        List<RespType> respValues = values.stream()
            .map(v -> (RespType) new RespBulkString(v))
            .toList();
        return new RespArray(respValues);
    }
}
