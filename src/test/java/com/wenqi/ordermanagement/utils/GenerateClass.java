/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.utils;

import com.wenqi.ordermanagement.constants.*;
import com.wenqi.ordermanagement.dto.*;
import com.wenqi.ordermanagement.entity.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

public class GenerateClass {
    public static Date generateDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2018-07-17 12:00:00.000", new ParsePosition(0));
    }

    public static Date generateStartDate() {
        return new SimpleDateFormat("yyyy-MM-dd").parse("2018-07-16", new ParsePosition(0));
    }

    public static Date generateEndDate() {
        return new SimpleDateFormat("yyyy-MM-dd").parse("2018-07-18", new ParsePosition(0));
    }

    public static OrderLine generateNewOrderLine() {
        OrderLine newOrderLine = new OrderLine();
        newOrderLine.setLineId(1);
        newOrderLine.setProviderId(1);
        newOrderLine.setProductId(1);
        newOrderLine.setUnitPrice(new BigDecimal(1));
        newOrderLine.setUnitCost(new BigDecimal(1));
        newOrderLine.setQuantity(1);
        newOrderLine.setReturnQuantity(1);
        newOrderLine.setCustomerSubtotal(newOrderLine.getUnitPrice().multiply(new BigDecimal(newOrderLine.getQuantity())).setScale(2, RoundingMode.HALF_UP));
        newOrderLine.setPurchaseSubtotal(newOrderLine.getUnitCost().multiply(new BigDecimal(newOrderLine.getQuantity())).setScale(2, RoundingMode.HALF_UP));
        newOrderLine.setDesc("Description");
        newOrderLine.setLineStatus(ItemStatus.NORMAL);
        newOrderLine.setCustomerOrderId(1);
        newOrderLine.setPurchaseOrderId(1);
        return newOrderLine;
    }

    public static CustomerOrder generateNewCustomerOrder() {
        CustomerOrder newCustomerOrder = new CustomerOrder();
        newCustomerOrder.setCustomerOrderId(1);
        newCustomerOrder.setCreateDatetime(generateDate());
        newCustomerOrder.setCustomerId(1);
        newCustomerOrder.setCustomerOrderStatus(CustomerStatus.UNPAID);
        newCustomerOrder.setSubtotal(new BigDecimal(1));
        newCustomerOrder.setDiscount(new BigDecimal(0));
        newCustomerOrder.setTax(newCustomerOrder.getSubtotal().multiply(OrderManagementConstants.taxRate).setScale(2, RoundingMode.HALF_UP));
        newCustomerOrder.setShippingFee(new BigDecimal(0));
        newCustomerOrder.setCoupon(new BigDecimal(0));
        newCustomerOrder.setTotal(newCustomerOrder.getSubtotal().subtract(newCustomerOrder.getDiscount()).add(newCustomerOrder.getTax()).add(newCustomerOrder.getShippingFee()).subtract(newCustomerOrder.getCoupon()));
        newCustomerOrder.setShippingAddressLine1("shippingAddressLine1");
        newCustomerOrder.setShippingAddressLine2("shippingAddressLine2");
        newCustomerOrder.setShippingAddressCity("shippingAddressCity");
        newCustomerOrder.setShippingAddressProvince("shippingAddressProvince");
        newCustomerOrder.setShippingAddressCountry("shippingAddressCountry");
        newCustomerOrder.setShippingAddressPhone("shippingAddressPhone");
        newCustomerOrder.setPaymentMethod(PaymentMethod.WECHATPAY);
        newCustomerOrder.setWxpayPrepayId("123");
        newCustomerOrder.setWxpayPaid(true);
        newCustomerOrder.setWxpayTransId("123");
        return newCustomerOrder;
    }

    public static PurchaseOrder generateNewPurchaseOrder() {
        PurchaseOrder newPurchaseOrder = new PurchaseOrder();
        newPurchaseOrder.setPurchaseOrderId(1);
        newPurchaseOrder.setCreateDatetime(generateDate());
        newPurchaseOrder.setCustomerOrderId(1);
        newPurchaseOrder.setCustomerId(1);
        newPurchaseOrder.setProviderId(1);
        newPurchaseOrder.setPurchaseOrderStatus(PurchaseStatus.AWAITING_PAYMENT);
        newPurchaseOrder.setSubtotal(new BigDecimal(1));
        newPurchaseOrder.setTax(newPurchaseOrder.getSubtotal().multiply(OrderManagementConstants.taxRate).setScale(2, RoundingMode.HALF_UP));
        newPurchaseOrder.setShippingFee(new BigDecimal(0));
        newPurchaseOrder.setTotal(newPurchaseOrder.getSubtotal().add(newPurchaseOrder.getTax()).add(newPurchaseOrder.getShippingFee()));
        newPurchaseOrder.setShippingAddressLine1("shippingAddressLine1");
        newPurchaseOrder.setShippingAddressLine2("shippingAddressLine2");
        newPurchaseOrder.setShippingAddressCity("shippingAddressCity");
        newPurchaseOrder.setShippingAddressProvince("shippingAddressProvince");
        newPurchaseOrder.setShippingAddressCountry("shippingAddressCountry");
        newPurchaseOrder.setShippingAddressPhone("shippingAddressPhone");
        return newPurchaseOrder;
    }

    public static OrderLineDTO generateNewOrderLineDTO() {
        OrderLine orderLine = generateNewOrderLine();
        return new OrderLineDTO(orderLine);
    }

    public static CustomerOrderDTO generateNewCustomerOrderDTO() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        OrderLine orderLine = generateNewOrderLine();
        return new CustomerOrderDTO(customerOrder, Collections.singletonList(orderLine));
    }

    public static CustomerOrderGotDTO generateNewCustomerOrderIncludePurchaseOrderGotDTO() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        OrderLine orderLine = generateNewOrderLine();
        PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO(purchaseOrder, Collections.singletonList(orderLine));
        CustomerOrderGotDTO customerOrderGotDTO = new CustomerOrderGotDTO(customerOrder);
        customerOrderGotDTO.purchaseOrderDTOList = Collections.singletonList(purchaseOrderDTO);
        return customerOrderGotDTO;
    }

    public static CustomerOrderGotDTO generateNewCustomerOrderIncludeOrderLineGotDTO() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        OrderLine orderLine = generateNewOrderLine();
        CustomerOrderGotDTO customerOrderGotDTO = new CustomerOrderGotDTO(customerOrder);
        customerOrderGotDTO.orderLineDTOList = Collections.singletonList(new OrderLineDTO(orderLine));
        return customerOrderGotDTO;
    }

    public static PurchaseOrderDTO generateNewPurchaseOrderDTO() {
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        OrderLine orderLine = generateNewOrderLine();
        return new PurchaseOrderDTO(purchaseOrder, Collections.singletonList(orderLine));
    }

    public static PurchaseOrderDTO generateUpdatedPurchaseOrderStatusDTO() {
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        OrderLine orderLine = generateNewOrderLine();
        purchaseOrder.setPurchaseOrderStatus(PurchaseStatus.AWAITING_SHIPPING);
        orderLine.setLineStatus(ItemStatus.NORMAL);
        return new PurchaseOrderDTO(purchaseOrder, Collections.singletonList(orderLine));
    }

    public static CartLine generateNewCartLine() {
        CartLine cartLine = new CartLine();
        cartLine.setCartId(1);
        cartLine.setProductId(1);
        cartLine.setUnitPrice(new BigDecimal(1));
        cartLine.setQuantity(1);
        cartLine.setCustomerId(1);
        return cartLine;
    }

    public static CartLineDTO generateNewCartLineDTO() {
        return new CartLineDTO(generateNewCartLine());
    }

    public static CartLineDTO generateUpdatedCartLineDTO() {
        CartLineDTO cartLineDTO = generateNewCartLineDTO();
        cartLineDTO.quantity += 1;
        return cartLineDTO;
    }

    public static Job generateNewJob() {
        Job job = new Job(JobType.CREATE_PURCHASE_ORDER, 1);
        job.setJobId(1);
        return job;
    }

}
