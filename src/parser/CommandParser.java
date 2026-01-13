package parser;

import java.io.IOException;
import java.net.ProtocolException;

import client.resptypes.RespArray;
import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespType;
import commands.Command;
import commands.KeyCommands.DelCommand;
import commands.KeyCommands.ExpireCommand;
import commands.StringCommands.GetCommand;
import commands.StringCommands.IncrByCommand;
import commands.StringCommands.IncrCommand;
import commands.StringCommands.MSetCommand;
import commands.StringCommands.SetCommand;

public class CommandParser {

    private String bulkToUpper(RespBulkString token) {
        return token.toString().toUpperCase();
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

        // at this stage commands expect a List of RespBulkString
        return switch (cmdStr) {
            // key commands
            case "DEL" -> new DelCommand(args);
            case "EXPIRE" -> new ExpireCommand(args);
            // String commands
            case "GET" -> new GetCommand(args);
            case "SET" -> new SetCommand(args);
            case "MSET" -> new MSetCommand(args);
            case "APPEND" -> new ExpireCommand(args);
            case "INCR" -> new IncrCommand(args);
            case "INCRBY" -> new IncrByCommand(args);
            default -> throw new RespError("ERR", "unknown command '" + cmdStr + "'");
        };
    }
}
