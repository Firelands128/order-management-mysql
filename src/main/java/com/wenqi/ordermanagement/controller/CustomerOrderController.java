/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.controller;

import com.wenqi.ordermanagement.dto.CustomerOrderDTO;
import com.wenqi.ordermanagement.dto.CustomerOrderGotDTO;
import com.wenqi.ordermanagement.service.CustomerOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/customerorder", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CustomerOrderController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CustomerOrderService customerOrderService;

    @Autowired
    public CustomerOrderController(CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    /**
     * add a customer order and
     * return a {@link ResponseEntity} of the added {@link CustomerOrderDTO}
     * <p>
     * URL path: "/customerorder"
     * </p>
     * <p>
     * HTTP method: POST
     * </p>
     *
     * @param newCustomerOrderDTO The data transfer object of customer order
     * @return a {@link ResponseEntity} of the added {@link CustomerOrderDTO}
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerOrderGotDTO> createCustomerOrder(@RequestBody CustomerOrderDTO newCustomerOrderDTO) {
        logger.info("Add customer order: " + newCustomerOrderDTO);
        long customerOrderId = customerOrderService.createCustomerOrder(newCustomerOrderDTO);
        Pageable pageRequest = PageRequest.of(0, 2000);
        CustomerOrderGotDTO addedCustomerOrderDTO = customerOrderService.getCustomerOrderIncludeOrderLineByCustomerOrderId(customerOrderId, pageRequest);
        return new ResponseEntity<>(addedCustomerOrderDTO, HttpStatus.CREATED);
    }

    /**
     * update a customer order status to paid and
     * return a {@link ResponseEntity} of the added {@link CustomerOrderDTO}
     * <p>
     * URL path: "/customerorder/paid/{customerOrderId}
     * </p>
     * <p>
     * HTTP method: GET
     * </p>
     *
     * @param customerOrderId The customer order id
     * @return a {@link ResponseEntity} of the {@link CustomerOrderDTO}
     */
    @RequestMapping(method = RequestMethod.GET, value = "/paid/{customerOrderId}")
    public ResponseEntity<CustomerOrderGotDTO> paidCustomerOrderStatus(@PathVariable("customerOrderId") long customerOrderId) {
        logger.info("Update customer order status to paid, customer order id is: " + customerOrderId);
        customerOrderService.updateCustomerOrderStatusToPaid(customerOrderId);
        CustomerOrderGotDTO updatedCustomerOrderDTO = customerOrderService.getCustomerOrderIncludePurchaseOrderByCustomerOrderId(customerOrderId, PageRequest.of(0, 2000), PageRequest.of(0, 2000));
        return new ResponseEntity<>(updatedCustomerOrderDTO, HttpStatus.OK);
    }

    /**
     * get a customer order by customer order id and
     * return a {@link ResponseEntity} of the added {@link CustomerOrderDTO}
     * <p>
     * URL path: "/customerorder/{customerOrderId}
     * </p>
     * <p>
     * HTTP method: GET
     * </p>
     *
     * @param customerOrderId          The customer order id
     * @param purchaseOrderPageRequest The {@link org.springframework.data.domain.PageRequest} of purchase order
     * @param orderLinePageRequest     The {@link org.springframework.data.domain.PageRequest} of order line
     * @return a {@link ResponseEntity} of the {@link CustomerOrderDTO}
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{customerOrderId}")
    public ResponseEntity<CustomerOrderGotDTO> getCustomerOrderByCustomerOrderId(@PathVariable("customerOrderId") long customerOrderId,
                                                                                 @Qualifier("purchase") Pageable purchaseOrderPageRequest,
                                                                                 @Qualifier("line") Pageable orderLinePageRequest) {
        logger.info("Get customer order by customer order id: " + customerOrderId);
        CustomerOrderGotDTO gotCustomerOrderDTO = customerOrderService
                .getCustomerOrderIncludePurchaseOrderByCustomerOrderId(customerOrderId, purchaseOrderPageRequest, orderLinePageRequest);
        return new ResponseEntity<>(gotCustomerOrderDTO, HttpStatus.OK);
    }

    /**
     * get a list of customer orders by customer id and create date time range and
     * return a {@link ResponseEntity} of a list of the added {@link CustomerOrderDTO}
     * <p>
     * URL path: "/customerorder/customer/{customerId}
     * </p>
     * <p>
     * HTTP method: GET
     * </p>
     *
     * @param customerId               The customer id
     * @param startDate                The start date
     * @param endDate                  The end date
     * @param customerOrderPageRequest The {@link org.springframework.data.domain.PageRequest} of customer order
     * @param purchaseOrderPageRequest The {@link org.springframework.data.domain.PageRequest} of purchase order
     * @param orderLinePageRequest     The {@link org.springframework.data.domain.PageRequest} of order line
     * @return a {@link ResponseEntity} of a list of the {@link CustomerOrderDTO}
     */
    @RequestMapping(method = RequestMethod.GET, value = "/customer/{customerId}")
    public ResponseEntity<List<CustomerOrderGotDTO>>
    getCustomerOrderByCustomerIdAndCreateDatetimeRange(@PathVariable("customerId") long customerId,
                                                       @RequestParam("startDate")
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                               Date startDate,
                                                       @RequestParam("endDate")
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                               Date endDate,
                                                       @Qualifier("customer") Pageable customerOrderPageRequest,
                                                       @Qualifier("purchase") Pageable purchaseOrderPageRequest,
                                                       @Qualifier("line") Pageable orderLinePageRequest) {
        logger.info("Get customer order by customer id: " + customerId
                + " and start date: " + startDate + " and end date: " + endDate + " in controller.");
        List<CustomerOrderGotDTO> gotCustomerOrderDTOList = customerOrderService
                .getCustomerOrderByCustomerIdAndCreateDatetimeRange(customerId, startDate, endDate,
                        customerOrderPageRequest, purchaseOrderPageRequest, orderLinePageRequest);
        return new ResponseEntity<>(gotCustomerOrderDTOList, HttpStatus.OK);
    }

    /**
     * cancel a customer order by customer order id and
     * return a {@link ResponseEntity} without response body
     * <p>
     * URL path: "/customerorder/cancel/{customerOrderId}
     * </p>
     * <p>
     * HTTP method: HEAD
     * </p>
     *
     * @param customerOrderId The customer order id
     * @return a {@link ResponseEntity} without response body
     */
    @RequestMapping(method = RequestMethod.HEAD, value = "/cancel/{customerOrderId}")
    public ResponseEntity cancelCustomerOrderByCustomerOrderId(@PathVariable("customerOrderId") long customerOrderId) {
        logger.info("Cancel customer order by customer order id: " + customerOrderId);
        customerOrderService.cancelCustomerOrderByCustomerOrderId(customerOrderId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
