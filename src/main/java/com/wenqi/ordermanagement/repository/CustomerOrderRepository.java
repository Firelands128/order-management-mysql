/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.repository;

import com.wenqi.ordermanagement.entity.CustomerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface CustomerOrderRepository extends PagingAndSortingRepository<CustomerOrder, Long> {

    CustomerOrder findByCustomerOrderId(long customerOrderId);

    Page<CustomerOrder> findByCustomerIdAndCreateDatetimeBetween(long customerId, Date startDate, Date endDate, Pageable pageRequest);

}

