/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.service;

import com.wenqi.ordermanagement.dto.CustomerOrderDTO;
import com.wenqi.ordermanagement.dto.CustomerOrderGotDTO;
import com.wenqi.ordermanagement.entity.CustomerOrder;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface CustomerOrderService {
    long createCustomerOrder(CustomerOrderDTO customerOrderDTO);

    void updateCustomerOrderStatusToPaid(long customerOrderId);

    CustomerOrder getCustomerOrderById(long customerOrderId);

    CustomerOrderGotDTO getCustomerOrderIncludeOrderLineByCustomerOrderId(long customerOrderId, Pageable orderLinePageRequest);

    CustomerOrderGotDTO getCustomerOrderIncludePurchaseOrderByCustomerOrderId(long customerOrderId, Pageable purchaseOrderPageRequest, Pageable orderLinePageRequest);

    List<CustomerOrderGotDTO> getCustomerOrderByCustomerIdAndCreateDatetimeRange(long customerId, Date startDate, Date endDate, Pageable customerOrderPageRequest, Pageable purchaseOrderPageRequest, Pageable orderLinePageRequest);

    void cancelCustomerOrderByCustomerOrderId(long customerOrderId);

    void assignWxpayPrepayId(long customerOrderId, String wxpayId);

    void updateWxpayStatus(long customerOrderId, boolean paid);

    void assignWxpayTransId(long customerOrderId, String transId);

    String getWxpayTransId(long customerOrderId);
}
