/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wenqi.ordermanagement.constants.PurchaseStatus;
import com.wenqi.ordermanagement.entity.OrderLine;
import com.wenqi.ordermanagement.entity.PurchaseOrder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings({"WeakerAccess"})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PurchaseOrderDTO {
    public Long purchaseOrderId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    public Date createDatetime;
    public Long customerOrderId;
    public Long customerId;
    public Long providerId;
    public PurchaseStatus purchaseOrderStatus;
    public BigDecimal subtotal;
    public BigDecimal tax;
    public BigDecimal shippingFee;
    public BigDecimal total;
    public ShippingAddressDTO shippingAddressDTO;
    public List<OrderLineDTO> orderLineDTOList;

    public PurchaseOrderDTO() {

    }

    public PurchaseOrderDTO(PurchaseOrder purchaseOrder, List<OrderLine> orderLineList) {
        this.purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        this.createDatetime = purchaseOrder.getCreateDatetime();
        this.customerOrderId = purchaseOrder.getPurchaseOrderId();
        this.customerId = purchaseOrder.getCustomerId();
        this.providerId = purchaseOrder.getProviderId();
        this.purchaseOrderStatus = purchaseOrder.getPurchaseOrderStatus();
        this.subtotal = purchaseOrder.getSubtotal();
        this.tax = purchaseOrder.getTax();
        this.shippingFee = purchaseOrder.getShippingFee();
        this.total = purchaseOrder.getTotal();
        ShippingAddressDTO shippingAddressDTO = new ShippingAddressDTO();
        shippingAddressDTO.addressLine1 = purchaseOrder.getShippingAddressLine1();
        shippingAddressDTO.addressLine2 = purchaseOrder.getShippingAddressLine2();
        shippingAddressDTO.city = purchaseOrder.getShippingAddressCity();
        shippingAddressDTO.province = purchaseOrder.getShippingAddressProvince();
        shippingAddressDTO.country = purchaseOrder.getShippingAddressCountry();
        shippingAddressDTO.phone = purchaseOrder.getShippingAddressPhone();
        this.shippingAddressDTO = shippingAddressDTO;
        List<OrderLineDTO> orderLineDTOList = new ArrayList<>();
        orderLineList.forEach(orderLine -> orderLineDTOList.add(new OrderLineDTO(orderLine)));
        this.orderLineDTOList = orderLineDTOList;
    }

    @Override
    public String toString() {
        return "PurchaseOrderDTO{" +
                "purchaseOrderId=" + purchaseOrderId +
                ", createDatetime=" + createDatetime +
                ", customerOrderId=" + customerOrderId +
                ", customerId=" + customerId +
                ", providerId=" + providerId +
                ", purchaseOrderStatus=" + purchaseOrderStatus +
                ", subtotal=" + subtotal +
                ", tax=" + tax +
                ", shippingFee=" + shippingFee +
                ", total=" + total +
                ", shippingAddressDTO=" + shippingAddressDTO +
                ", orderLineDTOList=" + orderLineDTOList +
                '}';
    }
}
