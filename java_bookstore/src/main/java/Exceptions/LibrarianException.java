package Exceptions;

public class LibrarianException extends RuntimeException{
    public LibrarianException(){
        super();
    }

    public LibrarianException(String message){
        super(message);
    }

    public LibrarianException(String message, Throwable cause){
        super(message, cause);
    }
}
