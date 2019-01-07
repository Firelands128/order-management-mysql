/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.entity;

import com.wenqi.ordermanagement.constants.CustomerStatus;
import com.wenqi.ordermanagement.constants.PaymentMethod;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "customer_orders")
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long customerOrderId;

    @Column(name = "create_dtm")
    private Date createDatetime;

    @Column(name = "customer_id")
    private long customerId;

    @Column(name = "customer_order_status")
    private CustomerStatus customerOrderStatus;

    @Column(name = "sub_total")
    private BigDecimal subtotal;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "tax")
    private BigDecimal tax;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Column(name = "coupon")
    private BigDecimal coupon;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "shipping_address_line1")
    private String shippingAddressLine1;

    @Column(name = "shipping_address_line2")
    private String shippingAddressLine2;

    @Column(name = "shipping_address_city")
    private String shippingAddressCity;

    @Column(name = "shipping_address_province")
    private String shippingAddressProvince;

    @Column(name = "shipping_address_country")
    private String shippingAddressCountry;

    @Column(name = "shipping_address_phone")
    private String shippingAddressPhone;

    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "wxpay_prepay_id")
    private String wxpayPrepayId;

    @Column(name = "wxpay_paid")
    private boolean wxpayPaid;

    @Column(name = "wxpay_trans_id")
    private String wxpayTransId;

    public CustomerOrder() {
    }

    public long getCustomerOrderId() {
        return this.customerOrderId;
    }

    public void setCustomerOrderId(long customerOrderId) {
        this.customerOrderId = customerOrderId;
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public CustomerStatus getCustomerOrderStatus() {
        return customerOrderStatus;
    }

    public void setCustomerOrderStatus(CustomerStatus customerOrderStatus) {
        this.customerOrderStatus = customerOrderStatus;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getCoupon() {
        return coupon;
    }

    public void setCoupon(BigDecimal coupon) {
        this.coupon = coupon;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getShippingAddressLine1() {
        return shippingAddressLine1;
    }

    public void setShippingAddressLine1(String shippingAddressLine1) {
        this.shippingAddressLine1 = shippingAddressLine1;
    }

    public String getShippingAddressLine2() {
        return shippingAddressLine2;
    }

    public void setShippingAddressLine2(String shippingAddressLine2) {
        this.shippingAddressLine2 = shippingAddressLine2;
    }

    public String getShippingAddressCity() {
        return shippingAddressCity;
    }

    public void setShippingAddressCity(String shippingAddressCity) {
        this.shippingAddressCity = shippingAddressCity;
    }

    public String getShippingAddressProvince() {
        return shippingAddressProvince;
    }

    public void setShippingAddressProvince(String shippingAddressProvince) {
        this.shippingAddressProvince = shippingAddressProvince;
    }

    public String getShippingAddressCountry() {
        return shippingAddressCountry;
    }

    public void setShippingAddressCountry(String shippingAddressCountry) {
        this.shippingAddressCountry = shippingAddressCountry;
    }

    public String getShippingAddressPhone() {
        return shippingAddressPhone;
    }

    public void setShippingAddressPhone(String shippingAddressPhone) {
        this.shippingAddressPhone = shippingAddressPhone;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getWxpayPrepayId() {
        return wxpayPrepayId;
    }

    public void setWxpayPrepayId(String wxpayPrepayId) {
        this.wxpayPrepayId = wxpayPrepayId;
    }

    public boolean isWxpayPaid() {
        return wxpayPaid;
    }

    public void setWxpayPaid(boolean wxpayPaid) {
        this.wxpayPaid = wxpayPaid;
    }

    public String getWxpayTransId() {
        return wxpayTransId;
    }

    public void setWxpayTransId(String wxpayTransId) {
        this.wxpayTransId = wxpayTransId;
    }

    @Override
    public String toString() {
        return "CustomerOrder{" +
                "customerOrderId=" + customerOrderId +
                ", createDatetime=" + createDatetime +
                ", customerId=" + customerId +
                ", customerOrderStatus=" + customerOrderStatus +
                ", subtotal=" + subtotal +
                ", discount=" + discount +
                ", tax=" + tax +
                ", shippingFee=" + shippingFee +
                ", coupon=" + coupon +
                ", total=" + total +
                ", shippingAddressLine1='" + shippingAddressLine1 + '\'' +
                ", shippingAddressLine2='" + shippingAddressLine2 + '\'' +
                ", shippingAddressCity='" + shippingAddressCity + '\'' +
                ", shippingAddressProvince='" + shippingAddressProvince + '\'' +
                ", shippingAddressCountry='" + shippingAddressCountry + '\'' +
                ", shippingAddressPhone='" + shippingAddressPhone + '\'' +
                ", paymentMethod=" + paymentMethod +
                ", wxpayPrepayId='" + wxpayPrepayId + '\'' +
                ", wxpayPaid=" + wxpayPaid +
                ", wxpayTransId='" + wxpayTransId + '\'' +
                '}';
    }
}
