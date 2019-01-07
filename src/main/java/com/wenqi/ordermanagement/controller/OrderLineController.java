/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.controller;

import com.wenqi.ordermanagement.service.OrderLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/orderline", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OrderLineController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final OrderLineService orderLineService;

    @Autowired
    public OrderLineController(OrderLineService orderLineService) {
        this.orderLineService = orderLineService;
    }

    /**
     * return an item by line id and parameter quantity then
     * return a {@link ResponseEntity} without response body
     * <p>
     * URL path: "/orderline/return/{lineId}
     * </p>
     * <p>
     * HTTP method: HEAD
     * </p>
     *
     * @param lineId   The line id
     * @param quantity The returning quantity
     * @return a {@link ResponseEntity} without response body
     */
    @RequestMapping(method = RequestMethod.HEAD, value = "/return/{lineId}")
    public ResponseEntity returnItem(@PathVariable("lineId") long lineId,
                                     @RequestParam("quantity") int quantity) {
        logger.info("Return an item in controller, lineId is: " + lineId + ", quantity is: " + quantity);
        orderLineService.returnItem(lineId, quantity);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * received a returned item by line id and received quantity then
     * return a {@link ResponseEntity} without response body
     * <p>
     * URL path: "/orderline/receive/{lineId}
     * </p>
     * <p>
     * HTTP method: HEAD
     * </p>
     *
     * @param lineId   The line id
     * @param quantity The received returning quantity
     * @return a {@link ResponseEntity} without response body
     */
    @RequestMapping(method = RequestMethod.HEAD, value = "/receive/{lineId}")
    public ResponseEntity receiveReturnItem(@PathVariable("lineId") long lineId,
                                            @RequestParam("quantity") int quantity) {
        logger.info("Receive a returned item in controller, lineId is: " + lineId + ", quantity is: " + quantity);
        orderLineService.receiveReturnItem(lineId, quantity);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * cancel an item and update customer order and purchase order status if needed then
     * return a {@link ResponseEntity} without response body
     * <p>
     * URL path: "/orderline/cancel/{lineId}
     * </p>
     * <p>
     * HTTP method: HEAD
     * </p>
     *
     * @param lineId The line id
     * @return a {@link ResponseEntity} without response body
     */
    @RequestMapping(method = RequestMethod.HEAD, value = "/cancel/{lineId}")
    public ResponseEntity cancelItem(@PathVariable("lineId") long lineId) {
        logger.info("Receive a returned item in controller, lineId is: " + lineId);
        orderLineService.cancelItem(lineId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
