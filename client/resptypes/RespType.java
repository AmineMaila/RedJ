package client.resptypes;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public sealed interface RespType permits RespError, RespSimpleString, RespBulkString, RespInteger, RespArray {
    public static final byte[] CRLF = "\r\n".getBytes(StandardCharsets.US_ASCII);
    void writeTo(BufferedOutputStream out) throws IOException;
}
