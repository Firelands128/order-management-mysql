/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.service.impl;

import com.wenqi.ordermanagement.constants.*;
import com.wenqi.ordermanagement.dto.CustomerOrderDTO;
import com.wenqi.ordermanagement.dto.CustomerOrderGotDTO;
import com.wenqi.ordermanagement.dto.OrderLineDTO;
import com.wenqi.ordermanagement.dto.PurchaseOrderDTO;
import com.wenqi.ordermanagement.entity.CustomerOrder;
import com.wenqi.ordermanagement.entity.Job;
import com.wenqi.ordermanagement.entity.OrderLine;
import com.wenqi.ordermanagement.entity.PurchaseOrder;
import com.wenqi.ordermanagement.exception.ErrorCode;
import com.wenqi.ordermanagement.exception.MyException;
import com.wenqi.ordermanagement.repository.CustomerOrderRepository;
import com.wenqi.ordermanagement.repository.JobRepository;
import com.wenqi.ordermanagement.repository.PurchaseOrderRepository;
import com.wenqi.ordermanagement.service.CustomerOrderService;
import com.wenqi.ordermanagement.service.OrderLineService;
import com.wenqi.ordermanagement.service.PurchaseOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Transactional
public class CustomerOrderServiceImpl implements CustomerOrderService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final OrderLineService orderLineService;
    private final PurchaseOrderService purchaseOrderService;
    private final CustomerOrderRepository customerOrderRepository;
    private final JobRepository jobRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    public CustomerOrderServiceImpl(OrderLineService orderLineService,
                                    PurchaseOrderService purchaseOrderService,
                                    CustomerOrderRepository customerOrderRepository,
                                    JobRepository jobRepository,
                                    PurchaseOrderRepository purchaseOrderRepository) {
        this.orderLineService = orderLineService;
        this.purchaseOrderService = purchaseOrderService;
        this.customerOrderRepository = customerOrderRepository;
        this.jobRepository = jobRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Override
    public long createCustomerOrder(CustomerOrderDTO newCustomerOrderDTO) {
        logger.debug("Create customer order from customer order dto: " + newCustomerOrderDTO);
        CustomerOrder newCustomerOrder = extractNewCustomerOrderEntity(newCustomerOrderDTO);
        this.checkCustomerOrder(newCustomerOrder);
        List<OrderLine> newOrderLineList = extractNewOrderLineEntity(newCustomerOrderDTO);
        newOrderLineList.forEach(this::checkOrderLine);
        this.checkCustomerOrderConformOrderLines(newCustomerOrder, newOrderLineList);
        CustomerOrder addedCustomerOrder = customerOrderRepository.save(newCustomerOrder);
        List<OrderLine> addedOrderLineList = orderLineService.createOrderLineOfCustomerOrder(addedCustomerOrder.getCustomerOrderId(), newOrderLineList);
        Job newJob = new Job(JobType.CREATE_PURCHASE_ORDER, addedCustomerOrder.getCustomerOrderId());
        jobRepository.save(newJob);
        return addedCustomerOrder.getCustomerOrderId();
    }

    @Override
    public void updateCustomerOrderStatusToPaid(long customerOrderId) {
        logger.debug("Update customer order status to paid, customer order id is: " + customerOrderId);
        CustomerOrder customerOrder = getCustomerOrderById(customerOrderId);
        customerOrder.setCustomerOrderStatus(CustomerStatus.PAID);
        customerOrderRepository.save(customerOrder);
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<PurchaseOrderDTO> purchaseOrderDTOList = purchaseOrderService.getPurchaseOrderByCustomerOrderId(customerOrderId, pageRequest, pageRequest);
        List<Long> purchaseOrderIdList = new ArrayList<>();
        purchaseOrderDTOList.forEach(purchaseOrderDTO -> purchaseOrderIdList.add(purchaseOrderDTO.purchaseOrderId));
        for (long purchaseOrderId : purchaseOrderIdList) {
            purchaseOrderService.updatePurchaseOrderStatus(purchaseOrderId, PurchaseStatus.AWAITING_SHIPPING);
        }
    }

    @Override
    public CustomerOrder getCustomerOrderById(long customerOrderId) {
        if (customerOrderId == 0) {
            logger.error("customerOrderId cannot be 0.");
            throw new MyException(ErrorCode.BAD_REQUEST, "customerOrderId cannot be 0.");
        }
        CustomerOrder customerOrder = customerOrderRepository.findByCustomerOrderId(customerOrderId);
        if (customerOrder == null) {
            logger.error("Customer order not found, get customer order by customer order id failed.");
            throw new MyException(ErrorCode.NOT_FOUND, "Customer order not found, get customer order by customer order id failed.");
        }
        return customerOrder;
    }

    @Override
    public CustomerOrderGotDTO getCustomerOrderIncludeOrderLineByCustomerOrderId(long customerOrderId, Pageable orderLinePageRequest) {
        logger.debug("Get entire customer order by customer order id: " + customerOrderId);
        CustomerOrder customerOrder = getCustomerOrderById(customerOrderId);
        List<OrderLine> orderLineList = orderLineService.getOrderLineByCustomerOrderId(customerOrderId, orderLinePageRequest);
        List<OrderLineDTO> orderLineDTOList = new ArrayList<>(orderLineList.size());
        for (OrderLine orderLine : orderLineList) {
            orderLineDTOList.add(new OrderLineDTO(orderLine));
        }
        CustomerOrderGotDTO customerOrderGotDTO = new CustomerOrderGotDTO(customerOrder);
        customerOrderGotDTO.orderLineDTOList = orderLineDTOList;
        return customerOrderGotDTO;
    }

    @Override
    public CustomerOrderGotDTO getCustomerOrderIncludePurchaseOrderByCustomerOrderId(long customerOrderId, Pageable purchaseOrderPageRequest, Pageable orderLinePageRequest) {
        logger.debug("Get entire customer order by customer order id: " + customerOrderId);
        CustomerOrder customerOrder = getCustomerOrderById(customerOrderId);
        List<PurchaseOrderDTO> purchaseOrderList = purchaseOrderService
                .getPurchaseOrderByCustomerOrderId(customerOrderId, purchaseOrderPageRequest, orderLinePageRequest);
        CustomerOrderGotDTO customerOrderGotDTO = new CustomerOrderGotDTO(customerOrder);
        customerOrderGotDTO.purchaseOrderDTOList = purchaseOrderList;
        return customerOrderGotDTO;
    }

    @Override
    public List<CustomerOrderGotDTO> getCustomerOrderByCustomerIdAndCreateDatetimeRange(long customerId, Date startDate, Date endDate, Pageable customerOrderPageRequest, Pageable PurchaseOrderPageRequest, Pageable orderLinePageRequest) {
        logger.debug("Get entire customer order by customer id: " + customerId
                + " and create date time between start date: " + startDate + " and end date: " + endDate);
        List<CustomerOrder> customerOrderList = getCustomerOrderByCustomerIdAndCreateDatetimeRange(customerId, startDate, endDate, customerOrderPageRequest);
        List<CustomerOrderGotDTO> returnCustomerOrderDTOList = new ArrayList<>();
        for (CustomerOrder customerOrder : customerOrderList) {
            long customerOrderId = customerOrder.getCustomerOrderId();
            CustomerOrderGotDTO customerOrderGotDTO = this
                    .getCustomerOrderIncludePurchaseOrderByCustomerOrderId(customerOrderId, PurchaseOrderPageRequest, orderLinePageRequest);
            returnCustomerOrderDTOList.add(customerOrderGotDTO);
        }
        return returnCustomerOrderDTOList;
    }

    @Override
    public void cancelCustomerOrderByCustomerOrderId(long customerOrderId) {
        logger.debug("Cancel customer order by customer order id: " + customerOrderId);
        CustomerOrder customerOrder = getCustomerOrderById(customerOrderId);
        customerOrder.setCustomerOrderStatus(CustomerStatus.CANCELLED);
        customerOrderRepository.save(customerOrder);
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> orderLineList = orderLineService.getOrderLineByCustomerOrderId(customerOrderId, pageRequest);
        orderLineList.forEach(orderLine -> orderLine.setLineStatus(ItemStatus.CANCELLED));
        orderLineList.forEach(orderLineService::saveOrderLine);
        Set<Long> purchaseOrderIdSet = new HashSet<>();
        orderLineList.forEach(orderLine -> purchaseOrderIdSet.add(orderLine.getPurchaseOrderId()));
        for (long purchaseOrderId : purchaseOrderIdSet) {
            PurchaseOrder purchaseOrder = purchaseOrderService.getPurchaseOrderById(purchaseOrderId);
            purchaseOrder.setPurchaseOrderStatus(PurchaseStatus.CANCELLED);
            purchaseOrderRepository.save(purchaseOrder);
        }
    }

    @Override
    public void assignWxpayPrepayId(long customerOrderId, String wxpayPrepayId) {
        logger.debug("Assign weixin pay prepay order id into customer order, customer order id is: " + customerOrderId);
        CustomerOrder customerOrder = getCustomerOrderById(customerOrderId);
        customerOrder.setWxpayPrepayId(wxpayPrepayId);
        customerOrderRepository.save(customerOrder);
    }

    @Override
    public void updateWxpayStatus(long customerOrderId, boolean paid) {
        logger.debug("Update weixin pay status into customer order, customer order id is: " + customerOrderId);
        CustomerOrder customerOrder = getCustomerOrderById(customerOrderId);
        customerOrder.setWxpayPaid(paid);
        customerOrderRepository.save(customerOrder);
    }

    @Override
    public void assignWxpayTransId(long customerOrderId, String transId) {
        logger.debug("Assign weixin pay transaction id into customer order, customer order id is: " + customerOrderId);
        CustomerOrder customerOrder = getCustomerOrderById(customerOrderId);
        customerOrder.setWxpayTransId(transId);
        customerOrderRepository.save(customerOrder);
    }

    @Override
    public String getWxpayTransId(long customerOrderId) {
        logger.debug("Get weixin pay transaction id by customer order id: " + customerOrderId);
        return getCustomerOrderById(customerOrderId).getWxpayTransId();
    }

    /*
     * Extract customer order entity from customer order DTO
     */
    private CustomerOrder extractNewCustomerOrderEntity(CustomerOrderDTO customerOrderDTO) {
        CustomerOrder newCustomerOrder = new CustomerOrder();
        newCustomerOrder.setCreateDatetime(new Date());
        newCustomerOrder.setCustomerId(customerOrderDTO.customerId);
        newCustomerOrder.setCustomerOrderStatus(CustomerStatus.UNPAID);
        newCustomerOrder.setPaymentMethod(customerOrderDTO.paymentMethod);
        newCustomerOrder.setSubtotal(customerOrderDTO.subtotal.setScale(2, RoundingMode.HALF_UP));
        newCustomerOrder.setDiscount(customerOrderDTO.discount.setScale(2, RoundingMode.HALF_UP));
        newCustomerOrder.setTax(customerOrderDTO.tax.setScale(2, RoundingMode.HALF_UP));
        newCustomerOrder.setShippingFee(customerOrderDTO.shippingFee.setScale(2, RoundingMode.HALF_UP));
        newCustomerOrder.setCoupon(customerOrderDTO.coupon.setScale(2, RoundingMode.HALF_UP));
        newCustomerOrder.setTotal(customerOrderDTO.total.setScale(2, RoundingMode.HALF_UP));
        newCustomerOrder.setShippingAddressLine1(customerOrderDTO.shippingAddressDTO.addressLine1);
        newCustomerOrder.setShippingAddressLine2(customerOrderDTO.shippingAddressDTO.addressLine2);
        newCustomerOrder.setShippingAddressCity(customerOrderDTO.shippingAddressDTO.city);
        newCustomerOrder.setShippingAddressProvince(customerOrderDTO.shippingAddressDTO.province);
        newCustomerOrder.setShippingAddressCountry(customerOrderDTO.shippingAddressDTO.country);
        newCustomerOrder.setShippingAddressPhone(customerOrderDTO.shippingAddressDTO.phone);
        return newCustomerOrder;
    }

    /*
     * Extract list of order line entity from customer order DTO
     */
    private List<OrderLine> extractNewOrderLineEntity(CustomerOrderDTO customerOrderDTO) {
        List<OrderLine> returnList = new ArrayList<>();
        for (OrderLineDTO orderLineDTO : customerOrderDTO.orderLineDTOList) {
            OrderLine newOrderLine = new OrderLine();
            newOrderLine.setProviderId(orderLineDTO.providerId);
            newOrderLine.setProductId(orderLineDTO.productId);
            newOrderLine.setUnitPrice(orderLineDTO.unitPrice.setScale(2, RoundingMode.HALF_UP));
            newOrderLine.setUnitCost(orderLineDTO.unitCost.setScale(2, RoundingMode.HALF_UP));
            newOrderLine.setQuantity(orderLineDTO.quantity);
            newOrderLine.setCustomerSubtotal(orderLineDTO.customerSubtotal.setScale(2, RoundingMode.HALF_UP));
            newOrderLine.setPurchaseSubtotal(orderLineDTO.purchaseSubtotal.setScale(2, RoundingMode.HALF_UP));
            newOrderLine.setDesc(orderLineDTO.desc);
            newOrderLine.setLineStatus(ItemStatus.NORMAL);
            returnList.add(newOrderLine);
        }
        return returnList;
    }

    /*
     * Check customer order valid
     */
    private void checkCustomerOrder(CustomerOrder customerOrder) {
        BigDecimal subtotal = customerOrder.getSubtotal();
        BigDecimal discount = customerOrder.getDiscount();
        BigDecimal tax = customerOrder.getTax();
        BigDecimal shippingFee = customerOrder.getShippingFee();
        BigDecimal coupon = customerOrder.getCoupon();
        BigDecimal total = customerOrder.getTotal();
        if (!tax.equals(subtotal.subtract(discount).multiply(OrderManagementConstants.taxRate).setScale(2, RoundingMode.HALF_UP))) {
            logger.error("Customer order tax amount is incorrect.");
            throw new MyException(ErrorCode.BAD_REQUEST, "Customer order tax amount is incorrect.");
        }
        if (!total.equals(subtotal.subtract(discount).add(tax).add(shippingFee).subtract(coupon).setScale(2, RoundingMode.HALF_UP))) {
            logger.error("Customer order total amount is incorrect.");
            throw new MyException(ErrorCode.BAD_REQUEST, "Customer order total amount is incorrect.");
        }
    }

    /*
     * Check order line valid
     */
    private void checkOrderLine(OrderLine orderLine) {
        int quantity = orderLine.getQuantity();
        BigDecimal unitPrice = orderLine.getUnitPrice();
        BigDecimal unitCost = orderLine.getUnitCost();
        BigDecimal customerSubtotal = orderLine.getCustomerSubtotal();
        BigDecimal purchaseSubtotal = orderLine.getPurchaseSubtotal();
        if (!customerSubtotal.equals(unitPrice.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP))) {
            logger.error("Customer subtotal of order line is incorrect.");
            throw new MyException(ErrorCode.BAD_REQUEST, "Customer subtotal of order line is incorrect.");
        }
        if (!purchaseSubtotal.equals(unitCost.multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP))) {
            logger.error("Purchase subtotal of order line is incorrect.");
            throw new MyException(ErrorCode.BAD_REQUEST, "Purchase subtotal of order line is incorrect.");
        }
    }

    /*
     * Check customer order subtotal conform to order lines subtotal
     */
    private void checkCustomerOrderConformOrderLines(CustomerOrder customerOrder, List<OrderLine> orderLineList) {
        BigDecimal customerOrderSubtotal = customerOrder.getSubtotal();
        BigDecimal orderLineSubtotalSum = new BigDecimal(0);
        for (OrderLine orderLine : orderLineList) {
            orderLineSubtotalSum = orderLineSubtotalSum.add(orderLine.getCustomerSubtotal());
        }
        if (!customerOrderSubtotal.equals(orderLineSubtotalSum)) {
            logger.error("Customer order subtotal is not equal to sum of order lines subtotal.");
            throw new MyException(ErrorCode.BAD_REQUEST, "Customer order subtotal is not equal to sum of order lines subtotal.");
        }
    }

    private List<CustomerOrder> getCustomerOrderByCustomerIdAndCreateDatetimeRange(long customerId,
                                                                                   Date startDate, Date endDate,
                                                                                   Pageable pageRequest) {
        if (customerId == 0) {
            logger.error("customerId cannot be 0.");
            throw new MyException(ErrorCode.BAD_REQUEST, "customerId cannot be 0.");
        }
        Page<CustomerOrder> found = customerOrderRepository.findByCustomerIdAndCreateDatetimeBetween(customerId, startDate, endDate, pageRequest);
        if (!found.iterator().hasNext()) {
            logger.error("Customer order not found, get customer order by create date failed.");
            throw new MyException(ErrorCode.NOT_FOUND, "Customer order not found, get customer order by create date failed.");
        }
        return found.getContent();
    }
}
