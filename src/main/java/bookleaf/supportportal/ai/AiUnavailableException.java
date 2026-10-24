package bookleaf.supportportal.ai;

public class AiUnavailableException extends RuntimeException {
    public AiUnavailableException(String msg, Throwable cause) { super(msg, cause); }
    public AiUnavailableException(String msg) { super(msg); }
}