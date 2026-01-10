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

        out.write('$');
        out.write(Integer.toString(data.length).getBytes(StandardCharsets.US_ASCII));
        out.write("\r\n".getBytes(StandardCharsets.US_ASCII));
        out.write(data);
        out.write("\r\n".getBytes(StandardCharsets.US_ASCII));
    }

    
}
