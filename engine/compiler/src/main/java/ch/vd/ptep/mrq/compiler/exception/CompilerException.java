package ch.vd.ptep.mrq.compiler.exception;

public class CompilerException extends Exception {

    public CompilerException(String message, String logs, Throwable cause) {
        super("%s\nPrism command output:\n%s".formatted(message, logs), cause);
    }

    public CompilerException(String message, String logs) {
        super("%s\nPrism command output:\n%s".formatted(message, logs));
    }

    public CompilerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompilerException(String message) {
        super(message);
    }
}
