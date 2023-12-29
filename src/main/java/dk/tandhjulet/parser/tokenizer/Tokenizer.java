package dk.tandhjulet.parser.tokenizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import dk.tandhjulet.parser.Parser;
import dk.tandhjulet.parser.ParserException;
import dk.tandhjulet.utils.Regex;
import dk.tandhjulet.utils.Utils;

public class Tokenizer {
    private Parser parser;
    private static final Set<String> KEYWORDS = new HashSet<String>(Arrays.asList(new String[] {
            "if",
            "then",
            "else",
            "lambda",
            "true",
            "false",
    }));
    private TokenNode<?> current;

    public Tokenizer(Parser parser) {
        this.parser = parser;
    }

    public TokenNode<?> peek() throws IOException {
        return current == null ? (current = readNext()) : current;
    }

    public TokenNode<?> next() throws IOException {
        TokenNode<?> token = current;
        current = null;
        return token == null ? readNext() : token;
    }

    public boolean eof() throws IOException {
        return peek() == null;
    }

    private TokenNode<?> readNext() throws IOException {
        readWhile(ch -> isWhitespace(Utils.strToChar(ch)));
        if (parser.peek() == (char) -1)
            return null;

        char ch = parser.peek();

        if (ch == '#') {
            skipComment();
            return readNext();
        }

        else if (ch == '"')
            return readString();
        else if (isDigit(ch))
            return readNumber();
        else if (isIdStart(Utils.charToStr(ch)))
            return readIdent();
        else if (isPunc(ch))
            return new TokenNode<>(TokenType.PUNC, Utils.charToStr(parser.next()));
        else if (isOperator(ch))
            return new TokenNode<>(TokenType.OP, readWhile(op -> isOperator(Utils.strToChar(op))));

        throw new ParserException("Cannot read character: " + ch + "!");
    }

    private boolean isKeyword(String kw) {
        return KEYWORDS.contains(kw);
    }

    private boolean isDigit(char ch) {
        return Regex.matches("[0-9]", ch, Pattern.CASE_INSENSITIVE);
    }

    private boolean isDigit(String str) {
        return Regex.matches("[0-9]", str, Pattern.CASE_INSENSITIVE);
    }

    private boolean isIdStart(String ch) {
        return Regex.matches("[a-z_]", ch, Pattern.CASE_INSENSITIVE);
    }

    private boolean isId(String ch) {
        return isIdStart(ch) || "?!-<>=0123456789".indexOf(ch) >= 0;
    }

    private boolean isOperator(char ch) {
        return "+-*/%=&|<>!".indexOf(ch) >= 0;
    }

    private boolean isPunc(char ch) {
        return ",;(){}[]".indexOf(ch) >= 0;
    }

    private boolean isWhitespace(char ch) {
        return " \t\n".indexOf(ch) >= 0;
    }

    private String readWhile(Predicate<String> pred) throws IOException {
        String out = "";

        do {
            out += parser.peek();
        } while (parser.next() != (char) -1 && pred.test(Utils.charToStr(parser.peek())));

        return out;
    }

    private TokenNode<Number> readNumber() throws IOException {
        AtomicBoolean hasDot = new AtomicBoolean(false);

        String num = readWhile((ch) -> {
            if (ch == ".") {
                if (hasDot.get())
                    return false;
                hasDot.set(true);
                return true;
            }
            return isDigit(ch);
        });
        if (!hasDot.get())
            return new TokenNode<Number>(TokenType.NUM, Integer.parseInt(num));
        return new TokenNode<Number>(TokenType.NUM, Float.parseFloat(num));
    }

    private TokenNode<?> readIdent() throws IOException {
        String id = readWhile(x -> {
            return isId(x);
        });
        return new TokenNode<>(isKeyword(id) ? TokenType.KW : TokenType.VAR, id);
    }

    private String readEscaped(char end) throws IOException {
        boolean escaped = false;
        String str = "";

        parser.next();

        char ch;
        while ((ch = parser.next()) != (char) -1) {
            if (escaped) {
                str += ch;
                escaped = false;
            } else if (ch == '\\') {
                escaped = true;
            } else if (ch == end) {
                break;
            } else {
                str += ch;
            }
        }
        return str;
    }

    private TokenNode<?> readString() throws IOException {
        return new TokenNode<>(TokenType.STR, readEscaped('\"'));
    }

    private void skipComment() throws IOException {
        readWhile(ch -> ch != "\n");
        parser.next();
    }
}
