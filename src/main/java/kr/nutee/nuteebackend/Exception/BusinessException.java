package kr.nutee.nuteebackend.Exception;

import kr.nutee.nuteebackend.Enum.ErrorCode;

public class BusinessException extends RuntimeException {
    ErrorCode errorCode;

    public BusinessException(String msg, ErrorCode errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }

    public BusinessException(String msg) {
    }
}
