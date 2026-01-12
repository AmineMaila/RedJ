package command;

import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespType;
import client.resptypes.RespSimpleString;
import store.Entry;
import store.datatypes.StringValue;

public class SetCommand extends Command {
    private final byte[] key;
    private final Entry value;
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

        key = ((RespBulkString) args.get(1)).data();
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

        value = new Entry(new StringValue(rawValue), expireAtMilis);
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
    public RespType execute(CommandContext ctx) {
        Entry old = ctx.store().get(key);
        if ((nx && old != null) || (xx && old == null)) {
            return new RespBulkString(null);
        }

        if (keepTTL && old != null) {
            value.setExpiresAt(old.getExpiresAt());
        }

        ctx.store().set(key, value);

        if (get) {
            if (old == null)
                return new RespBulkString(null);
            if (!(old.value().toResp() instanceof RespBulkString respOld))
                throw new RespError("WRONGTYPE", "Operation against a key holding the wrong kind of value");
            return respOld;
        }

        return new RespSimpleString("OK");
    }
}
