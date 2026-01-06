package client.resptypes;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public record RespInteger(long value) implements RespType {
    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(':');
        out.write(Long.toString(value).getBytes(StandardCharsets.US_ASCII));
        out.write("\r\n".getBytes(StandardCharsets.US_ASCII));
    }
}
