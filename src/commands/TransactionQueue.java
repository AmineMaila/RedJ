package commands;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import client.resptypes.RespArray;
import client.resptypes.RespType;
import store.DataStore;

public class TransactionQueue extends Command {
    private final Deque<Command> commands;
    
    public TransactionQueue(Deque<Command> commands) {
        super(null);

        this.commands = commands;
    }

    public boolean queue(Command cmd) {
        return this.commands.offer(cmd);
    }

    @Override
    public RespType execute(DataStore store) {
        List<RespType> result = new ArrayList<>(commands.size());
        while (!commands.isEmpty()) {
            Command cmd = commands.poll();
            result.add(cmd.execute(store));
        }
        return new RespArray(result);
    }
}
