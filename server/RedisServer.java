package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedisServer {
    private final ExecutorService clientPool = Executors.newCachedThreadPool();
    private final int port;
    private volatile boolean running = true;

    public RedisServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket socket = new ServerSocket(port)) {
            System.out.println("Mini-Redis listening on port " + this.port);

            
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;
        clientPool.shutdown();
        try {
            if(!clientPool.awaitTermination(3, TimeUnit.SECONDS))
                clientPool.shutdownNow();
        } catch (InterruptedException e) {
            clientPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}