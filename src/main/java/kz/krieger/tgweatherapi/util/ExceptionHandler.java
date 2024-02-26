package kz.krieger.tgweatherapi.util;

public class ExceptionHandler extends Exception {
    public ExceptionHandler(String message, Throwable cause){
        super(message,cause);
    }
    public ExceptionHandler(String message) {
        super(message);
    }
}
