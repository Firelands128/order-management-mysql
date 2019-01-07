/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wenqi.ordermanagement.constants.CustomerStatus;
import com.wenqi.ordermanagement.constants.PaymentMethod;
import com.wenqi.ordermanagement.entity.CustomerOrder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@SuppressWarnings({"WeakerAccess"})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class CustomerOrderGotDTO {
    public Long customerOrderId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    public Date createDatetime;
    public Long customerId;
    public CustomerStatus customerOrderStatus;
    public PaymentMethod paymentMethod;
    public BigDecimal subtotal;
    public BigDecimal discount;
    public BigDecimal tax;
    public BigDecimal shippingFee;
    public BigDecimal coupon;
    public BigDecimal total;
    public ShippingAddressDTO shippingAddressDTO;
    public List<PurchaseOrderDTO> purchaseOrderDTOList;
    public List<OrderLineDTO> orderLineDTOList;

    public CustomerOrderGotDTO() {

    }

    public CustomerOrderGotDTO(CustomerOrder customerOrder) {
        this.customerOrderId = customerOrder.getCustomerOrderId();
        this.createDatetime = customerOrder.getCreateDatetime();
        this.customerId = customerOrder.getCustomerId();
        this.customerOrderStatus = customerOrder.getCustomerOrderStatus();
        this.paymentMethod = customerOrder.getPaymentMethod();
        this.subtotal = customerOrder.getSubtotal();
        this.discount = customerOrder.getDiscount();
        this.tax = customerOrder.getTax();
        this.shippingFee = customerOrder.getShippingFee();
        this.coupon = customerOrder.getCoupon();
        this.total = customerOrder.getTotal();
        ShippingAddressDTO shippingAddressDTO = new ShippingAddressDTO();
        shippingAddressDTO.addressLine1 = customerOrder.getShippingAddressLine1();
        shippingAddressDTO.addressLine2 = customerOrder.getShippingAddressLine2();
        shippingAddressDTO.city = customerOrder.getShippingAddressCity();
        shippingAddressDTO.province = customerOrder.getShippingAddressProvince();
        shippingAddressDTO.country = customerOrder.getShippingAddressCountry();
        shippingAddressDTO.phone = customerOrder.getShippingAddressPhone();
        this.shippingAddressDTO = shippingAddressDTO;
    }

    @Override
    public String toString() {
        return "CustomerOrderGotDTO{" +
                "customerOrderId=" + customerOrderId +
                ", createDatetime=" + createDatetime +
                ", customerId=" + customerId +
                ", customerOrderStatus=" + customerOrderStatus +
                ", paymentMethod=" + paymentMethod +
                ", subtotal=" + subtotal +
                ", discount=" + discount +
                ", tax=" + tax +
                ", shippingFee=" + shippingFee +
                ", coupon=" + coupon +
                ", total=" + total +
                ", shippingAddressDTO=" + shippingAddressDTO +
                ", purchaseOrderDTOList=" + purchaseOrderDTOList +
                ", orderLineDTOList=" + orderLineDTOList +
                '}';
    }
}
