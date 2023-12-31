package dk.tandhjulet.environment;

public class EnvironmentException extends RuntimeException {
    public EnvironmentException(String errMessage, Throwable err) {
        super(errMessage, err);
    }

    public EnvironmentException(String errMessage) {
        super(errMessage);
    }
}
