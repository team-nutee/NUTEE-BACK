package kr.nutee.nuteebackend.Exception;

import kr.nutee.nuteebackend.Enum.ErrorCode;

public class NotAllowedException extends BusinessException {
    public NotAllowedException(String msg, ErrorCode acceptDenied) {
        super(msg);
    }
}
