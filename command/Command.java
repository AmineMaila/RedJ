package command;

import java.util.List;

import client.Response.Response;

public abstract class Command {
    protected final List<byte[]> args;

    public Command(List<byte[]> args) {
        this.args = args;
    }

    public abstract Response execute(CommandContext ctx);
}
