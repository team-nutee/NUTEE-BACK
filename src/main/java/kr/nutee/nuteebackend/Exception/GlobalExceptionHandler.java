package kr.nutee.nuteebackend.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<Object> notAllowedException(Exception e) {
        return new ResponseEntity<>(e, HttpStatus.NOT_ACCEPTABLE);
    }
}
