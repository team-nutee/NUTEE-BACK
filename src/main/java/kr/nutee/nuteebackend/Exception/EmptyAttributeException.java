package kr.nutee.nuteebackend.Exception;

import kr.nutee.nuteebackend.Enum.ErrorCode;

public class EmptyAttributeException extends BusinessException {
    public EmptyAttributeException(String msg, ErrorCode errorCode) {
        super(msg);
    }
}
