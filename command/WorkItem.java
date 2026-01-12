package command;

import java.util.concurrent.CompletableFuture;

import client.resptypes.RespType;

public class WorkItem {
    private final Command command;
    private final CommandContext ctx;
    public final CompletableFuture<RespType> result = new CompletableFuture<>();

    public WorkItem(Command command, CommandContext ctx) {
        this.command = command;
        this.ctx = ctx;
    }

    public RespType run() {
        return this.command.execute(ctx);
    }

    public Command getCommand() {
        return command;
    }
}
