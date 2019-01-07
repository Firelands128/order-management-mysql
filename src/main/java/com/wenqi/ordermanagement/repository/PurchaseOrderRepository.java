/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.repository;

import com.wenqi.ordermanagement.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface PurchaseOrderRepository extends PagingAndSortingRepository<PurchaseOrder, Long> {
    PurchaseOrder findByPurchaseOrderId(long purchaseOrderId);

    Page<PurchaseOrder> findByCustomerOrderId(long customerOrderId, Pageable pageRequest);

    Page<PurchaseOrder> findByProviderIdAndCreateDatetimeBetween(long providerId, Date startDate, Date endDate, Pageable pageRequest);
}
