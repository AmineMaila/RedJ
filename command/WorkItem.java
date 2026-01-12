package command;

import java.util.concurrent.CompletableFuture;

import client.resptypes.RespType;

public class WorkItem {
    public final Command command;
    public final CompletableFuture<RespType> result = new CompletableFuture<>();

    public WorkItem(Command command) {
        this.command = command;
    }
}
