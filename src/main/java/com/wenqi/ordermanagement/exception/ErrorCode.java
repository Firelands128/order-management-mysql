/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value()),
    NOT_FOUND(HttpStatus.NOT_FOUND.value()),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value());

    private final int value;

    ErrorCode(int value) {
        this.value = value;
    }

    public static ErrorCode getErrorCode(int value) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.value == value) {
                return errorCode;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    String getMessage() {
        if (value == ErrorCode.NOT_FOUND.getValue()) {
            return "Not Found.";
        } else if (value == ErrorCode.BAD_REQUEST.getValue()) {
            return "Bad Request.";
        } else if (value == ErrorCode.INTERNAL_SERVER_ERROR.getValue()) {
            return "Internal Server Error";
        }
        return null;
    }
}