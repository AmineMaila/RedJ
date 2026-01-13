package commands.StringCommands;

import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespType;
import commands.Command;
import client.resptypes.RespSimpleString;
import store.ByteArrayKey;
import store.DataStore;
import store.Entry;
import store.datatypes.StringValue;

public class SetCommand extends Command {
    private final ByteArrayKey key;
    private final StringValue value;
    private boolean nx;
    private boolean xx;
    private boolean keepTTL;
    private boolean get;
    private Long expireAtMilis;

    public SetCommand(List<RespType> args) {
        super(args);

        if (args.size() < 3) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'set' command"
            );
        }

        key = new ByteArrayKey(((RespBulkString) args.get(1)).data());
        byte[] rawValue = ((RespBulkString) args.get(2)).data();
        for (int i = 3; i < args.size(); i++) {
            String opt = args.get(i)
                            .toString()
                            .toUpperCase();
            switch (opt) {
                case "NX" -> nx = true;
                case "XX" -> xx = true;
                case "KEEPTTL" -> keepTTL = true;
                case "GET" -> get = true;
                case "EX" -> {
                    long seconds = parseLongArg(args, ++i);
                    expireAtMilis = System.currentTimeMillis() + seconds * 1000;
                }
                case "PX" -> {
                    long millis = parseLongArg(args, ++i);
                    expireAtMilis = System.currentTimeMillis() + millis;
                }
                case "EXAT" -> {
                    long seconds = parseLongArg(args, ++i);
                    expireAtMilis = seconds * 1000;
                }
                case "PXAT" -> {
                    expireAtMilis = parseLongArg(args, ++i);
                }
                default -> throw new RespError("ERR", "syntax error");
            }
        }
        if (nx && xx)
            throw new RespError("ERR", "NX and XX are mutually exclusive");

        value = new StringValue(rawValue);
    }

    private long parseLongArg(List<RespType> args, int index) {
        if (index + 1 >= args.size())
            throw new RespError("ERR", "expected expiration time");
        try {
            return Long.parseLong(((RespBulkString) args.get(index)).toString());
        } catch (NumberFormatException ne) {
            throw new RespError("ERR", "value is not an integer or out of range ");
        }
    }

    @Override
    public RespType execute(DataStore store) {
        Entry oldVal = store.get(key);
        if ((nx && oldVal != null) || (xx && oldVal == null)) {
            return new RespBulkString(null);
        }

        Entry newVal;

        if (keepTTL && oldVal != null) {
            newVal = new Entry(value, oldVal.getExpiresAt());
        } else {
            newVal = new Entry(value, expireAtMilis);
        }

        store.set(key, newVal);

        if (get) {
            if (oldVal == null)
                return new RespBulkString(null);
            if (!(oldVal.getValue() instanceof StringValue strval))
                throw new RespError("WRONGTYPE", "Operation against a key holding the wrong kind of value");
            return strval.toResp();
        }

        return new RespSimpleString("OK");
    }
}
