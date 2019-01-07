/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.repository;

import com.wenqi.ordermanagement.entity.CartLine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CartLineRepository extends PagingAndSortingRepository<CartLine, Long> {
    CartLine findByCartId(long cartId);

    Page<CartLine> findByCustomerId(long customerId, Pageable pageRequest);

    CartLine findByCustomerIdAndProductId(long customerId, long productId);
}
