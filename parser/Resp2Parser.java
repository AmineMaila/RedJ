package parser;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import command.Command;

public class Resp2Parser {
    private final BufferedInputStream in;

    public Resp2Parser(BufferedInputStream in) {
        this.in = in;
    }

    public Command parse() throws IOException {
        int prefix = in.read();

        if (prefix == -1) {
            throw new EOFException("Stream closed");
        }

        if (prefix != '*')
            throw new IOException("");
    }
}