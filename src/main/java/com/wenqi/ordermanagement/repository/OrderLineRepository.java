/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.repository;

import com.wenqi.ordermanagement.entity.OrderLine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderLineRepository extends PagingAndSortingRepository<OrderLine, Long> {
    OrderLine findByLineId(long lineId);

    Page<OrderLine> findByCustomerOrderId(long customerOrderId, Pageable pageRequest);

    Page<OrderLine> findByPurchaseOrderId(long purchaseOrderId, Pageable pageRequest);
}
