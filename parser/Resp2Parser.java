package parser;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import client.resptypes.RespArray;
import client.resptypes.RespType;

public class Resp2Parser {
    private final BufferedInputStream in;
    private final static short COUNT_LIMIT = 64;

    public Resp2Parser(BufferedInputStream in) {
        this.in = in;
    }

    public RespType parse() throws IOException {
        int prefix = in.read();

        if (prefix == -1) {
            throw new EOFException("Stream closed");
        }

        return switch (prefix) {
            case '*' -> readArray();
            default -> throw new ProtocolException("unknown RESP type");
        };
    }

    public RespArray readArray() throws IOException {
        String line = new String(readLineCRLF(COUNT_LIMIT), StandardCharsets.US_ASCII);
        try {
            int n = Integer.parseInt(line);

            if (n == -1) return new RespArray(null);

            if (n < -1) throw new ProtocolException("Invalid array length '" + n + "'");
        } catch (NumberFormatException ne) {
            throw new ProtocolException("Invalid array length '" + line + "'");
        }
    }

    public byte[] readLineCRLF(int maxLineLength) throws IOException {
        byte[] buf = new byte[128];
        int len = 0;
        boolean sawCR = false;

        while (len < maxLineLength + 1) {
            int b = in.read();
            if (b == -1) 
                throw new EOFException("Unexpected EOF while reading line");
            
            if (sawCR) {
                if (b != '\n') {
                    throw new ProtocolException("CR not followed by LF");
                }
                return Arrays.copyOf(buf, len - 1);
            } else if (b == '\n') {
                throw new ProtocolException("LF without preceeding CR");
            }


            if (len == buf.length) {
                if (buf.length >= maxLineLength) {
                    throw new ProtocolException("Line too long (> " + maxLineLength + ")");
                }

                int newCap = Math.min(buf.length * 2, maxLineLength);
                buf = Arrays.copyOf(buf, newCap);
            }

            if (b =='\r') sawCR = true;

            buf[len++] = (byte) b;
        }
        throw new ProtocolException("Line too long (> " + maxLineLength + ")");
    }
}