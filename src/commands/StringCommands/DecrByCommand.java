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

public class DecrByCommand extends Command {
    private final ByteArrayKey key;
    private final long decrement;

    public DecrByCommand(List<RespType> args) {
        super(args);

        if (args.size() != 3) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'decrby' command"
            );
        }

        this.key = new ByteArrayKey(((RespBulkString)args.get(1)).data());
        try {
            decrement = Long.parseLong(((RespBulkString)args.get(2)).toString());
        } catch (NumberFormatException ne) {
            throw new RespError("ERR", "value is not an integer or out of range");
        }
    }

    @Override
    public RespType execute(DataStore store) {
        Entry oldVal = store.get(key);

        try {
            if (oldVal == null) {
                long negatedValue = Math.negateExact(decrement);
                store.set(key, new Entry(new StringValue(Long.toString(negatedValue).getBytes(StandardCharsets.US_ASCII))));
                return new RespInteger(negatedValue);
            }

            if (!(oldVal.getValue() instanceof StringValue strVal)) {
                throw new RespError("WRONGTYPE", "Operation against a key holding the wrong kind of value");
            }

            long value = Long.parseLong(strVal.toString());
            value = Math.subtractExact(value, decrement);
            strVal.set(Long.toString(value).getBytes(StandardCharsets.US_ASCII));

            return new RespInteger(value);
        } catch (NumberFormatException ne) {
            throw new RespError("ERR", "value is not an integer or out of range");
        } catch (ArithmeticException ae) {
            throw new RespError("ERR", "increment or decrement would overflow");
        }
    }
}
