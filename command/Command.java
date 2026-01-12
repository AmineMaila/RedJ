package command;

import java.util.List;

import client.resptypes.RespType;
import store.DataStore;

public abstract class Command {
    protected final List<RespType> args;

    public Command(List<RespType> args) {
        this.args = args;
    }

    public abstract RespType execute(DataStore store);
}
