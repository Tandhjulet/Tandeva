package dk.tandhjulet.parser.tokenizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import dk.tandhjulet.parser.Parser;
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
    TokenNode<?> current;

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

    public void croak(String msg, Throwable err) {
        parser.croak(msg, err);
    }

    public void croak(String msg) {
        parser.croak(msg);
    }

    private TokenNode<?> readNext() throws IOException {
        readWhile(ch -> isWhitespace(Utils.strToChar(ch)));
        if (parser.eof())
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
        else if (isIdStart(ch))
            return readIdent();
        else if (isPunc(ch))
            return new TokenNode<>(TokenType.PUNC, parser.next());
        else if (isOperator(ch))
            return new TokenNode<>(TokenType.OP, readWhile(op -> isOperator(Utils.strToChar(op))));

        parser.croak("Cannot read character: " + ch + "!");
        return null;
    }

    private boolean isKeyword(String kw) {
        return KEYWORDS.contains(kw);
    }

    private boolean isDigit(char ch) {
        return isDigit(Utils.charToStr(ch));
    }

    private boolean isDigit(String str) {
        return str.matches("[0-9]");
    }

    private boolean isIdStart(char ch) {
        return isIdStart(Utils.charToStr(ch));
    }

    private boolean isIdStart(String str) {
        return "[a-zA-Z]".matches(str);
    }

    private boolean isId(char ch) {
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
        while (!parser.eof() && pred.test(Utils.charToStr(parser.peek()))) {
            out += parser.next();
        }
        return out;
    }

    private TokenNode<?> readNumber() throws IOException {
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
        return new TokenNode<>(TokenType.NUM, Float.parseFloat(num));
    }

    private TokenNode<?> readIdent() throws IOException {
        String id = readWhile(x -> isId(Utils.strToChar(x)));

        return new TokenNode<>(isKeyword(id) ? TokenType.KW : TokenType.VAR, id);
    }

    private String readEscaped(char end) throws IOException {
        boolean escaped = false;
        String str = "";

        parser.next();
        while (!parser.eof()) {
            char ch = parser.next();
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
