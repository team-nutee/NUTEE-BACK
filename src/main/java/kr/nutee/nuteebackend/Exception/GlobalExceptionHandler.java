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
        log.warn("NotAllowedException" + e.getClass());
        return new ResponseEntity<>("접근 권한이 없는 유저입니다.", HttpStatus.NOT_ACCEPTABLE);
    }
}
