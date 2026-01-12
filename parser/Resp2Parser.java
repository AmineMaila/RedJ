package parser;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import client.resptypes.RespArray;
import client.resptypes.RespBulkString;
import client.resptypes.RespError;
import client.resptypes.RespInteger;
import client.resptypes.RespSimpleString;
import client.resptypes.RespType;

public class Resp2Parser {
    private final InputStream in;
    private final static int MAX_INTEGER_LENGTH = 64;
    private final static int MAX_ELEMENTS = 1024;
    private final static int MAX_BULK = 512 * 1024;
    private final static int MAX_SIMPLE_STRING = 1024 * 1024;
    private final static int MAX_ERROR = 4096;

    public Resp2Parser(InputStream in) {
        this.in = in;
    }

    public RespType parse() throws IOException {
        int prefix = in.read();

        if (prefix == -1) {
            throw new EOFException("Stream closed");
        }

        return switch (prefix) {
            case '*' -> readArray();
            case '$' -> readBulkString();
            case ':' -> readInteger();
            case '+' -> readSimpleString();
            case '-' -> readError();
            default -> throw new ProtocolException("unknown RESP type '" + prefix + "'");
        };
    }

    public RespArray readArray() throws IOException {
        String line = new String(readLineCRLF(MAX_INTEGER_LENGTH), StandardCharsets.US_ASCII);
        try {
            int arrLen = Integer.parseInt(line);

            if (arrLen <= -1 || arrLen > MAX_ELEMENTS) throw new ProtocolException("Invalid array length '" + arrLen + "'");

            List<RespType> arr = new ArrayList<>(arrLen);
            for (int i = 0; i < arrLen; i++) {
                RespType elem = parse();

                arr.add(elem);
            }
            return new RespArray(arr);
        } catch (NumberFormatException ne) {
            throw new ProtocolException("Invalid array length '" + line + "'");
        }
    }

    public RespBulkString readBulkString() throws IOException {
        String line = new String(readLineCRLF(MAX_INTEGER_LENGTH), StandardCharsets.US_ASCII);
        try {
            int bulkStringLen = Integer.parseInt(line);

            if (bulkStringLen == -1) return new RespBulkString(null);

            if (bulkStringLen < -1 || bulkStringLen > MAX_BULK) throw new ProtocolException("Invalid bulk string length '" + bulkStringLen + "'");

            byte[] bulk = readExactly(bulkStringLen);
            expectCRLF();

            return new RespBulkString(bulk);
        } catch (NumberFormatException ne) {
            throw new ProtocolException("Invalid array length '" + line + "'");
        }
    }

    public RespInteger readInteger() throws IOException {
        String line = new String(readLineCRLF(MAX_INTEGER_LENGTH), StandardCharsets.US_ASCII);
        try {
            long value = Long.parseLong(line);
            return new RespInteger(value);
        } catch (NumberFormatException ne) {
            throw new ProtocolException("Invalid RESP integer '" + line + "'");
        }
    }

    public RespSimpleString readSimpleString() throws IOException {
        String line = new String(readLineCRLF(MAX_SIMPLE_STRING), StandardCharsets.US_ASCII);
        return new RespSimpleString(line);
    }

    public RespError readError() throws IOException {
        String line = new String(readLineCRLF(MAX_ERROR), StandardCharsets.US_ASCII);

        if (line .isEmpty() || line.charAt(0) == ' ') throw new ProtocolException("Invalid RESP error");

        int idx = line.indexOf(' ');
        if (idx == -1) {
            return new RespError(line, "");
        }
        return new RespError(line.substring(0, idx), line.substring(idx));
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

    public byte[] readExactly(int n) throws IOException {
        byte[] buf = new byte[n];
        int offset = 0;

        while (offset < n) {
            int bytesRead = in.read(buf, offset, n - offset);

            if (bytesRead == -1) {
                throw new EOFException("Unexpected EOF while reading '" + n + "' bytes");
            }

            offset += bytesRead;
        }
        return buf;
    }

    public void expectCRLF() throws IOException {
        int cr = in.read();
        int lf = in.read();

        if (cr == -1 || lf == -1) {
            throw new EOFException("Unexpected EOF while reading CRLF");
        }

        if (cr != '\r' || lf != '\n') {
            throw new ProtocolException("Invalid bulk string terminator");
        }
    }
}