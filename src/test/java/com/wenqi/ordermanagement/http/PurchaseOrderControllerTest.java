/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PurchaseOrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerOrderRepository customerOrderRepository;

    @MockBean
    private PurchaseOrderRepository purchaseOrderRepository;

    @MockBean
    private OrderLineRepository orderLineRepository;

    @MockBean
    private JobRepository jobRepository;

    @Test
    public void testUpdatePurchaseOrderStatusToShippingController() throws Exception {
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        OrderLine orderLine = generateNewOrderLine();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());

        doReturn(purchaseOrder).when(purchaseOrderRepository).findByPurchaseOrderId(purchaseOrderId);
        doReturn(orderLinePage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        MvcResult mockResponse = mockMvc.perform(get("/purchaseorder/shipped/1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String expectedResponseBody = generateUpdatedStringPurchaseOrderStatusToShippingDTO();
        String mvcResponse = new String(mockResponse.getResponse().getContentAsByteArray());
        JSONAssert.assertEquals(expectedResponseBody, mvcResponse, true);

        verify(purchaseOrderRepository, times(2)).findByPurchaseOrderId(purchaseOrderId);
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
        verify(orderLineRepository, times(1)).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verifyNoMoreInteractions(purchaseOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testUpdatePurchaseOrderStatusToDeliveredController() throws Exception {
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        OrderLine orderLine = generateNewOrderLine();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());

        doReturn(purchaseOrder).when(purchaseOrderRepository).findByPurchaseOrderId(purchaseOrderId);
        doReturn(orderLinePage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        MvcResult mockResponse = mockMvc.perform(get("/purchaseorder/delivered/1")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String expectedResponseBody = generateUpdatedStringPurchaseOrderStatusToDeliveredDTO();
        String mvcResponse = new String(mockResponse.getResponse().getContentAsByteArray());
        JSONAssert.assertEquals(expectedResponseBody, mvcResponse, true);

        verify(purchaseOrderRepository, times(2)).findByPurchaseOrderId(purchaseOrderId);
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
        verify(orderLineRepository, times(1)).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verifyNoMoreInteractions(purchaseOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testGetPurchaseOrderByPurchaseOrderIdController() throws Exception {
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        OrderLine orderLine = generateNewOrderLine();
        Pageable pageRequest = PageRequest.of(0, 2000);
        String pageParameterString = "?page=" + pageRequest.getPageNumber() + "&size=" + pageRequest.getPageSize();
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());

        doReturn(purchaseOrder).when(purchaseOrderRepository).findByPurchaseOrderId(purchaseOrderId);
        doReturn(orderLinePage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        MvcResult mockResponse = mockMvc.perform(get("/purchaseorder/1" + pageParameterString)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String expectedResponseBody = generateNewStringPurchaseOrderDTO();
        String mvcResponse = new String(mockResponse.getResponse().getContentAsByteArray());
        JSONAssert.assertEquals(expectedResponseBody, mvcResponse, true);

        verify(purchaseOrderRepository, times(1)).findByPurchaseOrderId(purchaseOrderId);
        verify(orderLineRepository, times(1)).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verifyNoMoreInteractions(purchaseOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testGetPurchaseOrderByProviderIdAndCreateDatetimeRangeController() throws Exception {
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        long providerId = purchaseOrder.getProviderId();
        OrderLine orderLine = generateNewOrderLine();
        Date startDate = generateStartDate();
        Date endDate = generateEndDate();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String startDateString = df.format(startDate);
        String endDateString = df.format(endDate);
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<PurchaseOrder> purchaseOrderList = Collections.singletonList(purchaseOrder);
        Page<PurchaseOrder> purchaseOrderPage = new PageImpl<>(purchaseOrderList, pageRequest, purchaseOrderList.size());
        List<OrderLine> orderLineList = Collections.singletonList(orderLine);
        Page<OrderLine> orderLinePage = new PageImpl<>(orderLineList, pageRequest, orderLineList.size());

        doReturn(purchaseOrderPage).when(purchaseOrderRepository).findByProviderIdAndCreateDatetimeBetween(providerId, startDate, endDate, pageRequest);
        doReturn(orderLinePage).when(orderLineRepository).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        MvcResult mockResponse = mockMvc.perform(get("/purchaseorder/provider/1")
                .param("purchase_page", String.valueOf(pageRequest.getPageNumber()))
                .param("purchase_size", String.valueOf(pageRequest.getPageSize()))
                .param("line_page", String.valueOf(pageRequest.getPageNumber()))
                .param("line_size", String.valueOf(pageRequest.getPageSize()))
                .param("startDate", startDateString)
                .param("endDate", endDateString)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String expectedResponseBody = generateNewStringListPurchaseOrderDTO();
        String mvcResponse = new String(mockResponse.getResponse().getContentAsByteArray());
        JSONAssert.assertEquals(expectedResponseBody, mvcResponse, true);

        verify(purchaseOrderRepository, times(1)).findByProviderIdAndCreateDatetimeBetween(providerId, startDate, endDate, pageRequest);
        verify(orderLineRepository, times(1)).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verifyNoMoreInteractions(purchaseOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testCancelPurchaseOrderByPurchaseOrderIdController() throws Exception {
        CustomerOrder customerOrder = generateNewCustomerOrder();
        long customerOrderId = customerOrder.getCustomerOrderId();
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        long purchaseOrderId = purchaseOrder.getPurchaseOrderId();
        Pageable pageRequest = PageRequest.of(0, 2000);
        OrderLine orderline = generateNewOrderLine();
        long lineId = orderline.getLineId();
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

        MvcResult mockResponse = mockMvc.perform(head("/purchaseorder/cancel/" + lineId)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        verify(purchaseOrderRepository, times(1)).findByPurchaseOrderId(purchaseOrderId);
        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        verify(orderLineRepository, times(1)).findByPurchaseOrderId(purchaseOrderId, pageRequest);
        verify(orderLineRepository).save(any(OrderLine.class));
        verify(customerOrderRepository, times(1)).findByCustomerOrderId(customerOrderId);
        verify(customerOrderRepository).save(any(CustomerOrder.class));
        verifyNoMoreInteractions(customerOrderRepository);
        verifyNoMoreInteractions(orderLineRepository);
        verifyNoMoreInteractions(purchaseOrderRepository);
    }

    private String generateNewStringPurchaseOrderDTO() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        mapper.setDateFormat(dateFormat);
        PurchaseOrderDTO newPurchaseOrderDTO = generateNewPurchaseOrderDTO();
        return mapper.writeValueAsString(newPurchaseOrderDTO);
    }

    private String generateNewStringListPurchaseOrderDTO() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        mapper.setDateFormat(dateFormat);
        PurchaseOrderDTO newPurchaseOrderDTO = generateNewPurchaseOrderDTO();
        return mapper.writeValueAsString(Collections.singletonList(newPurchaseOrderDTO));
    }

    private String generateUpdatedStringPurchaseOrderStatusToShippingDTO() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        mapper.setDateFormat(dateFormat);
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        OrderLine orderLine = generateNewOrderLine();
        purchaseOrder.setPurchaseOrderStatus(PurchaseStatus.SHIPPING);
        PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO(purchaseOrder, Collections.singletonList(orderLine));
        return mapper.writeValueAsString(purchaseOrderDTO);
    }

    private String generateUpdatedStringPurchaseOrderStatusToDeliveredDTO() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        mapper.setDateFormat(dateFormat);
        PurchaseOrder purchaseOrder = generateNewPurchaseOrder();
        OrderLine orderLine = generateNewOrderLine();
        purchaseOrder.setPurchaseOrderStatus(PurchaseStatus.DELIVERED);
        PurchaseOrderDTO purchaseOrderDTO = new PurchaseOrderDTO(purchaseOrder, Collections.singletonList(orderLine));
        return mapper.writeValueAsString(purchaseOrderDTO);
    }
}
