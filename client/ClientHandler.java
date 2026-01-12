package client;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

import client.resptypes.RespError;
import client.resptypes.RespType;
import command.Command;
import command.WorkItem;
import parser.CommandParser;
import parser.Resp2Parser;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final LinkedBlockingQueue<WorkItem> cmdQueue;

    public ClientHandler(Socket socket, LinkedBlockingQueue<WorkItem> cmdQueue) {
        this.clientSocket = socket;
        this.cmdQueue = cmdQueue;
    }
    
    @Override
    public void run() {

        try (InputStream in = clientSocket.getInputStream(); OutputStream out = clientSocket.getOutputStream()) {
            System.out.println("parsing...");
            final Resp2Parser respParser = new Resp2Parser(in);
            final CommandParser cmdParser = new CommandParser();
            
            while (true) {
                try {
                    RespType request = respParser.parse();
                    System.out.println(request);
                    Command cmd = cmdParser.parse(request);
                    WorkItem task = new WorkItem(cmd);
                    cmdQueue.put(task);
                    RespType response = task.result.get();
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            cause.printStackTrace();
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
