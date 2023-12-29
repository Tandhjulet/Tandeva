package dk.tandhjulet.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Parser extends InputStreamReader {
    private char curr;

    public Parser(InputStream i) throws IOException {
        super(i);
        next();
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
}
