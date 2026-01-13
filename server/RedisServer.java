package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import client.ClientHandler;

public class RedisServer {
    private final ExecutorService clientPool = Executors.newCachedThreadPool();
    private final LinkedBlockingQueue<WorkItem> cmdQueue = new LinkedBlockingQueue<>();
    private final int TIMEOUT = 60000;
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

            ExecutorService consumerExecutor = Executors.newSingleThreadExecutor();
            consumerExecutor.execute(new CommandDispatcher(cmdQueue));

            while(running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client '" + clientSocket + "' connected");
                    clientSocket.setSoTimeout(TIMEOUT); // timeout on hanging read call
                    clientPool.execute(new ClientHandler(clientSocket, this.cmdQueue));
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