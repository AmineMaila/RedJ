package commands.KeyCommands;

import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespInteger;
import client.resptypes.RespType;
import commands.Command;
import store.ByteArrayKey;
import store.DataStore;

public class DelCommand extends Command {

    public DelCommand(List<RespType> args) {
        super(args);

        if (args.size() < 2) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'del' command"
            );
        }
    }

    @Override
    public RespType execute(DataStore store) {
        int deletedCount = 0;
        for (int i = 1; i < args.size(); i++) {
            ByteArrayKey key = new ByteArrayKey(((RespBulkString)args.get(i)).data());

            if (store.delete(key) != null) deletedCount++;
        }
        return new RespInteger(deletedCount);
    }
}
