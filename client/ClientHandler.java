package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import client.resptypes.RespError;
import client.resptypes.RespType;
import command.CommandContext;
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

        try (BufferedInputStream in = new BufferedInputStream(clientSocket.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream())) {
            
            try {
                final Resp2Parser parser = new Resp2Parser(in);

                while (true) {
                    RespType value = parser.parse();
    
                }
            } catch (RespError re) {
                re.writeTo(out);
            }
        } catch (EOFException ee) {
            System.out.println("EOF encountered: " + ee.getMessage());
        } catch (ProtocolException pe) {
            System.out.println("Protocol error: " + pe.getMessage());
        } catch (SocketTimeoutException te) {
            System.out.println("Client Socket timeout: " + te.getMessage());
        } catch (IOException ioe) {
            System.out.println("Encountered I/O error: " + ioe.getMessage());
        }
    }
}
