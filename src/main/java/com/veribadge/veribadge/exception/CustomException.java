package com.veribadge.veribadge.exception;

import com.veribadge.veribadge.global.status.ErrorStatus;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ErrorStatus errorCode;

    public CustomException(ErrorStatus errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorStatus errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }
}
