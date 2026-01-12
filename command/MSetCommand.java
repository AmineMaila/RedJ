package command;

import java.util.List;

import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespSimpleString;
import client.resptypes.RespType;
import store.ByteArrayKey;
import store.DataStore;
import store.Entry;
import store.datatypes.StringValue;

public class MSetCommand extends Command {

    public MSetCommand(List<RespType> args) {
        super(args);
        int size = args.size();
        if (size <= 1 || size % 2 == 0) {
            throw new RespError("ERR", "wrong number of arguments for 'mset' command");
        }
    }

    @Override
    public RespType execute(DataStore store) {
        for (int i = 1; i < args.size(); i+=2) {
            ByteArrayKey key = new ByteArrayKey(((RespBulkString) args.get(i)).data());
            Entry value = new Entry(new StringValue(((RespBulkString)args.get(i + 1)).data()));
            store.set(key, value);
        }
        return new RespSimpleString("OK");
    }
}