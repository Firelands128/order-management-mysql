/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.controller;

import com.wenqi.ordermanagement.constants.PurchaseStatus;
import com.wenqi.ordermanagement.dto.PurchaseOrderDTO;
import com.wenqi.ordermanagement.service.PurchaseOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/purchaseorder", produces = MediaType.APPLICATION_JSON_VALUE)
public class PurchaseOrderController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final PurchaseOrderService purchaseOrderService;

    @Autowired
    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    /**
     * update a purchase order status to shipping and
     * return a {@link ResponseEntity} of the {@link PurchaseOrderDTO}
     * <p>
     * URL path: "/purchaseorder/shipped/{purchaseOrderId}
     * </p>
     * <p>
     * HTTP method: GET
     * </p>
     *
     * @param purchaseOrderId The purchase order id
     * @return a {@link ResponseEntity} of the {@link PurchaseOrderDTO}
     */
    @RequestMapping(method = RequestMethod.GET, value = "/shipped/{purchaseOrderId}")
    public ResponseEntity<PurchaseOrderDTO> updatePurchaseOrderStatusToShipping(@PathVariable("purchaseOrderId") long purchaseOrderId) {
        logger.info("Update purchase order status in controller: " + purchaseOrderId);
        PurchaseOrderDTO updatedPurchaseOrderDTO = purchaseOrderService.updatePurchaseOrderStatus(purchaseOrderId, PurchaseStatus.SHIPPING);
        return new ResponseEntity<>(updatedPurchaseOrderDTO, HttpStatus.OK);
    }

    /**
     * update a purchase order status to delivery and
     * return a {@link ResponseEntity} of the {@link PurchaseOrderDTO}
     * <p>
     * URL path: "/purchaseorder/delivered/{purchaseOrderId}
     * </p>
     * <p>
     * HTTP method: GET
     * </p>
     *
     * @param purchaseOrderId The purchase order id
     * @return a {@link ResponseEntity} of the {@link PurchaseOrderDTO}
     */
    @RequestMapping(method = RequestMethod.GET, value = "/delivered/{purchaseOrderId}")
    public ResponseEntity<PurchaseOrderDTO> updatePurchaseOrderStatusToDelivery(@PathVariable("purchaseOrderId") long purchaseOrderId) {
        logger.info("Update purchase order status in controller: " + purchaseOrderId);
        PurchaseOrderDTO updatedPurchaseOrderDTO = purchaseOrderService.updatePurchaseOrderStatus(purchaseOrderId, PurchaseStatus.DELIVERED);
        return new ResponseEntity<>(updatedPurchaseOrderDTO, HttpStatus.OK);
    }

    /**
     * get a purchase order by purchase order id and
     * return a {@link ResponseEntity} of the {@link PurchaseOrderDTO}
     * <p>
     * URL path: "/purchaseorder/{purchaseOrderId}
     * </p>
     * <p>
     * HTTP method: GET
     * </p>
     *
     * @param purchaseOrderId The purchase order id
     * @param pageRequest     The {@link org.springframework.data.domain.PageRequest} of purchase order
     * @return a {@link ResponseEntity} of the {@link PurchaseOrderDTO}
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{purchaseOrderId}")
    public ResponseEntity<PurchaseOrderDTO> getPurchaseOrderById(@PathVariable("purchaseOrderId") long purchaseOrderId,
                                                                 Pageable pageRequest) {
        logger.info("Get purchase order by purchase order id in controller: " + purchaseOrderId);
        PurchaseOrderDTO gotPurchaseOrderDTO = purchaseOrderService.getPurchaseOrderByPurchaseOrderId(purchaseOrderId, pageRequest);
        return new ResponseEntity<>(gotPurchaseOrderDTO, HttpStatus.OK);
    }

    /**
     * get a list of purchase orders by provider id and create datetime range then
     * return a {@link ResponseEntity} of list of the {@link PurchaseOrderDTO}
     * <p>
     * URL path: "/purchaseorder/provider/{providerId}
     * </p>
     * <p>
     * HTTP method: GET
     * </p>
     *
     * @param providerId               The provider id
     * @param startDate                The start date
     * @param endDate                  The end date
     * @param purchaseOrderPageRequest The {@link org.springframework.data.domain.PageRequest} of purchase order
     * @param orderLinePageRequest     The {@link org.springframework.data.domain.PageRequest} of order line
     * @return a {@link ResponseEntity} of list of the {@link PurchaseOrderDTO}
     */
    @RequestMapping(method = RequestMethod.GET, value = "/provider/{providerId}")
    public ResponseEntity<List<PurchaseOrderDTO>> getPurchaseOrderByProviderId(@PathVariable("providerId") long providerId,
                                                                               @RequestParam("startDate")
                                                                               @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                                       Date startDate,
                                                                               @RequestParam("endDate")
                                                                               @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                                       Date endDate,
                                                                               @Qualifier("purchase") Pageable purchaseOrderPageRequest,
                                                                               @Qualifier("line") Pageable orderLinePageRequest) {
        logger.info("Get purchase order by provider id: " + providerId
                + " and start date: " + startDate + " and end date: " + endDate + " in controller.");
        List<PurchaseOrderDTO> gotPurchaseOrderDTOList = purchaseOrderService
                .getPurchaseOrderByProviderIdAndCreateDatetimeRange(providerId, startDate, endDate,
                        purchaseOrderPageRequest, orderLinePageRequest);
        return new ResponseEntity<>(gotPurchaseOrderDTOList, HttpStatus.OK);
    }

    /**
     * cancel a purchase order by purchase order id and udpate customer order status if needed then
     * return a {@link ResponseEntity} without response body
     * <p>
     * URL path: "/purchaseorder/cancel/{purchaseOrderId}
     * </p>
     * <p>
     * HTTP method: HEAD
     * </p>
     *
     * @param purchaseOrderId The purchase order id
     * @return a {@link ResponseEntity} without response body
     */
    @RequestMapping(method = RequestMethod.HEAD, value = "/cancel/{purchaseOrderId}")
    public ResponseEntity cancelPurchaseOrderByPurchaseOrderId(@PathVariable("purchaseOrderId") long purchaseOrderId) {
        logger.info("Cancel purchase order by purchase order id: " + purchaseOrderId);
        purchaseOrderService.cancelPurchaseOrderByPurchaseOrderId(purchaseOrderId);
        return new ResponseEntity(HttpStatus.OK);
    }

}
