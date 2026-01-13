package commands.StringCommands;

import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespType;
import commands.Command;
import store.ByteArrayKey;
import store.DataStore;
import store.Entry;

public class GetCommand extends Command {
    private final ByteArrayKey key;

    public GetCommand(List<RespType> args) {
        super(args);
        if (args.size() != 2) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'get' command"
            );
        }
        this.key = new ByteArrayKey(((RespBulkString) args.get(1)).data());
    }

    @Override
    public RespType execute(DataStore store) {
        Entry entry = store.get(key);

        if (entry == null) {
            return new RespBulkString(null);
        }
        
        if (entry.isExpired()) {
            store.delete(key);
            return new RespBulkString(null);
        }

        if (!(entry.getValue().toResp() instanceof RespBulkString respVal))
            throw new RespError("WRONGTYPE", "Operation against a key holding the wrong kind of value");

        return respVal;
    }
}
