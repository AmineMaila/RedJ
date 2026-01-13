package client.resptypes;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public record RespSimpleString(String content) implements RespType {
    @Override
    public void writeTo(BufferedOutputStream out) throws IOException {
        out.write('+');
        out.write(content.getBytes(StandardCharsets.US_ASCII));
        out.write(RespType.CRLF);
        out.flush();
    }
}
