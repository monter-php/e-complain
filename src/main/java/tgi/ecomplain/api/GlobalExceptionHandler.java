package tgi.ecomplain.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import tgi.ecomplain.domain.complain.ComplainNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ComplainNotFoundException.class)
    public ResponseEntity<Object> handleComplainNotFoundException(ComplainNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
