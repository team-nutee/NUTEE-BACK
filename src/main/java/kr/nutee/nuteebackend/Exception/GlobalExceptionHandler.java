package kr.nutee.nuteebackend.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<Object> notAllowedException(Exception e) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 51);
        map.put("message","권한이 없는 유저입니다.");
        log.warn("NotAllowedException" + e.getClass());
        return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> duplicateEx(Exception e) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 51);
        map.put("message","Unique 키가 데이터베이스 내부 에서 중복");
        log.warn("DataIntegrityViolationException" + e.getClass());
        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            IllegalArgumentException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<Object> paramsEx(Exception e) {
        Map<String, Object> map = new HashMap<>();
        map.put("errorCode", 51);
        map.put("message","");
        log.warn("params ex: "+ e.getClass());
        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> nullEx(Exception e) {
        Map<String, Object> map = new HashMap<>();
        map.put("errorCode", 61);
        map.put("message","");
        log.warn("null ex" + e.getClass());
        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }
}
