package cc.catman.plugin.classloader.exceptions;

public class ClassLoadRuntimeException extends RuntimeException {
    public ClassLoadRuntimeException(String message) {
        super(message);
    }

    public ClassLoadRuntimeException(Throwable e) {
        super(e.getMessage(), e);
    }

    public ClassLoadRuntimeException(String message, Throwable e) {
        super(message, e);
    }
}
