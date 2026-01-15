import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * small Redis CLI that supports RESP Array input and parses RESP replies.
 *
 * Usage:
 *   javac RedisCli.java
 *   java RedisCli [-h host] [-p port] [-v]
 *
 * Examples:
 *   java RedisCli
 *   java RedisCli -h 127.0.0.1 -p 6380 -v
 */
public class RedisCli {
    private final String host;
    private final int port;
    private final boolean verbose;

    public RedisCli(String host, int port, boolean verbose) {
        this.host = host;
        this.port = port;
        this.verbose = verbose;
    }

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 5353;
        boolean verbose = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                case "--host":
                    host = args[++i];
                    break;
                case "-p":
                case "--port":
                    port = Integer.parseInt(args[++i]);
                    break;
                case "-v":
                case "--verbose":
                    verbose = true;
                    break;
                case "-?":
                case "--help":
                    printHelpAndExit();
                    break;
                default:
                    System.err.println("Unknown arg: " + args[i]);
                    printHelpAndExit();
            }
        }

        RedisCli cli = new RedisCli(host, port, verbose);
        cli.run();
    }

    private static void printHelpAndExit() {
        System.out.println("Usage: java RedisCli [-h host] [-p port] [-v]");
        System.out.println("  -h, --host     Redis host (default localhost)");
        System.out.println("  -p, --port     Redis port (default 6379)");
        System.out.println("  -v, --verbose  Show raw response bytes (escaped) and parsed output");
        System.exit(0);
    }

    private void run() {
        System.out.println("Connecting to " + host + ":" + port + "  (type QUIT or EXIT to quit)");
        try (Socket socket = new Socket(host, port)) {
            socket.setTcpNoDelay(true);
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            String line;
            while (true) {
                System.out.print("> ");
                line = console.readLine();
                if (line == null) { // EOF (Ctrl+D)
                    System.out.println();
                    break;
                }
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) break;

                List<String> args = tokenize(line);
                byte[] req = buildRespArray(args);
                out.write(req);
                out.flush();

                // Parse response while capturing bytes read
                ByteArrayOutputStream captured = new ByteArrayOutputStream();
                RespType resp = parseResp(in, captured);

                if (verbose) {
                    System.out.println("Raw bytes:");
                    System.out.println(escapeBytes(captured.toByteArray()));
                    System.out.println("Parsed:");
                }
                printResp(resp, "");
            }

        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }

    // ---------------- RESP building ----------------

    private static byte[] buildRespArray(List<String> parts) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            String header = "*" + parts.size() + "\r\n";
            out.write(header.getBytes(StandardCharsets.UTF_8));
            for (String s : parts) {
                byte[] b = s.getBytes(StandardCharsets.UTF_8);
                out.write(("$" + b.length + "\r\n").getBytes(StandardCharsets.UTF_8));
                out.write(b);
                out.write("\r\n".getBytes(StandardCharsets.UTF_8));
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // ---------------- Tokenizer (supports quoting and simple backslash escapes) ----------------

    private static List<String> tokenize(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inSingle = false;
        boolean inDouble = false;
        boolean escape = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (escape) {
                // simple escape processing: \n, \r, \t, \\, \", \', \xHH
                if (c == 'n') cur.append('\n');
                else if (c == 'r') cur.append('\r');
                else if (c == 't') cur.append('\t');
                else if (c == '\\') cur.append('\\');
                else if (c == '"') cur.append('"');
                else if (c == '\'') cur.append('\'');
                else if (c == 'x' && i + 2 < line.length()) {
                    String hex = line.substring(i + 1, i + 3);
                    try {
                        int v = Integer.parseInt(hex, 16);
                        cur.append((char) v);
                        i += 2;
                    } catch (NumberFormatException ex) {
                        cur.append('x'); // fallback
                    }
                } else {
                    cur.append(c);
                }
                escape = false;
                continue;
            }
            if (c == '\\') {
                escape = true;
                continue;
            }
            if (c == '\'' && !inDouble) {
                inSingle = !inSingle;
                continue;
            }
            if (c == '"' && !inSingle) {
                inDouble = !inDouble;
                continue;
            }
            if (Character.isWhitespace(c) && !inSingle && !inDouble) {
                if (cur.length() > 0) {
                    parts.add(cur.toString());
                    cur.setLength(0);
                }
            } else {
                cur.append(c);
            }
        }
        if (cur.length() > 0) parts.add(cur.toString());
        return parts;
    }

    // ---------------- RESP parsing ----------------

    private static abstract class RespType { }

    private static class RespSimpleString extends RespType {
        final String s;
        RespSimpleString(String s) { this.s = s; }
    }

    private static class RespError extends RespType {
        final String s;
        RespError(String s) { this.s = s; }
    }

    private static class RespInteger extends RespType {
        final long value;
        RespInteger(long v) { this.value = v; }
    }

    private static class RespBulkString extends RespType {
        final byte[] bytes; // null => nil bulk string
        RespBulkString(byte[] bytes) { this.bytes = bytes; }
    }

    private static class RespArray extends RespType {
        final RespType[] items; // null => nil array
        RespArray(RespType[] items) { this.items = items; }
    }

    /**
     * parse one RESP object from InputStream, capturing all bytes read into capturedOut.
     * this blocks until the full RESP item is read (or the server closes).
     */
    private static RespType parseResp(InputStream in, ByteArrayOutputStream capturedOut) throws IOException {
        int first = readByte(in, capturedOut);
        if (first == -1) throw new EOFException("Server closed connection");
        char t = (char) first;
        switch (t) {
            case '+':
                String simple = readLineCRLF(in, capturedOut);
                return new RespSimpleString(simple);
            case '-':
                String err = readLineCRLF(in, capturedOut);
                return new RespError(err);
            case ':':
                String num = readLineCRLF(in, capturedOut);
                long val;
                try { val = Long.parseLong(num); }
                catch (NumberFormatException e) { throw new IOException("Invalid integer reply: " + num); }
                return new RespInteger(val);
            case '$':
                String lenStr = readLineCRLF(in, capturedOut);
                int len = Integer.parseInt(lenStr);
                if (len == -1) return new RespBulkString(null); // nil
                byte[] data = readFixedBytes(in, capturedOut, len);
                // consume trailing CRLF
                expectCRLF(in, capturedOut);
                return new RespBulkString(data);
            case '*':
                String cntStr = readLineCRLF(in, capturedOut);
                int cnt = Integer.parseInt(cntStr);
                if (cnt == -1) return new RespArray(null); // nil array
                RespType[] items = new RespType[cnt];
                for (int i = 0; i < cnt; i++) {
                    items[i] = parseResp(in, capturedOut);
                }
                return new RespArray(items);
            default:
                throw new IOException("Unknown RESP type byte: " + t);
        }
    }

    private static int readByte(InputStream in, ByteArrayOutputStream captured) throws IOException {
        int b = in.read();
        if (b != -1) captured.write(b);
        return b;
    }

    private static String readLineCRLF(InputStream in, ByteArrayOutputStream captured) throws IOException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        int prev = -1;
        while (true) {
            int b = readByte(in, captured);
            if (b == -1) throw new EOFException("Unexpected EOF while reading line");
            if (prev == '\r' && b == '\n') {
                byte[] arr = tmp.toByteArray();
                // remove final '\r' from arr
                String s = new String(arr, 0, arr.length - 1, StandardCharsets.UTF_8);
                return s;
            }
            tmp.write(b);
            prev = b;
        }
    }

    private static byte[] readFixedBytes(InputStream in, ByteArrayOutputStream captured, int len) throws IOException {
        byte[] buf = new byte[len];
        int read = 0;
        while (read < len) {
            int r = in.read(buf, read, len - read);
            if (r == -1) throw new EOFException("Unexpected EOF reading bulk string");
            captured.write(buf, read, r);
            read += r;
        }
        return buf;
    }

    private static void expectCRLF(InputStream in, ByteArrayOutputStream captured) throws IOException {
        int r1 = readByte(in, captured);
        int r2 = readByte(in, captured);
        if (r1 != '\r' || r2 != '\n') {
            throw new IOException("Expected CRLF after bulk string");
        }
    }

    // ---------------- Printing helpers ----------------

    private static void printResp(RespType r, String indent) {
        if (r instanceof RespSimpleString) {
            System.out.println(((RespSimpleString) r).s);
        } else if (r instanceof RespError) {
            System.out.println("(error) " + ((RespError) r).s);
        } else if (r instanceof RespInteger) {
            System.out.println("(integer) " + ((RespInteger) r).value);
        } else if (r instanceof RespBulkString) {
            RespBulkString b = (RespBulkString) r;
            if (b.bytes == null) {
                System.out.println("(nil)");
            } else {
                String s = new String(b.bytes, StandardCharsets.UTF_8);
                System.out.println(s);
            }
        } else if (r instanceof RespArray) {
            RespArray a = (RespArray) r;
            if (a.items == null) {
                System.out.println("(nil)");
            } else if (a.items.length == 0) {
                System.out.println("(empty array)");
            } else {
                for (int i = 0; i < a.items.length; i++) {
                    System.out.print((i + 1) + ") ");
                    // For arrays, if nested array or bulk string, print with indentation
                    if (a.items[i] instanceof RespArray) {
                        System.out.println();
                        printResp(a.items[i], indent + "   ");
                    } else {
                        printRespInline(a.items[i], indent + "   ");
                    }
                }
            }
        } else {
            System.out.println("<unknown>");
        }
    }

    private static void printRespInline(RespType r, String indent) {
        if (r instanceof RespSimpleString) {
            System.out.println(((RespSimpleString) r).s);
        } else if (r instanceof RespError) {
            System.out.println("(error) " + ((RespError) r).s);
        } else if (r instanceof RespInteger) {
            System.out.println("(integer) " + ((RespInteger) r).value);
        } else if (r instanceof RespBulkString) {
            RespBulkString b = (RespBulkString) r;
            if (b.bytes == null) {
                System.out.println("(nil)");
            } else {
                String s = new String(b.bytes, StandardCharsets.UTF_8);
                System.out.println(s);
            }
        } else if (r instanceof RespArray) {
            // print nested arrays with indentation
            System.out.println();
            printResp(r, indent);
        } else {
            System.out.println("<unknown>");
        }
    }

    // ---------------- Byte escaping for verbose mode ----------------

    private static String escapeBytes(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte bb : bytes) {
            int b = bb & 0xFF;
            switch (b) {
                case '\r':
                    sb.append("\\r");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    if (b >= 0x20 && b <= 0x7E) {
                        sb.append((char) b);
                    } else {
                        sb.append(String.format("\\x%02X", b));
                    }
            }
        }
        return sb.toString();
    }
}
