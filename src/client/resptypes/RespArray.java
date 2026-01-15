package client.resptypes;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public record RespArray(List<RespType> arr) implements RespType{

    @Override
    public void writeTo(BufferedOutputStream out) throws IOException {
        if (arr == null) {
            out.write("*-1\r\n".getBytes(StandardCharsets.US_ASCII));
            out.flush();
            return;
        }
        out.write('*');
        out.write(Integer.toString(arr.size()).getBytes(StandardCharsets.US_ASCII));
        out.write(RespType.CRLF);
        for (RespType res : arr) {
            res.writeTo(out);
        }
        out.flush();
    }
}
