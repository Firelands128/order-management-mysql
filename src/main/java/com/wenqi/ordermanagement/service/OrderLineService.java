/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.service;

import com.wenqi.ordermanagement.entity.OrderLine;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderLineService {
    OrderLine saveOrderLine(OrderLine orderLine);

    List<OrderLine> getOrderLineByCustomerOrderId(long customerOrderId, Pageable pageRequest);

    List<OrderLine> getOrderLineByPurchaseOrderId(long purchaseOrderId, Pageable pageRequest);

    List<OrderLine> createOrderLineOfCustomerOrder(long customerOrderId, List<OrderLine> newOrderLineList);

    void returnItem(long lineId, int quantity);

    void receiveReturnItem(long lineId, int quantity);

    void cancelItem(long lineId);
}
