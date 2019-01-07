/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.service;

import com.wenqi.ordermanagement.constants.PurchaseStatus;
import com.wenqi.ordermanagement.dto.PurchaseOrderDTO;
import com.wenqi.ordermanagement.entity.PurchaseOrder;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface PurchaseOrderService {
    List<PurchaseOrder> createPurchaseOrderFromCustomerOrderId(long customerOrderId);

    PurchaseOrderDTO updatePurchaseOrderStatus(long purchaseOrderId, PurchaseStatus status);

    PurchaseOrder getPurchaseOrderById(long purchaseOrderId);

    PurchaseOrderDTO getPurchaseOrderByPurchaseOrderId(long purchaseOrderId, Pageable pageRequest);

    List<PurchaseOrderDTO> getPurchaseOrderByCustomerOrderId(long customerOrderId, Pageable purchaseOrderPageRequest, Pageable orderLinePageRequest);

    List<PurchaseOrderDTO> getPurchaseOrderByProviderIdAndCreateDatetimeRange(long providerId, Date startDate, Date endDate, Pageable purchaseOrderPageRequest, Pageable orderLinePageRequest);

    void cancelPurchaseOrderByPurchaseOrderId(long purchaseOrderId);
}
