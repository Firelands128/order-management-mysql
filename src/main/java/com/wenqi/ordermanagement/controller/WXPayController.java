/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.controller;

import com.wenqi.ordermanagement.dto.wxdto.WXOrderDTO;
import com.wenqi.ordermanagement.service.WXPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/wxpay")
public class WXPayController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WXPayService wxPayService;

    @Autowired
    public WXPayController(WXPayService wxPayService) {
        this.wxPayService = wxPayService;
    }

    /**
     * call wechat unified order API and
     * return a {@link ResponseEntity} of payment url
     * <p>
     * URL path: "/wxpay/unifiedorder/{customerOrderId}
     * </p>
     * <p>
     * HTTP method: GET
     * </p>
     *
     * @param customerOrderId The customer order id
     * @return a {@link ResponseEntity} of payment url
     */
    @RequestMapping(method = RequestMethod.GET, value = "/unifiedorder/{customerOrderId}")
    public ResponseEntity<String> callUnifiedOrder(@PathVariable("customerOrderId") long customerOrderId) {
        logger.info("Called weixin unified order API in controller, customer order id is: " + customerOrderId);
        String codeUrl = wxPayService.callUnifiedOrder(customerOrderId);
        return new ResponseEntity<>(codeUrl, HttpStatus.OK);
    }

    /**
     * callback API of wechat unified order API and
     * return a {@link ResponseEntity} of return message
     * <p>
     * URL path: "/wxpay/unifiedorder/notify
     * </p>
     * <p>
     * HTTP method: POST
     * </p>
     *
     * @param strXml The callback message
     * @return a {@link ResponseEntity} of return message
     */
    @RequestMapping(method = RequestMethod.POST, value = "/unifiedorder/notify", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> unifiedOrderRecallUrl(@RequestBody String strXml) {
        logger.info("Received weixin pay result notification: " + strXml);
        String returnStr = wxPayService.dealNotification(strXml);
        return new ResponseEntity<>(returnStr, HttpStatus.OK);
    }

    /**
     * call wechat order query API and
     * return a {@link ResponseEntity} of order details
     * <p>
     * URL path: "/wxpay/orderquer/{customerOrderId}
     * </p>
     * <p>
     * HTTP method: GET
     * </p>
     *
     * @param customerOrderId The CustomerOrderId
     * @return a {@link ResponseEntity} of order details
     */
    @RequestMapping(method = RequestMethod.GET, value = "/orderquery/{customerOrderId}")
    public ResponseEntity<WXOrderDTO> callOrderQuery(@PathVariable("customerOrderId") long customerOrderId) {
        logger.info("Called weixin order query API in controller, customer order id is: " + customerOrderId);
        WXOrderDTO found = wxPayService.callOrderQuery(customerOrderId);
        return new ResponseEntity<>(found, HttpStatus.OK);
    }

    /**
     * call wechat close order API and
     * return a {@link ResponseEntity} without response body
     * <p>
     * URl path: "/wxpay/closeorder/{customerOrderId}
     * </p>
     * <p>
     * HTTP method: HEAD
     * </p>
     *
     * @param customerOrderId The customer order id
     * @return a {@link ResponseEntity} without response body
     */
    @RequestMapping(method = RequestMethod.HEAD, value = "/closeorder/{customerOrderId}")
    public ResponseEntity<WXOrderDTO> callCloseOrder(@PathVariable("customerOrderId") long customerOrderId) {
        logger.info("Called weixin close order API in controller, customer order id is: " + customerOrderId);
        wxPayService.callCloseOrder(customerOrderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
