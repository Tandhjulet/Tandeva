package dk.tandhjulet.parser;

public class ParserException extends RuntimeException {
    public ParserException(String errMessage, Throwable err) {
        super(errMessage, err);
    }

    public ParserException(String errMessage) {
        super(errMessage);
    }
}
