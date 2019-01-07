/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wenqi.ordermanagement.entity.CartLine;

import java.math.BigDecimal;

@SuppressWarnings({"WeakerAccess"})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class CartLineDTO {
    public long cartId;
    public long productId;
    public BigDecimal unitPrice;
    public int quantity;
    public long customerId;

    public CartLineDTO() {

    }

    public CartLineDTO(CartLine cartLine) {
        this.cartId = cartLine.getCartId();
        this.productId = cartLine.getProductId();
        this.unitPrice = cartLine.getUnitPrice();
        this.quantity = cartLine.getQuantity();
        this.customerId = cartLine.getCustomerId();
    }

    @Override
    public String toString() {
        return "CartLineDTO{" +
                "cartId=" + cartId +
                ", productId=" + productId +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                ", customerId=" + customerId +
                '}';
    }
}
