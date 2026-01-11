package client.resptypes;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class RespError extends RuntimeException implements RespType {
    private final String errorType;

    public RespError(String errorType, String message) {
        super(message);

        this.errorType = errorType;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        byte[] payload = ('-' + errorType + ' ' + getMessage() + "\r\n").getBytes(StandardCharsets.US_ASCII);
        out.write(payload);
    }
}
