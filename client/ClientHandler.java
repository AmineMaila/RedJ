package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

import command.Command;
import parser.Resp2Parser;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final static int BUFFER_SIZE = 8192;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }
    
    @Override
    public void run() {
        byte[] buf = new byte[BUFFER_SIZE];
        int bytesRead;
        try (BufferedInputStream in = new BufferedInputStream(clientSocket.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream())) {

            final Resp2Parser parser = new Resp2Parser(in);
            while (true) {
                Command commands = parser.parse();

                // for (var cmd : commands) {

                // }
            }
        } catch (EOFException e) {
            System.out.println("Client " + this.clientSocket + " Disconnected");
        } catch (IOException e) {
            System.out.println("Connection lost: " + e.getMessage());
        } finally {
            this.clientSocket.close();
        }
    }
}
