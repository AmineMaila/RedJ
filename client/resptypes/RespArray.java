package client.resptypes;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public record RespArray(List<RespType> arr) implements RespType{

    @Override
    public void writeTo(OutputStream out) throws IOException {
        byte[] payload = ('*' + Integer.toString(arr.size()) + "\r\n").getBytes(StandardCharsets.US_ASCII);
        out.write(payload);
        for (RespType res : arr) {
            res.writeTo(out);
        }
    }
}
