package kr.nutee.nuteebackend.Exception;

import kr.nutee.nuteebackend.Enum.ErrorCode;
import org.springframework.http.HttpStatus;

public class EmptyAttributeException extends BusinessException {
    public EmptyAttributeException(String msg, ErrorCode errorCode, HttpStatus status) {
        super(msg,errorCode,status);
    }
}
