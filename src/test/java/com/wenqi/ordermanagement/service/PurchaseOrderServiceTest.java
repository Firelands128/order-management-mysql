/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.service;

import com.wenqi.ordermanagement.constants.JobStatus;
import com.wenqi.ordermanagement.constants.PurchaseStatus;
import com.wenqi.ordermanagement.dto.PurchaseOrderDTO;
import com.wenqi.ordermanagement.entity.CustomerOrder;
import com.wenqi.ordermanagement.entity.OrderLine;
import com.wenqi.ordermanagement.entity.PurchaseOrder;
import com.wenqi.ordermanagement.repository.CustomerOrderRepository;
import com.wenqi.ordermanagement.repository.JobRepository;
import com.wenqi.ordermanagement.repository.OrderLineRepository;
import com.wenqi.ordermanagement.repository.PurchaseOrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.wenqi.ordermanagement.utils.GenerateClass.*;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PurchaseOrderServiceTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @MockBean
    private CustomerOrderRepository customerOrderRepository;

    @MockBean
    private PurchaseOrderRepository purchaseOrderRepository;

    @MockBean
    private OrderLineRepository orderLineRepository;

    @MockBean
    private JobRepository jobRepository;

    @Test
    public void testCreatePurchaseOrderService() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        OrderLine orderLine = generateNewOrderLine();
        PageRequest pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());

        doReturn(customerOrder).when(customerOrderRepository).findByCustomerOrderId(customerOrderId);
        doReturn(orderLinePage).when(orderLineRepository).findByCustomerOrderId(customerOrderId, pageRequest);
        doReturn(purchaseOrder).when(purchaseOrderRepository).save(any(PurchaseOrder.class));
        doReturn(orderLine).when(orderLineRepository).save(any(OrderLine.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        List<PurchaseOrder> purchaseOrderList = purchaseOrderService.createPurchaseOrderFromCustomerOrderId(customerOrderId);
        logger.debug("Called purchase order service, added a purchase order: " + purchaseOrderList);
        purchaseOrderList.forEach(purchaseOrder1 -> assertThat(purchaseOrder1).isEqualTo(purchaseOrder));

        verify(customerOrderRepository, times(1)).findByCustomerOrderId(customerOrderId);
        verify(orderLineRepository, times(1)).findByCustomerOrderId(customerOrderId, pageRequest);
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
        verify(orderLineRepository, times(1)).save(any(OrderLine.class));
        verifyNoMoreInteractions(customerOrderRepository);
        verifyNoMoreInteractions(purchaseOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testUpdatePurchaseOrderStatusService() {
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        OrderLine orderLine = generateNewOrderLine();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        PurchaseOrderDTO expectedUpdatedPurchaseOrderDTO = generateUpdatedPurchaseOrderStatusDTO();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());

        doReturn(purchaseOrder).when(purchaseOrderRepository).findByPurchaseOrderId(purchaseOrderId);
        doReturn(orderLinePage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        PurchaseOrderDTO updatedPurchaseOrderDTO = purchaseOrderService.updatePurchaseOrderStatus(purchaseOrderId, PurchaseStatus.AWAITING_SHIPPING);
        logger.debug("Called purchase order service, updated status of a purchase order. (id is: " + purchaseOrderId + ")");
        assertThat(updatedPurchaseOrderDTO).isEqualToComparingFieldByFieldRecursively(expectedUpdatedPurchaseOrderDTO);

        verify(purchaseOrderRepository, times(2)).findByPurchaseOrderId(purchaseOrderId);
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
        verify(orderLineRepository, times(1)).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verifyNoMoreInteractions(purchaseOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testGetPurchaseOrderByPurchaseOrderIdService() {
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        OrderLine orderLine = generateNewOrderLine();
        PurchaseOrderDTO expectedPurchaseOrderDTO = generateNewPurchaseOrderDTO();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());

        doReturn(purchaseOrder).when(purchaseOrderRepository).findByPurchaseOrderId(purchaseOrderId);
        doReturn(orderLinePage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        PurchaseOrderDTO gotPurchaseOrderDTO = purchaseOrderService.getPurchaseOrderByPurchaseOrderId(purchaseOrderId, pageRequest);
        logger.debug("Called purchase order service, got purchase order by purchase order id: " + purchaseOrderId);
        assertThat(gotPurchaseOrderDTO).isEqualToComparingFieldByFieldRecursively(expectedPurchaseOrderDTO);

        verify(purchaseOrderRepository, times(1)).findByPurchaseOrderId(purchaseOrderId);
        verify(orderLineRepository, times(1)).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verifyNoMoreInteractions(purchaseOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testGetPurchaseOrderByCustomerOrderIdService() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        OrderLine orderLine = generateNewOrderLine();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<PurchaseOrder> purchaseOrderList = Collections.singletonList(purchaseOrder);
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<PurchaseOrder> purchaseOrderPage = new PageImpl<>(purchaseOrderList, pageRequest, purchaseOrderList.size());
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());

        doReturn(purchaseOrderPage).when(purchaseOrderRepository).findByCustomerOrderId(customerOrderId, pageRequest);
        doReturn(orderLinePage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        List<PurchaseOrderDTO> gotPurchaseOrderDTOList = purchaseOrderService.getPurchaseOrderByCustomerOrderId(customerOrderId, pageRequest, pageRequest);
        logger.debug("Called purchase order service, got a list of purchase orders by customer order id: " + customerOrderId);
        PurchaseOrderDTO expectedPurchaseOrderDTO = generateNewPurchaseOrderDTO();
        gotPurchaseOrderDTOList.forEach(purchaseOrderDTO -> assertThat(purchaseOrderDTO).isEqualToComparingFieldByFieldRecursively(expectedPurchaseOrderDTO));

        verify(purchaseOrderRepository, times(1)).findByCustomerOrderId(customerOrderId, pageRequest);
        verify(orderLineRepository, times(1)).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verifyNoMoreInteractions(purchaseOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testGetPurchaseOrderByProviderIdAndCreateDatetimeRangeService() {
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        long providerId = purchaseOrder.getProviderId();
        OrderLine orderLine = generateNewOrderLine();
        PurchaseOrderDTO expectedPurchaseOrderDTO = generateNewPurchaseOrderDTO();
        Date startDate = generateStartDate();
        Date endDate = generateEndDate();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<PurchaseOrder> purchaseOrderList = Collections.singletonList(purchaseOrder);
        Page<PurchaseOrder> purchaseOrderPage = new PageImpl<>(purchaseOrderList, pageRequest, purchaseOrderList.size());
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());

        doReturn(purchaseOrderPage).when(purchaseOrderRepository).findByProviderIdAndCreateDatetimeBetween(providerId, startDate, endDate, pageRequest);
        doReturn(orderLinePage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        List<PurchaseOrderDTO> gotPurchaseOrderDTO = purchaseOrderService.getPurchaseOrderByProviderIdAndCreateDatetimeRange(providerId, startDate, endDate, pageRequest, pageRequest);
        logger.debug("Called purchase order service, got purchase order by provider id: " + providerId + " and create date time between start date: " + startDate + " and end date: " + endDate);
        gotPurchaseOrderDTO.forEach(purchaseOrderDTO ->
                assertThat(purchaseOrderDTO).isEqualToComparingFieldByFieldRecursively(expectedPurchaseOrderDTO));

        verify(purchaseOrderRepository, times(1)).findByProviderIdAndCreateDatetimeBetween(providerId, startDate, endDate, pageRequest);
        verify(orderLineRepository, times(1)).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verifyNoMoreInteractions(purchaseOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testCancelPurchaseOrderByPurchaseOrderIdService() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        Pageable pageRequest = PageRequest.of(0, 2000);
        OrderLine orderline = generateNewOrderLine();
        List<OrderLine> orderLineList = Collections.singletonList(orderline);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());

        doReturn(purchaseOrder).when(purchaseOrderRepository).findByPurchaseOrderId(purchaseOrderId);
        doReturn(purchaseOrder).when(purchaseOrderRepository).save(any(PurchaseOrder.class));
        doReturn(orderLinePage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(orderline).when(orderLineRepository).save(any(OrderLine.class));
        doReturn(customerOrder).when(customerOrderRepository).findByCustomerOrderId(customerOrderId);
        doReturn(customerOrder).when(customerOrderRepository).save(any(CustomerOrder.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        purchaseOrderService.cancelPurchaseOrderByPurchaseOrderId(purchaseOrderId);
        logger.debug("called Purchase order service, cancel Purchase order by Purchase order id: " + purchaseOrderId);

        verify(purchaseOrderRepository, times(1)).findByPurchaseOrderId(customerOrderId);
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
        verify(orderLineRepository, times(1)).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verify(orderLineRepository).save(any(OrderLine.class));
        verify(customerOrderRepository).findByCustomerOrderId(purchaseOrderId);
        verify(customerOrderRepository).save(any(CustomerOrder.class));
        verifyNoMoreInteractions(customerOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
        verifyNoMoreInteractions(purchaseOrderRepository);
    }
}
