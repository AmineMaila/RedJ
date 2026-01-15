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

public class ExistsCommand extends Command {

    public ExistsCommand(List<RespType> args) {
        super(args);

        if (args.size() < 2) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'exists' command"
            );
        }
    }

    @Override
    public RespType execute(DataStore store) {
        long existCount = 0;
        for (int i = 1; i < args.size(); i++) {
            ByteArrayKey key = new ByteArrayKey(((RespBulkString)args.get(i)).data());
            Entry item = store.get(key);
            if (item != null)
                existCount++;
        }

        return new RespInteger(existCount);
    }
}
