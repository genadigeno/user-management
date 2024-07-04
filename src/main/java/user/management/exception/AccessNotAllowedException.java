package user.management.exception;

public class AccessNotAllowedException extends RuntimeException {
    public AccessNotAllowedException() {
        super("Access not allowed");
    }
}
