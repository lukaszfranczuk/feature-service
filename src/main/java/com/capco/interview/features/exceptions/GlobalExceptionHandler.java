package com.capco.interview.features.exceptions;

import com.capco.interview.features.api.model.ErrorBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = FeatureAlreadyExistException.class)
    protected ResponseEntity<ErrorBody> handleFeatureAlreadyExistException(RuntimeException ex) {
        ErrorBody errorBody = new ErrorBody();
        errorBody.setMessage(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errorBody);
    }

    @ExceptionHandler(value = FeatureNotExistException.class)
    protected ResponseEntity<ErrorBody> handleFeatureNotExistException(RuntimeException ex) {
        ErrorBody errorBody = new ErrorBody();
        errorBody.setMessage(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorBody);
    }

    @ExceptionHandler(value = UserNotExistException.class)
    protected ResponseEntity<ErrorBody> handleUserNotExistException(RuntimeException ex) {
        ErrorBody errorBody = new ErrorBody();
        errorBody.setMessage(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorBody);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ErrorBody> handleAuthenticationException(Exception ex) {
        ErrorBody errorBody = new ErrorBody();
        errorBody.setMessage("User in not authenticated");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorBody);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<ErrorBody> handleAccessDeniedException(Exception ex) {
        ErrorBody errorBody = new ErrorBody();
        errorBody.setMessage("User doesn't have permissions to perform this action");
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorBody);
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<ErrorBody> handleAllUnhandledException(Exception ex) {
        ErrorBody errorBody = new ErrorBody();
        errorBody.setMessage("Unexpected error occurred");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody);
    }
}
