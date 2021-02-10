package kr.nutee.nuteebackend.Exception;

import kr.nutee.nuteebackend.Enum.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotExistException extends BusinessException {
    public NotExistException(String msg, ErrorCode errorCode, HttpStatus status) {
        super(msg,errorCode,status);
    }
    public NotExistException(String msg, ErrorCode errorCode, HttpStatus status, Long id) {
        super(msg,errorCode,status,id);
    }
}
