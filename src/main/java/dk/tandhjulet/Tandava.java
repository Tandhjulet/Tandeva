package dk.tandhjulet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import dk.tandhjulet.parser.Parser;
import dk.tandhjulet.parser.TokenParser;
import dk.tandhjulet.parser.ast.ASTNode;
import dk.tandhjulet.parser.tokenizer.Tokenizer;

public class Tandava {
    public static void main(String[] args) {
        try {
            // debug
            String code = "sum = lambda(x, y) x + y;";
            InputStream stream = new ByteArrayInputStream(code.getBytes());

            Parser parser = new Parser(stream);
            Tokenizer lexer = new Tokenizer(parser);
            TokenParser tokenParser = new TokenParser(lexer);

            ASTNode program = tokenParser.parseTopLevel();
            parser.close();

            System.out.println("\nPROGRAM:");
            System.out.println(program.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}