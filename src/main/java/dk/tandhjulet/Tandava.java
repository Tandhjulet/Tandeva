package dk.tandhjulet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import dk.tandhjulet.parser.Parser;
import dk.tandhjulet.parser.TokenParser;
import dk.tandhjulet.parser.tokenizer.Tokenizer;

public class Tandava {
    public static void main(String[] args) {
        try {
            // debug
            String code = "a = 5*4";
            InputStream stream = new ByteArrayInputStream(code.getBytes());

            Parser parser = new Parser(stream);
            Tokenizer lexer = new Tokenizer(parser);
            TokenParser tokenParser = new TokenParser(lexer);

            tokenParser.parseTopLevel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}