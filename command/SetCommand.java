package command;

import java.util.List;
import java.util.Map;

import client.Response.RespError;
import client.Response.Response;
import client.Response.SimpleStringResponse;
import store.Entry;

public class SetCommand extends Command {
    public SetCommand(List<byte[]> args) {
        super(args);
    }

    public Response execute(CommandContext ctx) {
        if (args.size() != 2) {
            return new RespError("ERR", "wrong number of arguments for 'set' command");
        }

        ctx.store().set(args.get(0).toString(), args.get(1));

        return new SimpleStringResponse("OK");
    }
}
