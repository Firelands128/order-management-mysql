/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@SuppressWarnings("WeakerAccess")
@ControllerAdvice
public class MyExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler({MyException.class})
    @ResponseBody
    public ResponseEntity<HttpError> handleOrderManagementException(MyException e) {
        logger.debug("Order exception handler ", e);
        return new ResponseEntity<>(new HttpError(e), HttpStatus.valueOf(e.getErrorCode().getValue()));
    }
}
