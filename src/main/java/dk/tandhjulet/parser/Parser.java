package dk.tandhjulet.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Parser extends InputStreamReader {
    private char curr;

    public Parser(InputStream i) {
        super(i);
    }

    public void croak(String msg, Throwable err) {
        throw new ParserException(msg, err);
    }

    public void croak(String msg) {
        throw new ParserException(msg);
    }

    /**
     * Peeks the current value of the input stream
     * 
     * @return the current char
     */
    public char peek() {
        return curr;
    }

    /**
     * @return The next char of the input stream
     * @throws IOException
     */
    public char next() throws IOException {
        this.curr = (char) super.read();
        return curr;
    }

    /**
     * EOF (end-of-file) returns a boolean value indicating whether the end of the
     * file has been reached.
     * 
     * @return True if end of file has been reached, otherwise false
     * @throws IOException
     */
    public boolean eof() throws IOException {
        return super.read() == -1;
    }
}
