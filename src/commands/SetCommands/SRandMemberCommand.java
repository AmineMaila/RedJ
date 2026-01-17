package commands.SetCommands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import client.resptypes.RespArray;
import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespType;
import commands.Command;
import store.ByteArrayKey;
import store.DataStore;
import store.Entry;
import store.datatypes.SetValue;

public class SRandMemberCommand extends Command {

    private final ByteArrayKey key;
    private final int count;

    public SRandMemberCommand(List<RespType> args) {
        super(args);

        if (args.size() < 2 || args.size() > 3) {
            throw new RespError(
                "ERR",
                "wrong number of arguments for 'srandmember' command"
            );
        }

        this.key = new ByteArrayKey(((RespBulkString) args.get(1)).data());

        if (args.size() == 3) {
            try {
                this.count = Integer.parseInt(
                    new String(((RespBulkString) args.get(2)).data())
                );
            } catch (NumberFormatException e) {
                throw new RespError("ERR", "count must be an integer");
            }
        } else {
            this.count = 1;
        }
    }

    @Override
    public RespType execute(DataStore store) {
        Entry entry = store.get(key);

        if (entry == null) {
            if (count == 1) return new RespBulkString(null);
            return new RespArray(List.of());
        }

        if (!(entry.getValue() instanceof SetValue set)) {
            throw new RespError(
                "WRONGTYPE",
                "Operation against a key holding the wrong kind of value"
            );
        }

        List<ByteArrayKey> list = new ArrayList<>(set.members());
        Collections.shuffle(list);

        if (count == 1) return list.isEmpty() ? new RespBulkString(null) : new RespBulkString(list.get(0).bytes());

        int toReturn = Math.min(count, list.size());
        List<RespType> result = new ArrayList<>();
        for (int i = 0; i < toReturn; i++) {
            result.add(new RespBulkString(list.get(i).bytes()));
        }

        return new RespArray(result);
    }
}
