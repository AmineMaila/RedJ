package command;

import java.nio.charset.StandardCharsets;
import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespType;
import client.resptypes.RespSimpleString;
import store.Entry;
import store.datatypes.StringValue;

public class SetCommand extends Command {
    private final byte[] key;
    private final Entry value;

    public SetCommand(List<RespType> args) {
        super(args);

        if (args.size() != 3) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'set' command"
            );
        }

        this.key = ((RespBulkString) args.get(1)).data();
        byte[] rawValue = ((RespBulkString) args.get(2)).data();
        this.value = (rawValue == null) ? new Entry(null) : new Entry(new StringValue(rawValue));
    }

    @Override
    public RespType execute(CommandContext ctx) {
        ctx.store().set(key, value);
        return new RespSimpleString("OK");
    }
}
