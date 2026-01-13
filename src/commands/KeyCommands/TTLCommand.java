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

public class TTLCommand extends Command {
    private final ByteArrayKey key;
    public TTLCommand(List<RespType> args) {
        super(args);

        if (args.size() != 2) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'del' command"
            );
        }

        key = new ByteArrayKey(((RespBulkString)args.get(1)).data());
    }

    @Override
    public RespType execute(DataStore store) {
        Entry item = store.get(key);
        if (item == null) return new RespInteger(-2);

        Long expiresAt = item.getExpiresAt();
        if (expiresAt == null) return new RespInteger(-1);

        return new RespInteger((long)Math.ceil((expiresAt - System.currentTimeMillis()) / 1000));
    }
}
