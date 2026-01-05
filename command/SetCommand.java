package command;

import java.nio.charset.StandardCharsets;
import java.util.List;

import client.resptypes.RespError;
import client.resptypes.RespType;
import client.resptypes.RespSimpleString;
import store.Entry;
import store.datatypes.StringValue;

public class SetCommand extends Command {
    public SetCommand(List<byte[]> args) {
        super(args);
    }

    public RespType execute(CommandContext ctx) {
        if (args.size() < 2) {
            return new RespError("ERR", "wrong number of arguments for 'set' command");
        }

        StringValue val = new StringValue(args.get(1));
        Entry el = new Entry(val);
        String key = new String(args.get(0), StandardCharsets.UTF_8);

        ctx.store().set(key, el);

        return new RespSimpleString("OK");
    }
}
