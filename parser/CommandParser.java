package parser;

import java.io.IOException;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;

import client.resptypes.RespArray;
import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespType;
import command.Command;
import command.GetCommand;
import command.SetCommand;

public class CommandParser {

    private String bulkToUpper(RespBulkString token) {
        return new String(token.data(), StandardCharsets.US_ASCII).toUpperCase();
    }

    public Command parse(RespType request) throws IOException {
        if (!(request instanceof RespArray array))
            throw new RespError("ERR", "Protocol error: expected array");

        var args = array.arr();
        if (args == null)
            throw new ProtocolException("nil resp array");

        for (RespType arg : args) {
            if (!(arg instanceof RespBulkString bulk))
                throw new RespError("ERR", "Protocol error: expected bulk string");

            if (bulk.isNull())
                throw new RespError("ERR", "Protocol error: invalid bulk length");
        }


        String cmdStr = bulkToUpper((RespBulkString) args.get(0));

        return switch (cmdStr) {
            case "GET" -> new GetCommand(args);
            case "SET" -> new SetCommand(args);
            default -> throw new RespError("ERR", "unknown command '" + cmdStr + "'");
        };
    }
}
