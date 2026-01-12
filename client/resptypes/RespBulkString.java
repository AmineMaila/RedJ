package client.resptypes;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public record RespBulkString(byte[] data) implements RespType {

    public boolean isNull() {
        return data == null;
    }

    @Override
    public void writeTo(BufferedOutputStream out) throws IOException {
        if (data == null) {
            out.write("$-1\r\n".getBytes(StandardCharsets.US_ASCII));
            return;
        }

        out.write('$');
        out.write(Integer.toString(data.length).getBytes(StandardCharsets.US_ASCII));
        out.write(RespType.CRLF);
        out.write(data);
        out.write(RespType.CRLF);
        out.flush();
    }

    @Override
    public final String toString() {
        return new String(data, StandardCharsets.US_ASCII);
    }
}
