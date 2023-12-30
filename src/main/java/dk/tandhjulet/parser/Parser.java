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
     * Returns the current char and afterwards advances the stream
     * 
     * @return the current char
     * @throws IOException
     */
    public synchronized char next() throws IOException {
        char holder = this.curr;
        this.curr = (char) read();
        return holder;
    }

    /**
     * @return true if the end of the file has been reaced, otherwise false
     * @throws IOException
     */
    public boolean eof() throws IOException {
        return peek() == (char) -1;
    }
}
