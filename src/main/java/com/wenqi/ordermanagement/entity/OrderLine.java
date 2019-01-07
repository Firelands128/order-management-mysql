/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.entity;

import com.wenqi.ordermanagement.constants.ItemStatus;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_line")
public class OrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private long lineId;

    @Column(name = "provider_id")
    private long providerId;

    @Column(name = "product_id")
    private long productId;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "unit_cost")
    private BigDecimal unitCost;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "return_quantity")
    private int returnQuantity;

    @Column(name = "customer_subtotal")
    private BigDecimal customerSubtotal;

    @Column(name = "purchase_subtotal")
    private BigDecimal purchaseSubtotal;

    @Column(name = "description")
    private String desc;

    @Column(name = "line_status")
    private ItemStatus lineStatus;

    @Column(name = "customer_order_id")
    private long customerOrderId;

    @Column(name = "purchase_order_id")
    private long purchaseOrderId;

    public OrderLine() {

    }

    public long getLineId() {
        return this.lineId;
    }

    public void setLineId(long lineId) {
        this.lineId = lineId;
    }

    public long getProviderId() {
        return this.providerId;
    }

    public void setProviderId(long providerId) {
        this.providerId = providerId;
    }

    public long getProductId() {
        return this.productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReturnQuantity() {
        return returnQuantity;
    }

    public void setReturnQuantity(int returnQuantity) {
        this.returnQuantity = returnQuantity;
    }

    public BigDecimal getCustomerSubtotal() {
        return this.customerSubtotal;
    }

    public void setCustomerSubtotal(BigDecimal customerSubtotal) {
        this.customerSubtotal = customerSubtotal;
    }

    public BigDecimal getPurchaseSubtotal() {
        return purchaseSubtotal;
    }

    public void setPurchaseSubtotal(BigDecimal purchaseSubtotal) {
        this.purchaseSubtotal = purchaseSubtotal;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ItemStatus getLineStatus() {
        return this.lineStatus;
    }

    public void setLineStatus(ItemStatus lineStatus) {
        this.lineStatus = lineStatus;
    }

    public long getCustomerOrderId() {
        return this.customerOrderId;
    }

    public void setCustomerOrderId(long customerOrderId) {
        this.customerOrderId = customerOrderId;
    }

    public long getPurchaseOrderId() {
        return this.purchaseOrderId;
    }

    public void setPurchaseOrderId(long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    @Override
    public String toString() {
        return "OrderLine{" +
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