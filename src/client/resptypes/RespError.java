package client.resptypes;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class RespError extends RuntimeException implements RespType {
    private final String errorType;

    public RespError(String errorType, String message) {
        super(message);

        this.errorType = errorType;
    }

    @Override
    public void writeTo(BufferedOutputStream out) throws IOException {
        out.write('-');
        out.write(errorType.getBytes(StandardCharsets.US_ASCII));
        out.write(' ');
        out.write(getMessage().getBytes(StandardCharsets.US_ASCII));
        out.write(RespType.CRLF);
        out.flush();
    }
}
