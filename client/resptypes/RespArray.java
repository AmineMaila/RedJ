package client.resptypes;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public record RespArray(List<RespType> arr) implements RespType{

    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write('*');
        out.write(Integer.toString(arr.size()).getBytes(StandardCharsets.US_ASCII));
        out.write("\r\n".getBytes(StandardCharsets.US_ASCII));
        for (RespType res : arr) {
            res.writeTo(out);
        }
    }
}
