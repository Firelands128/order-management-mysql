/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.service.impl;

import com.wenqi.ordermanagement.constants.ItemStatus;
import com.wenqi.ordermanagement.entity.OrderLine;
import com.wenqi.ordermanagement.exception.ErrorCode;
import com.wenqi.ordermanagement.exception.MyException;
import com.wenqi.ordermanagement.repository.OrderLineRepository;
import com.wenqi.ordermanagement.service.OrderLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderLineServiceImpl implements OrderLineService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final OrderLineRepository orderLineRepository;

    @Autowired
    public OrderLineServiceImpl(OrderLineRepository orderLineRepository) {
        this.orderLineRepository = orderLineRepository;
    }

    @Override
    public OrderLine saveOrderLine(OrderLine orderLine) {
        return orderLineRepository.save(orderLine);
    }

    @Override
    public List<OrderLine> getOrderLineByCustomerOrderId(long customerOrderId, Pageable pageRequest) {
        if (customerOrderId == 0) {
            logger.error("CustomerOrderId cannot be 0, get line by customerOrderId failed.");
            throw new MyException(ErrorCode.BAD_REQUEST, "CustomerOrderId cannot be 0, get line by customerOrderId failed.");
        }
        Page<OrderLine> found = orderLineRepository.findByCustomerOrderId(customerOrderId, pageRequest);
        if (found == null) {
            logger.error("Line not found, get line by customerOrderId failed.");
            throw new MyException(ErrorCode.NOT_FOUND, "Line not found, get line by customerOrderId failed.");
        }
        return found.getContent();
    }

    @Override
    public List<OrderLine> getOrderLineByPurchaseOrderId(long purchaseOrderId, Pageable pageRequest) {
        if (purchaseOrderId == 0) {
            logger.error("PurchaseOrderId cannot be 0, get line by purchaseOrderId failed.");
            throw new MyException(ErrorCode.BAD_REQUEST, "PurchaseOrderId cannot be 0, get line by purchaseOrderId failed.");
        }
        Page<OrderLine> found = orderLineRepository.findByPurchaseOrderId(purchaseOrderId, pageRequest);
        if (found == null) {
            logger.error("Line not found, get line by purchaseOrderId failed.");
            throw new MyException(ErrorCode.NOT_FOUND, "Line not found, get line by purchaseOrderId failed.");
        }
        return found.getContent();
    }

    @Override
    public List<OrderLine> createOrderLineOfCustomerOrder(long customerOrderId, List<OrderLine> newOrderLineList) {
        for (OrderLine orderLine : newOrderLineList) {
            orderLine.setCustomerOrderId(customerOrderId);
        }
        logger.debug("Assigned customer order id: " + customerOrderId + " to order line.");
        List<OrderLine> addedOrderLineList = new ArrayList<>();
        for (OrderLine orderLine : newOrderLineList) {
            OrderLine addedOrderLine = orderLineRepository.save(orderLine);
            addedOrderLineList.add(addedOrderLine);
        }
        logger.debug("Created list of order lines: " + addedOrderLineList);
        return addedOrderLineList;
    }

    @Override
    public void returnItem(long lineId, int quantity) {
        OrderLine orderLine = getOrderLineById(lineId);
        if (orderLine.getQuantity() < quantity) {
            throw new MyException(ErrorCode.BAD_REQUEST, "Return quantity is too much.");
        } else {
            orderLine.setLineStatus(ItemStatus.RETURNING);
            orderLine.setReturnQuantity(quantity);
        }
        orderLineRepository.save(orderLine);
    }

    @Override
    public void receiveReturnItem(long lineId, int quantity) {
        OrderLine orderLine = getOrderLineById(lineId);
        if (orderLine.getReturnQuantity() != quantity) {
            throw new MyException(ErrorCode.BAD_REQUEST, "Return quantity is not equal to request return quantity.");
        } else {
            orderLine.setLineStatus(ItemStatus.RETURNED);
        }
        orderLineRepository.save(orderLine);
    }

    @Override
    public void cancelItem(long lineId) {
        logger.debug("Cancel item by line id: " + lineId);
        OrderLine orderLine = getOrderLineById(lineId);
        orderLine.setLineStatus(ItemStatus.CANCELLED);
        orderLineRepository.save(orderLine);
    }

    private OrderLine getOrderLineById(long lineId) {
        if (lineId == 0) {
            logger.error("LineId cannot be 0, get line by lineId failed.");
            throw new MyException(ErrorCode.BAD_REQUEST, "LineId cannot be 0, get line by lineId failed.");
        }
        OrderLine found = orderLineRepository.findByLineId(lineId);
        if (found == null) {
            logger.error("Line not found, get line by lineId failed.");
            throw new MyException(ErrorCode.NOT_FOUND, "Line not found, get line by lineId failed.");
        }
        return found;
    }
}
