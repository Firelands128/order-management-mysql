/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.service;

import com.wenqi.ordermanagement.constants.JobStatus;
import com.wenqi.ordermanagement.dto.CustomerOrderDTO;
import com.wenqi.ordermanagement.dto.CustomerOrderGotDTO;
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
public class CustomerOrderServiceTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CustomerOrderService customerOrderService;

    @MockBean
    private CustomerOrderRepository customerOrderRepository;

    @MockBean
    private OrderLineRepository orderLineRepository;

    @MockBean
    private PurchaseOrderRepository purchaseOrderRepository;

    @MockBean
    private JobRepository jobRepository;

    @Test
    public void testCreateCustomerOrderService() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        OrderLine orderLine = generateNewOrderLine();
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        CustomerOrderDTO customerOrderDTO = generateNewCustomerOrderDTO();

        doReturn(customerOrder).when(customerOrderRepository).save(any(CustomerOrder.class));
        doReturn(orderLine).when(orderLineRepository).save(any(OrderLine.class));
        doReturn(purchaseOrder).when(purchaseOrderRepository).save(any(PurchaseOrder.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        long customerOrderId = customerOrderService.createCustomerOrder(customerOrderDTO);
        logger.debug("Called customer order service, added a customer order: " + customerOrderDTO +
                ", assigned customer order id is: " + customerOrderId);
        assertThat(customerOrderId).isEqualTo(customerOrder.getCustomerId());

        verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
        verify(orderLineRepository, times(1)).save(any(OrderLine.class));
        verifyNoMoreInteractions(customerOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testUpdateCustomerOrderStatusToPaidService() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        OrderLine orderLine = generateNewOrderLine();
        long customerOrderId = customerOrder.getCustomerOrderId();
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        List<PurchaseOrder> purchaseOrderList = Collections.singletonList(purchaseOrder);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());
        Page<PurchaseOrder> purchaseOrderPage = new PageImpl<>(purchaseOrderList, pageRequest, purchaseOrderList.size());

        doReturn(customerOrder).when(customerOrderRepository).findByCustomerOrderId(customerOrderId);
        doReturn(purchaseOrderPage).when(purchaseOrderRepository).findByCustomerOrderId(customerOrderId, pageRequest);
        doReturn(purchaseOrder).when(purchaseOrderRepository).findByPurchaseOrderId(purchaseOrderId);
        doReturn(orderLinePage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        customerOrderService.updateCustomerOrderStatusToPaid(customerOrderId);
        logger.debug("Called customer order service, updated status of a customer order. (id is: " + customerOrderId + ")");

        verify(customerOrderRepository, times(1)).findByCustomerOrderId(customerOrderId);
        verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
        verify(purchaseOrderRepository, times(1)).findByCustomerOrderId(customerOrderId, pageRequest);
        verify(purchaseOrderRepository, times(2)).findByPurchaseOrderId(purchaseOrderId);
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
        verify(orderLineRepository, times(2)).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verifyNoMoreInteractions(customerOrderRepository);
        verifyNoMoreInteractions(purchaseOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testGetCustomerOrderByCustomerOrderIdService() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        OrderLine orderLine = generateNewOrderLine();
        CustomerOrderGotDTO expectedCustomerOrderGotDTO = generateNewCustomerOrderIncludePurchaseOrderGotDTO();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<PurchaseOrder> purchaseOrderList = Collections.singletonList(purchaseOrder);
        Page<PurchaseOrder> purchaseOrderPage = new PageImpl<>(purchaseOrderList, pageRequest, purchaseOrderList.size());
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        doReturn(customerOrder).when(customerOrderRepository).findByCustomerOrderId(customerOrderId);
        doReturn(purchaseOrderPage).when(purchaseOrderRepository).findByCustomerOrderId(customerOrderId, pageRequest);
        doReturn(orderLinePage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        CustomerOrderGotDTO gotCustomerOrderDTO = customerOrderService.getCustomerOrderIncludePurchaseOrderByCustomerOrderId(customerOrderId, pageRequest, pageRequest);
        logger.debug("Called customer order service, got customer order by customer order id: " + customerOrderId);
        assertThat(gotCustomerOrderDTO).isEqualToComparingFieldByFieldRecursively(expectedCustomerOrderGotDTO);

        verify(customerOrderRepository, times(1)).findByCustomerOrderId(customerOrderId);
        verify(purchaseOrderRepository, times(1)).findByCustomerOrderId(customerOrderId, pageRequest);
        verify(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verifyNoMoreInteractions(customerOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testGetCustomerOrderByCustomerIdAndCreateDatetimeRangeService() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();
        long customerId = customerOrder.getCustomerId();
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        OrderLine orderLine = generateNewOrderLine();
        CustomerOrderGotDTO customerOrderGotDTO = generateNewCustomerOrderIncludePurchaseOrderGotDTO();
        Date startDate = generateStartDate();
        Date endDate = generateEndDate();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<CustomerOrder> customerOrderList = Collections.singletonList(customerOrder);
        Page<CustomerOrder> customerOrderPage = new PageImpl<>(customerOrderList, pageRequest, customerOrderList.size());
        List<PurchaseOrder> purchaseOrderList = Collections.singletonList(purchaseOrder);
        Page<PurchaseOrder> purchaseOrderPage = new PageImpl<>(purchaseOrderList, pageRequest, purchaseOrderList.size());
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());

        doReturn(customerOrderPage).when(customerOrderRepository).findByCustomerIdAndCreateDatetimeBetween(customerId, startDate, endDate, pageRequest);
        doReturn(customerOrder).when(customerOrderRepository).findByCustomerOrderId(customerOrderId);
        doReturn(purchaseOrderPage).when(purchaseOrderRepository).findByCustomerOrderId(customerOrderId, pageRequest);
        doReturn(orderLinePage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        List<CustomerOrderGotDTO> customerOrderDTOList = customerOrderService.getCustomerOrderByCustomerIdAndCreateDatetimeRange(customerId, startDate, endDate, pageRequest, pageRequest, pageRequest);
        logger.debug("Called customer order service, got customer order by customer id: " + customerId + " and create date time between start date: " + startDate + " and end date: " + endDate);
        customerOrderDTOList.forEach(customerOrderDTO1 -> assertThat(customerOrderDTO1).isEqualToComparingFieldByFieldRecursively(customerOrderGotDTO));

        verify(customerOrderRepository, times(1)).findByCustomerIdAndCreateDatetimeBetween(customerId, startDate, endDate, pageRequest);
        verify(customerOrderRepository, times(1)).findByCustomerOrderId(customerOrderId);
        verify(purchaseOrderRepository, times(1)).findByCustomerOrderId(customerOrderId, pageRequest);
        verify(orderLineRepository, times(1)).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verifyNoMoreInteractions(customerOrderRepository);
        verifyNoMoreInteractions(purchaseOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testCancelCustomerOrderByCustomerOrderIdService() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();
        Pageable pageRequest = PageRequest.of(0, 2000);
        OrderLine orderline = generateNewOrderLine();
        List<OrderLine> orderLineList = Collections.singletonList(orderline);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();

        doReturn(customerOrder).when(customerOrderRepository).findByCustomerOrderId(customerOrderId);
        doReturn(customerOrder).when(customerOrderRepository).save(any(CustomerOrder.class));
        doReturn(orderLinePage).when(orderLineRepository).findByCustomerOrderId(customerOrderId, pageRequest);
        doReturn(orderline).when(orderLineRepository).save(any(OrderLine.class));
        doReturn(purchaseOrder).when(purchaseOrderRepository).findByPurchaseOrderId(purchaseOrderId);
        doReturn(purchaseOrder).when(purchaseOrderRepository).save(any(PurchaseOrder.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        customerOrderService.cancelCustomerOrderByCustomerOrderId(customerOrderId);
        logger.debug("called customer order service, cancel customer order by customer order id: " + customerOrderId);

        verify(customerOrderRepository, times(1)).findByCustomerOrderId(customerOrderId);
        verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
        verify(orderLineRepository, times(1)).findByCustomerOrderId(customerOrderId, pageRequest);
        verify(orderLineRepository).save(any(OrderLine.class));
        verify(purchaseOrderRepository).findByPurchaseOrderId(purchaseOrderId);
        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        verifyNoMoreInteractions(customerOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
        verifyNoMoreInteractions(purchaseOrderRepository);
    }

    @Test
    public void testAssignWxpayPrepayIdService() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();
        String wxpayId = "123";

        doReturn(customerOrder).when(customerOrderRepository).findByCustomerOrderId(customerOrderId);
        doReturn(customerOrder).when(customerOrderRepository).save(any(CustomerOrder.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        customerOrderService.assignWxpayPrepayId(customerOrderId, wxpayId);
        logger.debug("Called customer order service, assigned weixin pay prepay id: " + wxpayId +
                " to customer order(id: " + customerOrderId + ")");

        verify(customerOrderRepository, times(1)).findByCustomerOrderId(customerOrderId);
        verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
        verifyNoMoreInteractions(customerOrderRepository);
    }

    @Test
    public void testUpdateWxpayStatusService() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();

        doReturn(customerOrder).when(customerOrderRepository).findByCustomerOrderId(customerOrderId);
        doReturn(customerOrder).when(customerOrderRepository).save(any(CustomerOrder.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        customerOrderService.updateWxpayStatus(customerOrderId, true);
        logger.debug("Called customer order service, updated weixin pay status paid to customer order(id: " + customerOrderId + ")");

        verify(customerOrderRepository, times(1)).findByCustomerOrderId(customerOrderId);
        verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
        verifyNoMoreInteractions(customerOrderRepository);
    }

    @Test
    public void testAssignWxpayTransIdService() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();
        String transId = "123";

        doReturn(customerOrder).when(customerOrderRepository).findByCustomerOrderId(customerOrderId);
        doReturn(customerOrder).when(customerOrderRepository).save(any(CustomerOrder.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        customerOrderService.assignWxpayTransId(customerOrderId, transId);
        logger.debug("Called customer order service, assigned weixin pay transaction id: " + transId +
                " to customer order(id: " + customerOrderId + ")");

        verify(customerOrderRepository, times(1)).findByCustomerOrderId(customerOrderId);
        verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
        verifyNoMoreInteractions(customerOrderRepository);
    }

    @Test
    public void testGetWxpayTransIdService() {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();
        String transId = "123";

        doReturn(customerOrder).when(customerOrderRepository).findByCustomerOrderId(customerOrderId);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        String gotTransId = customerOrderService.getWxpayTransId(customerOrderId);
        logger.debug("Called customer order service, assigned weixin pay transaction id: " + transId +
                " to customer order(id: " + customerOrderId + ")");
        assertThat(gotTransId).isEqualTo(transId);

        verify(customerOrderRepository, times(1)).findByCustomerOrderId(customerOrderId);
        verifyNoMoreInteractions(customerOrderRepository);
    }
}
