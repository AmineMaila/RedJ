package commands.HashCommands;

import java.util.ArrayList;
import java.util.List;

import client.resptypes.RespArray;
import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespType;
import commands.Command;
import store.ByteArrayKey;
import store.DataStore;
import store.Entry;
import store.datatypes.HashValue;

public class HMGetCommand extends Command {

    private final ByteArrayKey key;

    public HMGetCommand(List<RespType> args) {
        super(args);

        if (args.size() < 3) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'hmget' command"
            );
        }

        this.key = new ByteArrayKey(((RespBulkString) args.get(1)).data());
    }

    @Override
    public RespType execute(DataStore store) {
        Entry entry = store.get(key);

        List<RespType> result = new ArrayList<>(args.size() - 2);

        if (entry == null) {
            // key missing → all nils
            for (int i = 2; i < args.size(); i++) {
                result.add(new RespBulkString(null));
            }
            return new RespArray(result);
        }

        if (!(entry.getValue() instanceof HashValue hash)) {
            throw new RespError(
                "WRONGTYPE",
                "Operation against a key holding the wrong kind of value"
            );
        }

        for (int i = 2; i < args.size(); i++) {
            byte[] hkey = ((RespBulkString) args.get(i)).data();
            result.add(new RespBulkString(hash.hget(hkey)));
        }

        return new RespArray(result);
    }
}

