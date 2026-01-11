package client.resptypes;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public record RespSimpleString(String content) implements RespType {
    @Override
    public void writeTo(OutputStream out) throws IOException {
        byte[] payload = ('+' + content + "\r\n").getBytes(StandardCharsets.US_ASCII);
        out.write(payload);
    }
}
