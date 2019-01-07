/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.exception;

public class MyException extends RuntimeException {
    private final ErrorCode errorCode;
    private Throwable cause;

    public MyException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public MyException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public MyException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public MyException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
