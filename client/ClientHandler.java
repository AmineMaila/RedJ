package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import client.resptypes.RespError;
import client.resptypes.RespType;
import command.Command;
import command.CommandContext;
import parser.CommandParser;
import parser.Resp2Parser;
import store.DataStore;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final DataStore store;

    public ClientHandler(Socket socket, DataStore store) {
        this.clientSocket = socket;
        this.store = store;
    }
    
    @Override
    public void run() {
        CommandContext ctx = new CommandContext(this.store);


        try (InputStream in = clientSocket.getInputStream(); OutputStream out = clientSocket.getOutputStream()) {
            System.out.println("parsing...");
            final Resp2Parser respParser = new Resp2Parser(in);
            final CommandParser cmdParser = new CommandParser();
            
                while (true) {
                    try {
                        RespType request = respParser.parse();
                        System.out.println(request);
                        Command cmd = cmdParser.parse(request);
                        RespType response = cmd.execute(ctx);
                        response.writeTo(out);
                    } catch (RespError re) {
                        re.writeTo(out);
                    }
                }
        } catch (EOFException ee) {
            System.out.println("EOF encountered: " + ee.getMessage());
        } catch (ProtocolException pe) {
            System.out.println("Protocol error: " + pe.getMessage());
        } catch (SocketTimeoutException te) {
            System.out.println("Client Socket timeout: " + te.getMessage());
        } catch (IOException ioe) {
            System.out.println("Encountered I/O error: " + ioe.getMessage());
        } finally {
            try {
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
