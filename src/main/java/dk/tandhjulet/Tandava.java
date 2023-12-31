package dk.tandhjulet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import dk.tandhjulet.environment.Environment;
import dk.tandhjulet.environment.Evaluator;
import dk.tandhjulet.parser.Parser;
import dk.tandhjulet.parser.TokenParser;
import dk.tandhjulet.parser.ast.ASTNode;
import dk.tandhjulet.parser.tokenizer.Tokenizer;

public class Tandava {
    public static void main(String[] args) {
        try {
            // debug
            String code = "sum = lambda(x, y) x + y; print(sum(5,4));";
            InputStream stream = new ByteArrayInputStream(code.getBytes());

            Parser parser = new Parser(stream);
            Tokenizer lexer = new Tokenizer(parser);
            TokenParser tokenParser = new TokenParser(lexer);

            ASTNode program = tokenParser.parseTopLevel();
            parser.close();

            System.out.println("\nPROGRAM converted to AST:");
            System.out.println(program.toString() + "\n");

            Environment env = new Environment(null);

            env.defineFunction("print", (printArgs) -> {
                for (Object arg : printArgs) {
                    System.out.println(arg.toString());
                }

                return null;
            });

            Evaluator.evaluate(program, env);

            System.out.println("\nPROGRAM has ran!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}