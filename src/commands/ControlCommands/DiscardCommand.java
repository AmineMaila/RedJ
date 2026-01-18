package commands.ControlCommands;

import java.util.List;

import client.resptypes.RespError;
import client.resptypes.RespType;
import commands.Command;
import store.DataStore;

public class DiscardCommand extends Command implements ControlCommand {

    public DiscardCommand(List<RespType> args) {
        super(args);
        if (args.size() > 1) {
            throw new RespError("ERR", "wrong number of arguments for 'discard' command");
        }
    }


    @Override
    public RespType execute(DataStore store) {
        throw new UnsupportedOperationException(
            "Discard must be handled in ClientHandler"
        );
    }
}