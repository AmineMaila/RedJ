package client.resptypes;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public sealed interface RespType permits RespError, RespSimpleString, RespBulkString, RespInteger, RespArray {
    public final static byte[] CRLF = "\r\n".getBytes(StandardCharsets.US_ASCII);
    void writeTo(OutputStream out) throws IOException;
}
