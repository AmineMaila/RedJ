package server;

import java.util.concurrent.CompletableFuture;

import client.resptypes.RespType;
import commands.Command;

public class WorkItem {
    public final Command command;
    public final CompletableFuture<RespType> result = new CompletableFuture<>();

    public WorkItem(Command command) {
        this.command = command;
    }
}
