package commands.KeyCommands;

import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespInteger;
import client.resptypes.RespType;
import commands.Command;
import store.ByteArrayKey;
import store.DataStore;
import store.Entry;

public class ExpireCommand extends Command {
    private final ByteArrayKey key;
    private final Long seconds;
    private boolean xx;
    private boolean nx;
    private boolean gt;
    private boolean lt;

    public ExpireCommand(List<RespType> args) {
        super(args);
        if (args.size() < 3) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'expire' command"
            );
        }

        for (int i = 3; i < args.size(); i++) {
            String opt = args.get(i).toString().toUpperCase();

            switch(opt) {
                case "XX" -> xx = true;
                case "NX" -> nx = true;
                case "GT" -> gt = true;
                case "LT" -> lt = true;
                default -> throw new RespError("ERR", "syntax error");
            }
        }
        int exclusiveTrue = (xx ? 1 : 0) + (nx ? 1 : 0) + (gt ? 1 : 0) + (lt ? 1 : 0);

        if (exclusiveTrue > 1) {
            throw new RespError("ERR", "wrong number of arguments for 'expire' command");
        }

        this.key = new ByteArrayKey(((RespBulkString)args.get(1)).data());
        this.seconds = parseLongArg((RespBulkString)args.get(2));
    }

    private long parseLongArg(RespBulkString number) {
        try {
            return Long.parseLong(number.toString());
        } catch (NumberFormatException ne) {
            throw new RespError("ERR", "value is not an integer or out of range ");
        }
    }

    @Override
    public RespType execute(DataStore store) {
        if (seconds <= 0) {
            return new RespInteger(store.delete(key) == null ? 0 : 1);
        }

        Entry item = store.get(key);
        if (item == null) {
            return new RespInteger(0);
        }
        
        Long currentExpiresMs = item.getExpiresAt();
        Long currentExpiresSec = currentExpiresMs == null ? null : currentExpiresMs / 1000;
        
        if (nx && item.getExpiresAt() != null) {
            return new RespInteger(0);
        }

        if (xx && item.getExpiresAt() == null) {
            return new RespInteger(0);
        }

        if (gt && (currentExpiresSec == null || currentExpiresSec <= seconds)) {
            return new RespInteger(0);
        }

        if (lt && (currentExpiresSec == null || currentExpiresSec >= seconds)) {
            return new RespInteger(0);
        }

        item.setExpiresAt((seconds * 1000) + System.currentTimeMillis());

        return new RespInteger(1);
    }
    
}
