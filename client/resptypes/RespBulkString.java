package client.resptypes;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public record RespBulkString(byte[] data) implements RespType {

    public boolean isNull() {
        return data == null;
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        if (data == null) {
            out.write("$-1\r\n".getBytes(StandardCharsets.US_ASCII));
            return;
        }

        byte[] payload = ('$' + Integer.toString(data.length) + "\r\n" + data + "\r\n").getBytes(StandardCharsets.US_ASCII);

        out.write(payload);
    }

    @Override
    public final String toString() {
        return new String(data, StandardCharsets.US_ASCII);
    }
}
