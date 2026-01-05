package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import client.resptypes.RespType;
import command.Command;
import command.CommandContext;
import parser.Resp2Parser;
import store.DataStore;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final DataStore store;
    private final static int BUFFER_SIZE = 8192;

    public ClientHandler(Socket socket, DataStore store) {
        this.clientSocket = socket;
        this.store = store;
    }
    
    @Override
    public void run() {
        CommandContext ctx = new CommandContext(this.store);
        byte[] buf = new byte[BUFFER_SIZE];
        int bytesRead;
        try (BufferedInputStream in = new BufferedInputStream(clientSocket.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream())) {

            final Resp2Parser parser = new Resp2Parser(in);
            while (true) {
                RespType value = parser.parse();

            }
        } catch (EOFException e) {
            System.out.println("Client " + this.clientSocket + " Disconnected");
        } catch (IOException e) {
            System.out.println("Encountered I/O error: " + e.getMessage());
        } finally {
            this.clientSocket.close();
        }
    }
}
