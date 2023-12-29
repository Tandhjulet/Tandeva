package dk.tandhjulet.parser;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import dk.tandhjulet.parser.ast.ASTNode;
import dk.tandhjulet.parser.ast.types.ASTAssign;
import dk.tandhjulet.parser.ast.types.ASTBinary;
import dk.tandhjulet.parser.ast.types.ASTBoolean;
import dk.tandhjulet.parser.ast.types.ASTCall;
import dk.tandhjulet.parser.ast.types.ASTIf;
import dk.tandhjulet.parser.ast.types.ASTLambda;
import dk.tandhjulet.parser.ast.types.ASTNumber;
import dk.tandhjulet.parser.ast.types.ASTProg;
import dk.tandhjulet.parser.ast.types.ASTString;
import dk.tandhjulet.parser.ast.types.ASTVariable;
import dk.tandhjulet.parser.tokenizer.TokenNode;
import dk.tandhjulet.parser.tokenizer.TokenType;
import dk.tandhjulet.parser.tokenizer.Tokenizer;

public class TokenParser {
    private static final Map<String, Integer> PRECEDENCE = new HashMap<>();

    private final Tokenizer tokenizer;

    public TokenParser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public ASTNode parseTopLevel() throws Exception {
        List<ASTNode> prog = new LinkedList<>();
        while (!tokenizer.eof()) {
            prog.add(parseExpression());
            if (!tokenizer.eof())
                skipPunc(";");
        }
        return new ASTProg(prog.toArray(new ASTNode[0]));
    }

    @SuppressWarnings("unchecked")
    private ASTNode maybeBinary(ASTNode left, Integer prec) throws Exception {
        TokenNode<String> token = (TokenNode<String>) isOperator(null);
        if (token != null) {
            int tokenPrec = PRECEDENCE.get(token.getValue());
            if (tokenPrec > prec) {
                tokenizer.next();

                ASTNode node;
                if (token.getValue() == "=") {
                    node = new ASTAssign(left, maybeBinary(parseAtom(), tokenPrec));
                } else {
                    node = new ASTBinary(token.getValue(), left, maybeBinary(parseAtom(), tokenPrec));
                }
                return maybeBinary(node, prec);
            }
        }
        return left;
    }

    private ASTNode parseExpression() throws Exception {
        return maybeCall(() -> {
            return maybeBinary(parseAtom(), 0);
        });
    }

    @SuppressWarnings("unchecked")
    private <T> T[] delimited(String start, String stop, String seperator, Callable<T> callable)
            throws Exception {
        Set<T> variables = new HashSet<>();
        boolean first = true;

        skipPunc(start);
        while (!tokenizer.eof()) {
            if (isPunc(stop) != null)
                break;

            if (first)
                first = false;
            else
                skipPunc(seperator);

            if (isPunc(stop) != null)
                break;

            variables.add(callable.call());
        }
        skipPunc(stop);
        return (T[]) variables.toArray();
    }

    private ASTNode parseCall(ASTNode call) throws Exception {
        return new ASTCall(call, delimited("(", ")", ",", () -> {
            return parseExpression();
        }));
    }

    private ASTNode maybeCall(Callable<ASTNode> expr) throws Exception {
        ASTNode res = expr.call();
        return isPunc("(") != null ? parseCall(res) : res;
    }

    private ASTNode parseProg() throws Exception {
        List<ASTNode> prog = Arrays.asList(delimited("{", "}", ";", () -> {
            return parseExpression();
        }));

        while (!tokenizer.eof()) {
            prog.add(parseExpression());
            if (!tokenizer.eof())
                skipPunc(";");
        }
        return new ASTProg(prog.toArray(new ASTNode[0]));
    }

    private ASTNode parseIf() throws Exception {
        skipKeyword("if");
        ASTNode cond = parseExpression();
        if (isPunc("{") == null)
            skipKeyword("then");

        ASTNode then = parseExpression();
        if (isKeyword("else") != null) {
            tokenizer.next();
            return new ASTIf(cond, then, parseExpression());
        }
        return new ASTIf(cond, then);
    }

    private ASTNode parseBool() throws IOException {
        return new ASTBoolean(tokenizer.next().getValue(String.class) == "true");
    }

    private String parseVarName() throws IOException {
        TokenNode<?> name = tokenizer.next();
        if (name.getType() != TokenType.VAR)
            throw new ParserException("Expected variable name");
        return name.getValue(String.class);
    }

    private ASTNode parseLambda() throws Exception {
        return new ASTLambda(parseExpression(), delimited("(", ")", ",", () -> {
            return parseVarName();
        }));
    }

    private ASTNode parseAtom() throws Exception {
        return maybeCall(() -> {
            if (isPunc("(") != null) {
                tokenizer.next();
                ASTNode exp = parseExpression();
                skipPunc(")");
                return exp;
            }
            if (isPunc("{") != null)
                return parseProg();
            if (isKeyword("if") != null)
                return parseIf();
            if (isKeyword("true") != null || isKeyword("false") != null)
                return parseBool();
            if (isKeyword("lambda") != null) {
                tokenizer.next();
                return parseLambda();
            }

            TokenNode<?> token = tokenizer.next();
            System.out.println(token.getType());

            if (token.getType().equals(TokenType.VAR))
                return new ASTVariable(token.getValue(String.class));
            else if (token.getType().equals(TokenType.NUM))
                return new ASTNumber(token.getValue(Number.class));
            else if (token.getType().equals(TokenType.STR))
                return new ASTString(token.getValue(String.class));

            unexpectedToken();
            return null;
        });
    }

    private TokenNode<?> isPunc(String ch) throws IOException {
        TokenNode<?> token = tokenizer.peek();
        return (token != null && token.getType() == TokenType.PUNC
                && (ch == null || token.getValue(String.class) == ch)) ? token : null;
    }

    private TokenNode<?> isKeyword(String kw) throws IOException {
        TokenNode<?> token = tokenizer.peek();
        return (token != null && token.getType() == TokenType.KW && (kw == null || token.getValue(String.class) == kw))
                ? token
                : null;
    }

    private TokenNode<?> isOperator(String op) throws IOException {
        TokenNode<?> token = tokenizer.peek();
        return (token != null && token.getType() == TokenType.OP && (op == null || token.getValue(String.class) == op))
                ? token
                : null;
    }

    private void skipPunc(String ch) throws IOException {
        if (isPunc(ch) != null)
            tokenizer.next();
        else
            throw new ParserException("Expecting punctuation: \"" + ch + "\"");
    }

    private void skipKeyword(String kw) throws IOException {
        if (isKeyword(kw) != null)
            tokenizer.next();
        else
            throw new ParserException("Expecting keyword: \"" + kw + "\"");
    }

    private void unexpectedToken() throws IOException {
        throw new ParserException("Unexpected token: " + tokenizer.peek().toString());
    }

    static {
        PRECEDENCE.put("=", 1);
        PRECEDENCE.put("||", 2);
        PRECEDENCE.put("&&", 3);

        PRECEDENCE.put("<", 7);
        PRECEDENCE.put(">", 7);
        PRECEDENCE.put("<=", 7);
        PRECEDENCE.put(">=", 7);
        PRECEDENCE.put("==", 7);
        PRECEDENCE.put("!=", 7);

        PRECEDENCE.put("+", 10);
        PRECEDENCE.put("-", 10);

        PRECEDENCE.put("*", 20);
        PRECEDENCE.put("/", 20);
        PRECEDENCE.put("%", 20);
    }

}
