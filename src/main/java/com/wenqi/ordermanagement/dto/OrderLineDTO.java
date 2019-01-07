/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wenqi.ordermanagement.constants.ItemStatus;
import com.wenqi.ordermanagement.entity.OrderLine;

import java.math.BigDecimal;

@SuppressWarnings({"WeakerAccess"})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class OrderLineDTO {
    public Long lineId;
    public Long providerId;
    public Long productId;
    public BigDecimal unitPrice;
    public BigDecimal unitCost;
    public Integer quantity;
    public Integer returnQuantity;
    public BigDecimal customerSubtotal;
    public BigDecimal purchaseSubtotal;
    public String desc;
    public ItemStatus lineStatus;
    public Long customerOrderId;
    public Long purchaseOrderId;

    public OrderLineDTO() {

    }

    public OrderLineDTO(OrderLine orderLine) {
        this.lineId = orderLine.getLineId();
        this.providerId = orderLine.getProviderId();
        this.productId = orderLine.getProductId();
        this.unitPrice = orderLine.getUnitPrice();
        this.unitCost = orderLine.getUnitCost();
        this.quantity = orderLine.getQuantity();
        this.returnQuantity = orderLine.getReturnQuantity();
        this.customerSubtotal = orderLine.getCustomerSubtotal();
        this.purchaseSubtotal = orderLine.getPurchaseSubtotal();
        this.desc = orderLine.getDesc();
        this.lineStatus = orderLine.getLineStatus();
        this.customerOrderId = orderLine.getCustomerOrderId();
        this.purchaseOrderId = orderLine.getPurchaseOrderId();
    }

    @Override
    public String toString() {
        return "OrderLineDTO{" +
                "lineId=" + lineId +
                ", providerId=" + providerId +
                ", productId=" + productId +
                ", unitPrice=" + unitPrice +
                ", unitCost=" + unitCost +
                ", quantity=" + quantity +
                ", returnQuantity=" + returnQuantity +
                ", customerSubtotal=" + customerSubtotal +
                ", purchaseSubtotal=" + purchaseSubtotal +
                ", desc='" + desc + '\'' +
                ", lineStatus=" + lineStatus +
                ", customerOrderId=" + customerOrderId +
                ", purchaseOrderId=" + purchaseOrderId +
                '}';
    }
}
