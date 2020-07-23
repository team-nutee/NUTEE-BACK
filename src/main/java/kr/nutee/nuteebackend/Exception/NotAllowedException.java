package kr.nutee.nuteebackend.Exception;

import kr.nutee.nuteebackend.Enum.ErrorCode;
import org.springframework.http.HttpStatus;

public class NotAllowedException extends BusinessException {
    public NotAllowedException(String msg, ErrorCode errorCode, HttpStatus status) {
        super(msg,errorCode,status);
    }
}
