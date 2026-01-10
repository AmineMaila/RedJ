package command;

import java.util.List;

import client.resptypes.RespType;

public abstract class Command {
    protected final List<RespType> args;

    public Command(List<RespType> args) {
        this.args = args;
    }

    public abstract RespType execute(CommandContext ctx);
}
