package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import client.ClientHandler;
import store.DataStore;

public class RedisServer {
    private final ExecutorService clientPool = Executors.newCachedThreadPool();
    private final DataStore store = new DataStore();
    private final int port;
    private volatile boolean running = true;

    public RedisServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Mini-Redis listening on port " + this.port);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Stoping server...");
                stop();
            }));


            while(running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientPool.execute(new ClientHandler(clientSocket, this.store));
                } catch (SocketException se) {
                    if (running) {
                        System.out.println("SocketException in accept(): " + se.getMessage());
                    }
                } catch (IOException ioe) {
                        System.out.println("I/O Error accepting connection: " + ioe.getMessage());
                }
            }
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