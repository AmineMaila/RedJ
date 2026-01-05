package command;

import java.util.List;

import client.resptypes.RespType;

public abstract class Command {
    protected final List<byte[]> args;

    public Command(List<byte[]> args) {
        this.args = args;
    }

    public abstract RespType execute(CommandContext ctx);
}
