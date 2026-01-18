package commands.ControlCommands;

import java.util.List;

import client.resptypes.RespError;
import client.resptypes.RespType;
import commands.Command;
import store.DataStore;

public class ExecCommand extends Command implements ControlCommand {

    public ExecCommand(List<RespType> args) {
        super(args);
        if (args.size() > 1) {
            throw new RespError("ERR", "wrong number of arguments for 'exec' command");
        }
    }


    @Override
    public RespType execute(DataStore store) {
        throw new UnsupportedOperationException(
            "EXEC must be handled in ClientHandler"
        );
    }
}
