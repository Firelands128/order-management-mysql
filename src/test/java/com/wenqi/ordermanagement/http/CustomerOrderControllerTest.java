/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenqi.ordermanagement.constants.CustomerStatus;
import com.wenqi.ordermanagement.constants.JobStatus;
import com.wenqi.ordermanagement.constants.PurchaseStatus;
import com.wenqi.ordermanagement.dto.CustomerOrderDTO;
import com.wenqi.ordermanagement.dto.CustomerOrderGotDTO;
import com.wenqi.ordermanagement.entity.CustomerOrder;
import com.wenqi.ordermanagement.entity.Job;
import com.wenqi.ordermanagement.entity.OrderLine;
import com.wenqi.ordermanagement.entity.PurchaseOrder;
import com.wenqi.ordermanagement.repository.CustomerOrderRepository;
import com.wenqi.ordermanagement.repository.JobRepository;
import com.wenqi.ordermanagement.repository.OrderLineRepository;
import com.wenqi.ordermanagement.repository.PurchaseOrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.wenqi.ordermanagement.utils.GenerateClass.*;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CustomerOrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerOrderRepository customerOrderRepository;

    @MockBean
    private OrderLineRepository orderLineRepository;

    @MockBean
    private PurchaseOrderRepository purchaseOrderRepository;

    @MockBean
    private JobRepository jobRepository;

    @Test
    public void testCreateCustomerOrderController() throws Exception {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        OrderLine orderLine = generateNewOrderLine();
        long customerOrderId = customerOrder.getCustomerOrderId();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());
        Job job = generateNewJob();

        doReturn(customerOrder).when(customerOrderRepository).save(any(CustomerOrder.class));
        doReturn(orderLine).when(orderLineRepository).save(any(OrderLine.class));
        doReturn(customerOrder).when(customerOrderRepository).findByCustomerOrderId(customerOrderId);
        doReturn(orderLinePage).when(orderLineRepository).findByCustomerOrderId(customerOrderId, pageRequest);
        doReturn(job).when(jobRepository).save(any(Job.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        MvcResult mockResponse = mockMvc.perform(post("/customerorder")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(generateNewStringCustomerOrderDTO())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
        String expectedResponseBody = generateNewStringCustomerOrderIncludeOrderLineGotDTO();
        String mvcResponse = new String(mockResponse.getResponse().getContentAsByteArray());
        JSONAssert.assertEquals(expectedResponseBody, mvcResponse, true);

        verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
        verify(orderLineRepository, times(1)).save(any(OrderLine.class));
        verify(customerOrderRepository, times(1)).findByCustomerOrderId(customerOrderId);
        verify(orderLineRepository, times(1)).findByCustomerOrderId(customerOrderId, pageRequest);
        verify(jobRepository, times(1)).save(any(Job.class));
        verifyNoMoreInteractions(customerOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testUpdateCustomerOrderStatusToPaidController() throws Exception {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        OrderLine orderLine = generateNewOrderLine();

        Pageable pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());
        List<PurchaseOrder> purchaseOrderList = Collections.singletonList(purchaseOrder);
        Page<PurchaseOrder> purchaseOrderPage = new PageImpl<>(purchaseOrderList, pageRequest, purchaseOrderList.size());

        doReturn(customerOrder).when(customerOrderRepository).findByCustomerOrderId(customerOrderId);
        doReturn(purchaseOrderPage).when(purchaseOrderRepository).findByCustomerOrderId(customerOrderId, pageRequest);
        doReturn(purchaseOrder).when(purchaseOrderRepository).findByPurchaseOrderId(purchaseOrderId);
        doReturn(orderLinePage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        MvcResult mockResponse = mockMvc.perform(get("/customerorder/paid/" + customerOrderId)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andReturn();

        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String expectedResponseBody = generateNewStringPaidCustomerOrderGotDTO();
        String mvcResponse = new String(mockResponse.getResponse().getContentAsByteArray());
        JSONAssert.assertEquals(expectedResponseBody, mvcResponse, true);

        verify(customerOrderRepository, times(2)).findByCustomerOrderId(customerOrderId);
        verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
        verify(purchaseOrderRepository, times(2)).findByCustomerOrderId(customerOrderId, pageRequest);
        verify(purchaseOrderRepository, times(2)).findByPurchaseOrderId(purchaseOrderId);
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
        verify(orderLineRepository, times(3)).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verifyNoMoreInteractions(customerOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testGetCustomerOrderByCustomerOrderIdController() throws Exception {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        OrderLine orderLine = generateNewOrderLine();
        Pageable pageRequest = PageRequest.of(0, 2000);
        String pageParameterString = "?purchase_page=" + pageRequest.getPageNumber() + "&purchase_size=" + pageRequest.getPageSize() +
                "&line_page=" + pageRequest.getPageNumber() + "&line_size=" + pageRequest.getPageSize();
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());
        List<PurchaseOrder> purchaseOrderList = Collections.singletonList(purchaseOrder);
        Page<PurchaseOrder> purchaseOrderPage = new PageImpl<>(purchaseOrderList, pageRequest, purchaseOrderList.size());

        doReturn(customerOrder).when(customerOrderRepository).findByCustomerOrderId(customerOrderId);
        doReturn(purchaseOrderPage).when(purchaseOrderRepository).findByCustomerOrderId(customerOrderId, pageRequest);
        doReturn(orderLinePage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        MvcResult mockResponse = mockMvc.perform(
                get("/customerorder/" + customerOrderId + pageParameterString)
                        .accept(MediaType.APPLICATION_JSON_VALUE)).andDo((print())).andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String expectedResponseBody = generateNewStringCustomerOrderIncludePurchaseOrderGotDTO();
        String mvcResponse = new String(mockResponse.getResponse().getContentAsByteArray());
        JSONAssert.assertEquals(expectedResponseBody, mvcResponse, true);

        verify(customerOrderRepository, times(1)).findByCustomerOrderId(customerOrderId);
        verify(purchaseOrderRepository, times(1)).findByCustomerOrderId(customerOrderId, pageRequest);
        verify(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verifyNoMoreInteractions(customerOrderRepository);
        verifyNoMoreInteractions(purchaseOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testGetCustomerOrderByCustomerIdAndCreateDatetimeRangeController() throws Exception {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();
        long customerId = customerOrder.getCustomerId();
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        OrderLine orderLine = generateNewOrderLine();
        Date startDate = generateStartDate();
        Date endDate = generateEndDate();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String startDateString = df.format(startDate);
        String endDateString = df.format(endDate);
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

        MvcResult mockResponse = mockMvc.perform(get("/customerorder/customer/1")
                .param("purchase_page", String.valueOf(pageRequest.getPageNumber()))
                .param("purchase_size", String.valueOf(pageRequest.getPageSize()))
                .param("customer_page", String.valueOf(pageRequest.getPageNumber()))
                .param("customer_size", String.valueOf(pageRequest.getPageSize()))
                .param("line_page", String.valueOf(pageRequest.getPageNumber()))
                .param("line_size", String.valueOf(pageRequest.getPageSize()))
                .param("startDate", startDateString)
                .param("endDate", endDateString)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andReturn();

        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String expectedResponseBody = generateNewStringListCustomerOrderGotDTO();
        String mvcResponse = new String(mockResponse.getResponse().getContentAsByteArray());
        JSONAssert.assertEquals(expectedResponseBody, mvcResponse, true);

        verify(customerOrderRepository, times(1)).findByCustomerIdAndCreateDatetimeBetween(customerId, startDate, endDate, pageRequest);
        verify(customerOrderRepository, times(1)).findByCustomerOrderId(customerOrderId);
        verify(purchaseOrderRepository, times(1)).findByCustomerOrderId(customerOrderId, pageRequest);
        verify(orderLineRepository, times(1)).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verifyNoMoreInteractions(customerOrderRepository);
        verifyNoMoreInteractions(purchaseOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testCancelCustomerOrderByCustomerOrderIdController() throws Exception {
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

        MvcResult mockResponse = mockMvc.perform(head("/customerorder/cancel/" + customerOrderId)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo((print())).andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

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

    private String generateNewStringCustomerOrderDTO() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        mapper.setDateFormat(dateFormat);
        CustomerOrderDTO newCustomerOrderDTO = generateNewCustomerOrderDTO();
        return mapper.writeValueAsString(newCustomerOrderDTO);
    }

    private String generateNewStringCustomerOrderIncludePurchaseOrderGotDTO() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        mapper.setDateFormat(dateFormat);
        CustomerOrderGotDTO newCustomerOrderDTO = generateNewCustomerOrderIncludePurchaseOrderGotDTO();
        return mapper.writeValueAsString(newCustomerOrderDTO);
    }

    private String generateNewStringCustomerOrderIncludeOrderLineGotDTO() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        mapper.setDateFormat(dateFormat);
        CustomerOrderGotDTO newCustomerOrderDTO = generateNewCustomerOrderIncludeOrderLineGotDTO();
        return mapper.writeValueAsString(newCustomerOrderDTO);
    }


    private String generateNewStringPaidCustomerOrderGotDTO() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        mapper.setDateFormat(dateFormat);
        CustomerOrderGotDTO newCustomerOrderDTO = generateNewCustomerOrderIncludePurchaseOrderGotDTO();
        newCustomerOrderDTO.customerOrderStatus = CustomerStatus.PAID;
        newCustomerOrderDTO.purchaseOrderDTOList
                .forEach(purchaseOrderDTO -> purchaseOrderDTO.purchaseOrderStatus = PurchaseStatus.AWAITING_SHIPPING);
        return mapper.writeValueAsString(newCustomerOrderDTO);
    }

    private String generateNewStringListCustomerOrderGotDTO() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        mapper.setDateFormat(dateFormat);
        CustomerOrderGotDTO newCustomerOrderDTO = generateNewCustomerOrderIncludePurchaseOrderGotDTO();
        return mapper.writeValueAsString(Collections.singletonList(newCustomerOrderDTO));
    }
}
