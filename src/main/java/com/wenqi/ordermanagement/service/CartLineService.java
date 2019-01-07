/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.service;

import com.wenqi.ordermanagement.dto.CartLineDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartLineService {
    CartLineDTO createCartLine(CartLineDTO cartLineDTO);

    List<CartLineDTO> getCartLineByCustomerId(long customerId, Pageable pageRequest);

    CartLineDTO updateProductQuantityByCustomerIdAndProductId(long customerId, long productId, int quantity);

    void deleteCartLineByCustomerIdAndProductId(long customerId, long productId);
}
