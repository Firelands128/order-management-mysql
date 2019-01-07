/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.service.impl;

import com.wenqi.ordermanagement.constants.CustomerStatus;
import com.wenqi.ordermanagement.constants.ItemStatus;
import com.wenqi.ordermanagement.constants.OrderManagementConstants;
import com.wenqi.ordermanagement.constants.PurchaseStatus;
import com.wenqi.ordermanagement.dto.PurchaseOrderDTO;
import com.wenqi.ordermanagement.entity.CustomerOrder;
import com.wenqi.ordermanagement.entity.OrderLine;
import com.wenqi.ordermanagement.entity.PurchaseOrder;
import com.wenqi.ordermanagement.exception.ErrorCode;
import com.wenqi.ordermanagement.exception.MyException;
import com.wenqi.ordermanagement.repository.CustomerOrderRepository;
import com.wenqi.ordermanagement.repository.PurchaseOrderRepository;
import com.wenqi.ordermanagement.service.OrderLineService;
import com.wenqi.ordermanagement.service.PurchaseOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.*;


@Service
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final OrderLineService orderLineService;
    private final CustomerOrderRepository customerOrderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    public PurchaseOrderServiceImpl(OrderLineService orderLineService,
                                    CustomerOrderRepository customerOrderRepository,
                                    PurchaseOrderRepository purchaseOrderRepository) {
        this.orderLineService = orderLineService;
        this.customerOrderRepository = customerOrderRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Override
    public List<PurchaseOrder> createPurchaseOrderFromCustomerOrderId(long customerOrderId) {
        CustomerOrder customerOrder = getCustomerOrderById(customerOrderId);
        PageRequest pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> orderLineList = orderLineService.getOrderLineByCustomerOrderId(customerOrderId, pageRequest);
        logger.debug("Start to create purchase order from customer order, customer order id is: " + customerOrder
                + " and list of order line: " + orderLineList + ".");
        List<PurchaseOrder> purchaseOrderList = new ArrayList<>();
        List<Long> existedProvider = new ArrayList<>();
        purchaseOrderList.forEach(purchaseOrder -> existedProvider.add(purchaseOrder.getProviderId()));
        for (OrderLine orderLine : orderLineList) {
            logger.debug("For each line, allocate order line to a purchase order with same provider.");
            if (existedProvider.contains(orderLine.getProviderId())) {
                for (PurchaseOrder purchaseOrder : purchaseOrderList) {
                    if (purchaseOrder.getProviderId() == orderLine.getProviderId()) {
                        purchaseOrder.setSubtotal(purchaseOrder.getSubtotal().add(orderLine.getPurchaseSubtotal()));
                        purchaseOrder.setTax(purchaseOrder.getSubtotal().multiply(OrderManagementConstants.taxRate).setScale(2, RoundingMode.HALF_UP));
                        purchaseOrder.setTotal(purchaseOrder.getSubtotal().add(purchaseOrder.getTax()));
                        logger.debug("Update purchase order in repository.");
                        purchaseOrderRepository.save(purchaseOrder);
                        logger.debug("Assign purchase order id to this order line.");
                        orderLine.setPurchaseOrderId(purchaseOrder.getPurchaseOrderId());
                        logger.debug("Update order line status in repository.");
                        orderLineService.saveOrderLine(orderLine);
                    }
                }
            } else {
                PurchaseOrder newPurchaseOrder = new PurchaseOrder();
                newPurchaseOrder.setCreateDatetime(new Date());
                newPurchaseOrder.setCustomerOrderId(customerOrder.getCustomerOrderId());
                newPurchaseOrder.setCustomerId(customerOrder.getCustomerId());
                newPurchaseOrder.setProviderId(orderLine.getProviderId());
                newPurchaseOrder.setPurchaseOrderStatus(PurchaseStatus.AWAITING_PAYMENT);
                newPurchaseOrder.setSubtotal(orderLine.getPurchaseSubtotal());
                newPurchaseOrder.setTax(newPurchaseOrder.getSubtotal().multiply(OrderManagementConstants.taxRate).setScale(2, RoundingMode.HALF_UP));
                newPurchaseOrder.setShippingFee(customerOrder.getShippingFee());//TODO calculate shipping fee
                newPurchaseOrder.setTotal(newPurchaseOrder.getSubtotal().add(newPurchaseOrder.getTax()));
                newPurchaseOrder.setShippingAddressLine1(customerOrder.getShippingAddressLine1());
                newPurchaseOrder.setShippingAddressLine2(customerOrder.getShippingAddressLine2());
                newPurchaseOrder.setShippingAddressCity(customerOrder.getShippingAddressCity());
                newPurchaseOrder.setShippingAddressProvince(customerOrder.getShippingAddressProvince());
                newPurchaseOrder.setShippingAddressCountry(customerOrder.getShippingAddressCountry());
                newPurchaseOrder.setShippingAddressPhone(customerOrder.getShippingAddressPhone());
                logger.debug("Create purchase order in repository.");
                PurchaseOrder addedPurchaseOrder = purchaseOrderRepository.save(newPurchaseOrder);
                logger.debug("Assign purchase order id to this order line.");
                orderLine.setPurchaseOrderId(addedPurchaseOrder.getPurchaseOrderId());
                logger.debug("Update order line status in repository.");
                orderLineService.saveOrderLine(orderLine);
                purchaseOrderList.add(addedPurchaseOrder);
                existedProvider.add(addedPurchaseOrder.getProviderId());
            }
        }
        return purchaseOrderList;
    }

    @Override
    public PurchaseOrderDTO updatePurchaseOrderStatus(long purchaseOrderId, PurchaseStatus status) {
        logger.debug("Update purchase order status to " + status + ", purchase order is: " + purchaseOrderId);
        PurchaseOrder purchaseOrder = getPurchaseOrderById(purchaseOrderId);
        purchaseOrder.setPurchaseOrderStatus(status);
        purchaseOrderRepository.save(purchaseOrder);
        PurchaseOrder updatedPurchaseOrder = getPurchaseOrderById(purchaseOrderId);
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> updatedOrderLineList = orderLineService.getOrderLineByPurchaseOrderId(purchaseOrderId, pageRequest);
        return new PurchaseOrderDTO(updatedPurchaseOrder, updatedOrderLineList);
    }

    @Override
    public PurchaseOrder getPurchaseOrderById(long purchaseOrderId) {
        if (purchaseOrderId == 0) {
            logger.error("PurchaseOrderId cannot be 0, get purchase order by purchaseOrderId failed.");
            throw new MyException(ErrorCode.BAD_REQUEST,
                    "PurchaseOrderId cannot be 0, get purchase order by purchaseOrderId failed.");
        }
        PurchaseOrder found = purchaseOrderRepository.findByPurchaseOrderId(purchaseOrderId);
        if (found == null) {
            logger.error("Purchase order not found, get purchase order failed.");
            throw new MyException(ErrorCode.NOT_FOUND, "Purchase order not found, get purchase order failed.");
        }
        return found;
    }

    @Override
    public PurchaseOrderDTO getPurchaseOrderByPurchaseOrderId(long purchaseOrderId, Pageable pageRequest) {
        logger.debug("Get purchase order by purchase order id: " + purchaseOrderId);
        PurchaseOrder purchaseOrder = getPurchaseOrderById(purchaseOrderId);
        List<OrderLine> orderLineList = orderLineService.getOrderLineByPurchaseOrderId(purchaseOrderId, pageRequest);
        return new PurchaseOrderDTO(purchaseOrder, orderLineList);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public List<PurchaseOrderDTO> getPurchaseOrderByCustomerOrderId(long customerOrderId, Pageable purchaseOrderPageRequest, Pageable orderLinePageRequest) {
        logger.debug("Get purchase order by customer order id: " + customerOrderId);

        if (customerOrderId == 0) {
            logger.error("CustomerOrderId cannot be 0, get purchase order by customer order id failed.");
            throw new MyException(ErrorCode.BAD_REQUEST,
                    "CustomerOrderId cannot be 0, get purchase order by customer order id failed.");
        }
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findByCustomerOrderId(customerOrderId, purchaseOrderPageRequest).getContent();
        if (!purchaseOrderList.iterator().hasNext()) {
            logger.error("Purchase order not found, get purchase order failed.");
            throw new MyException(ErrorCode.NOT_FOUND, "Purchase order not found, get purchase order failed.");
        }

        List<PurchaseOrderDTO> returnPurchaseOrderDTOList = new ArrayList<>();
        for (PurchaseOrder purchaseOrder : purchaseOrderList) {
            long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
            List<OrderLine> orderLineList = orderLineService.getOrderLineByPurchaseOrderId(purchaseOrderId, orderLinePageRequest);
            returnPurchaseOrderDTOList.add(new PurchaseOrderDTO(purchaseOrder, orderLineList));
        }
        return returnPurchaseOrderDTOList;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public List<PurchaseOrderDTO> getPurchaseOrderByProviderIdAndCreateDatetimeRange(long providerId,
                                                                                     Date startDate,
                                                                                     Date endDate,
                                                                                     Pageable purchaseOrderPageRequest,
                                                                                     Pageable orderLinePageRequest) {
        logger.debug("Get entire purchase order by provider id: " + providerId
                + " and create date time between start date: " + startDate + " and end date: " + endDate);

        if (providerId == 0) {
            logger.error("ProviderId cannot be 0, get purchase order by providerId failed.");
            throw new MyException(ErrorCode.BAD_REQUEST,
                    "ProviderId cannot be 0, get purchase order by providerId failed.");
        }
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findByProviderIdAndCreateDatetimeBetween(providerId,
                startDate,
                endDate,
                purchaseOrderPageRequest)
                .getContent();
        if (!purchaseOrderList.iterator().hasNext()) {
            logger.error("Purchase order not found, get purchase order by providerId and create datetime range failed.");
            throw new MyException(ErrorCode.NOT_FOUND,
                    "Purchase order not found, get purchase order by providerId and create datetime range failed.");
        }

        List<PurchaseOrderDTO> returnPurchaseOrderDTOList = new ArrayList<>();
        for (PurchaseOrder purchaseOrder : purchaseOrderList) {
            long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
            List<OrderLine> orderLineList = orderLineService.getOrderLineByPurchaseOrderId(purchaseOrderId, orderLinePageRequest);
            returnPurchaseOrderDTOList.add(new PurchaseOrderDTO(purchaseOrder, orderLineList));
        }
        return returnPurchaseOrderDTOList;
    }

    @Override
    public void cancelPurchaseOrderByPurchaseOrderId(long purchaseOrderId) {
        logger.debug("Cancel purchase order by purchase order id: " + purchaseOrderId);
        PurchaseOrder purchaseOrder = getPurchaseOrderById(purchaseOrderId);
        purchaseOrder.setPurchaseOrderStatus(PurchaseStatus.CANCELLED);
        purchaseOrderRepository.save(purchaseOrder);
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> orderLineList = orderLineService.getOrderLineByPurchaseOrderId(purchaseOrderId, pageRequest);
        orderLineList.forEach(orderLine -> orderLine.setLineStatus(ItemStatus.CANCELLED));
        orderLineList.forEach(orderLineService::saveOrderLine);
        Set<Long> customerOrderIdSet = new HashSet<>();
        orderLineList.forEach(orderLine -> customerOrderIdSet.add(orderLine.getCustomerOrderId()));
        for (long customerOrderId : customerOrderIdSet) {
            CustomerOrder customerOrder = getCustomerOrderById(customerOrderId);
            customerOrder.setCustomerOrderStatus(CustomerStatus.CANCELLED);
            customerOrderRepository.save(customerOrder);
        }
    }

    private CustomerOrder getCustomerOrderById(long customerOrderId) {
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

}
