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

public class HIncrByCommand extends Command {

    private final ByteArrayKey key;
    private final byte[] field;
    private final long increment;

    public HIncrByCommand(List<RespType> args) {
        super(args);

        if (args.size() != 4) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'hincrby' command"
            );
        }

        this.key = new ByteArrayKey(((RespBulkString) args.get(1)).data());
        this.field = ((RespBulkString) args.get(2)).data();

        try {
            this.increment = Long.parseLong(((RespBulkString) args.get(3)).toString());
        } catch (NumberFormatException ne) {
            throw new RespError("ERR", "value is not an integer or out of range");
        }
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

        long current = 0;
        byte[] raw = hash.hget(field);

        if (raw != null) {
            try {
                current = Long.parseLong(new String(raw));
            } catch (NumberFormatException e) {
                throw new RespError("ERR", "hash value is not an integer");
            }
        }

        long result;
        try {
            result = Math.addExact(current, increment);
        } catch (ArithmeticException e) {
            throw new RespError("ERR", "increment or decrement would overflow");
        }

        hash.hset(field, Long.toString(result).getBytes());
        return new RespInteger(result);
    }
}
