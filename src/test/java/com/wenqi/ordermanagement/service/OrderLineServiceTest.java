/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.service;

import com.wenqi.ordermanagement.constants.JobStatus;
import com.wenqi.ordermanagement.entity.CustomerOrder;
import com.wenqi.ordermanagement.entity.OrderLine;
import com.wenqi.ordermanagement.entity.PurchaseOrder;
import com.wenqi.ordermanagement.repository.JobRepository;
import com.wenqi.ordermanagement.repository.OrderLineRepository;
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
import java.util.List;

import static com.wenqi.ordermanagement.utils.GenerateClass.*;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderLineServiceTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderLineService orderLineService;

    @MockBean
    private OrderLineRepository orderLineRepository;

    @MockBean
    private JobRepository jobRepository;

    @Test
    public void testSaveOrderLineService() {
        OrderLine orderLine = generateNewOrderLine();
        doReturn(orderLine).when(orderLineRepository).save(any(OrderLine.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);
        OrderLine addedOrderLine = orderLineService.saveOrderLine(orderLine);
        logger.debug("Order line added " + addedOrderLine);
        assertThat(addedOrderLine).isEqualTo(orderLine);
    }

    @Test
    public void testGetOrderLineByCustomerOrderIdService() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        OrderLine orderLine = generateNewOrderLine();
        long customerOrderId = customerOrder.getCustomerOrderId();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> purchaseOrderPage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());

        doReturn(purchaseOrderPage).when(orderLineRepository).findByCustomerOrderId(customerOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);
        List<OrderLine> gotOrderLineList = orderLineService.getOrderLineByCustomerOrderId(customerOrderId, pageRequest);
        gotOrderLineList.forEach(orderLine1 -> assertThat(orderLine1.getCustomerOrderId()).isEqualTo(customerOrderId));
    }

    @Test
    public void testGetOrderLineByPurchaseOrderIdService() {
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        OrderLine orderLine = generateNewOrderLine();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> purchaseOrderPage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());

        doReturn(purchaseOrderPage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);
        List<OrderLine> gotOrderLineList = orderLineService.getOrderLineByPurchaseOrderId(purchaseOrderId, pageRequest);
        gotOrderLineList.forEach(orderLine1 -> assertThat(orderLine1.getPurchaseOrderId()).isEqualTo(purchaseOrderId));
    }

    @Test
    public void testCreateOrderLineOfCustomerOrderService() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();
        OrderLine orderLine = generateNewOrderLine();

        doReturn(orderLine).when(orderLineRepository).save(any(OrderLine.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        List<OrderLine> createdOrderLineList = orderLineService
                .createOrderLineOfCustomerOrder(customerOrderId, Collections.singletonList(orderLine));
        logger.debug("Called order line service, added a list of order lines in a customer order: " + createdOrderLineList);
        createdOrderLineList.forEach(orderLine1 -> assertThat(orderLine1).isEqualTo(orderLine));

        verify(orderLineRepository, times(1)).save(any(OrderLine.class));
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testReturnItemService() {
        OrderLine orderLine = generateNewOrderLine();
        long lineId = orderLine.getLineId();

        doReturn(orderLine).when(orderLineRepository).findByLineId(lineId);
        doReturn(orderLine).when(orderLineRepository).save(any(OrderLine.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        orderLineService.returnItem(lineId, 1);
        logger.debug("Called order line service, return an item, lineId is: " + lineId);

        verify(orderLineRepository, times(1)).findByLineId(lineId);
        verify(orderLineRepository, times(1)).save(any(OrderLine.class));
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testReceiveReturnItemService() {
        OrderLine orderLine = generateNewOrderLine();
        long lineId = orderLine.getLineId();

        doReturn(orderLine).when(orderLineRepository).findByLineId(lineId);
        doReturn(orderLine).when(orderLineRepository).save(any(OrderLine.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        orderLineService.receiveReturnItem(lineId, 1);
        logger.debug("Called order line service, return an item, lineId is: " + lineId);

        verify(orderLineRepository, times(1)).findByLineId(lineId);
        verify(orderLineRepository, times(1)).save(any(OrderLine.class));
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testCancelItemService() {
        OrderLine orderLine = generateNewOrderLine();
        long lineId = orderLine.getLineId();

        doReturn(orderLine).when(orderLineRepository).findByLineId(lineId);
        doReturn(orderLine).when(orderLineRepository).save(any(OrderLine.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        orderLineService.cancelItem(lineId);
        logger.debug("Called order line service, return an item, lineId is: " + lineId);

        verify(orderLineRepository, times(1)).findByLineId(lineId);
        verify(orderLineRepository, times(1)).save(any(OrderLine.class));
        verifyNoMoreInteractions(orderLineRepository);
    }
}
