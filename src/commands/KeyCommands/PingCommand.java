package commands.KeyCommands;

import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespType;
import commands.Command;
import store.DataStore;

public class PingCommand extends Command {
    private byte[] response = new byte[]{'P', 'O', 'N', 'G'};
    public PingCommand(List<RespType> args) {
        super(args);

        if (args.size() > 2) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'ping' command"
            );
        }

        if (args.size() == 2) {
            response = ((RespBulkString)args.get(1)).data();
        }
    }

    @Override
    public RespType execute(DataStore store) {
        return new RespBulkString(response);
    }
}
