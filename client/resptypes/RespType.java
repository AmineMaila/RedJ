package client.resptypes;

import java.io.IOException;
import java.io.OutputStream;

public sealed interface RespType permits RespError, RespSimpleString, RespBulkString, RespInteger, RespArray {
    void writeTo(OutputStream out) throws IOException;
}
