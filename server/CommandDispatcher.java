package server;

import java.util.concurrent.LinkedBlockingQueue;

import client.resptypes.RespError;
import client.resptypes.RespType;
import command.WorkItem;

public class CommandDispatcher implements Runnable {
    private final LinkedBlockingQueue<WorkItem> cmdQueue;

    public CommandDispatcher(LinkedBlockingQueue<WorkItem> cmdQueue) {
        this.cmdQueue = cmdQueue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                WorkItem task = cmdQueue.take();
                try {
                    RespType result = task.run();

                    task.result.complete(result);
                } catch (RespError e) {
                    task.result.complete(e);
                } catch (Exception e) {
                    task.result.completeExceptionally(e);
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
}
