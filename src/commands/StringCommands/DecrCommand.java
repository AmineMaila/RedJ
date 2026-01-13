package commands.StringCommands;

import java.nio.charset.StandardCharsets;
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

public class DecrCommand extends Command {
    private final ByteArrayKey key;

    public DecrCommand(List<RespType> args) {
        super(args);

        if (args.size() != 2) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'decr' command"
            );
        }

        this.key = new ByteArrayKey(((RespBulkString)args.get(1)).data());
    }

    @Override
    public RespType execute(DataStore store) {
        Entry oldVal = store.get(key);

        if (oldVal == null) {
            store.set(key, new Entry(new StringValue(new byte[]{'-', '1'})));
            return new RespInteger(-1);
        }

        if (!(oldVal.getValue() instanceof StringValue strVal)) {
            throw new RespError("WRONGTYPE", "Operation against a key holding the wrong kind of value");
        }

        long value;
        try {
            value = Long.parseLong(strVal.toString());
            value = Math.subtractExact(value, 1);
            strVal.set(Long.toString(value).getBytes(StandardCharsets.US_ASCII));
        } catch (NumberFormatException ne) {
            throw new RespError("ERR", "value is not an integer or out of range");
        } catch (ArithmeticException ae) {
            throw new RespError("ERR", "increment or decrement would overflow");
        }

        return new RespInteger(value);
    }
}
