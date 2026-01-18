package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

import client.resptypes.RespError;
import client.resptypes.RespSimpleString;
import client.resptypes.RespType;
import commands.Command;
import commands.TransactionQueue;
import commands.ControlCommands.DiscardCommand;
import commands.ControlCommands.ExecCommand;
import commands.ControlCommands.MultiCommand;
import parser.CommandParser;
import parser.Resp2Parser;
import server.WorkItem;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final LinkedBlockingQueue<WorkItem> cmdQueue;
    private boolean txHasSyntaxError = false;
    private boolean inTransaction = false;
    private Deque<Command> txQueue = null;


    public ClientHandler(Socket socket, LinkedBlockingQueue<WorkItem> cmdQueue) {
        this.clientSocket = socket;
        this.cmdQueue = cmdQueue;
    }

    @Override
    public void run() {

        try (BufferedInputStream in = new BufferedInputStream(clientSocket.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream())) {
            final Resp2Parser respParser = new Resp2Parser(in);
            final CommandParser cmdParser = new CommandParser();
            
            while (true) {
                try {
                    RespType request = respParser.parse();
                    System.out.println("Client Request: " + request);
                    Command cmd = cmdParser.parse(request);
                    if (cmd instanceof MultiCommand) {
                        if (inTransaction) {
                            throw new RespError("ERR", "MULTI calls can not be nested");
                        }
                        txQueue = new ArrayDeque<Command>();
                        inTransaction = true;
                        new RespSimpleString("OK")
                            .writeTo(out);
                        continue;
                    } else if (cmd instanceof ExecCommand) {
                        if (!inTransaction) {
                            throw new RespError("ERR", "EXEC without MULTI");
                        }

                        if (txHasSyntaxError) {
                            txHasSyntaxError = false;
                            inTransaction = false;
                            txQueue = null;
                            throw new RespError("EXECABORT", "Transaction discarded because of previous errors.");
                        }

                        inTransaction = false;
                        txHasSyntaxError = false;
                        cmd = new TransactionQueue(txQueue);
                        txQueue = null;
                    } else if (cmd instanceof DiscardCommand) {
                        if (!inTransaction) {
                            throw new RespError("ERR", "DISCARD without MULTI");
                        }
                        inTransaction = false;
                        txHasSyntaxError = false;
                        txQueue = null;
                        new RespSimpleString("OK")
                            .writeTo(out);
                        continue;
                    } else if (inTransaction) {
                        this.txQueue.offer(cmd);
                        new RespSimpleString("QUEUED")
                            .writeTo(out);
                        continue;
                    }
                    WorkItem task = new WorkItem(cmd);
                    cmdQueue.put(task);
                    RespType response = task.result.get();
                    response.writeTo(out);
                } catch (RespError re) {
                    if (inTransaction) {
                        txHasSyntaxError = true;
                    }
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
