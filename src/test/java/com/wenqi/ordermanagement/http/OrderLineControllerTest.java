/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.http;

import com.wenqi.ordermanagement.constants.JobStatus;
import com.wenqi.ordermanagement.entity.OrderLine;
import com.wenqi.ordermanagement.repository.CustomerOrderRepository;
import com.wenqi.ordermanagement.repository.JobRepository;
import com.wenqi.ordermanagement.repository.OrderLineRepository;
import com.wenqi.ordermanagement.repository.PurchaseOrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.wenqi.ordermanagement.utils.GenerateClass.generateNewOrderLine;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderLineControllerTest {
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
    public void testReturnItemController() throws Exception {
        OrderLine orderLine = generateNewOrderLine();
        long lineId = orderLine.getLineId();

        doReturn(orderLine).when(orderLineRepository).findByLineId(lineId);
        doReturn(orderLine).when(orderLineRepository).save(any(OrderLine.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        MvcResult mockResponse = mockMvc.perform(head("/orderline/return/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("quantity", "1"))
                .andDo(print()).andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        verify(orderLineRepository, times(1)).findByLineId(lineId);
        verify(orderLineRepository, times(1)).save(any(OrderLine.class));
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testReceiveReturnItemController() throws Exception {
        OrderLine orderLine = generateNewOrderLine();
        long lineId = orderLine.getLineId();

        doReturn(orderLine).when(orderLineRepository).findByLineId(lineId);
        doReturn(orderLine).when(orderLineRepository).save(any(OrderLine.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        MvcResult mockResponse = mockMvc.perform(head("/orderline/receive/" + lineId)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .param("quantity", "1"))
                .andDo(print()).andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        verify(orderLineRepository, times(1)).findByLineId(lineId);
        verify(orderLineRepository, times(1)).save(any(OrderLine.class));
        verifyNoMoreInteractions(orderLineRepository);
    }

    @Test
    public void testCancelItemController() throws Exception {
        OrderLine orderLine = generateNewOrderLine();
        long lineId = orderLine.getLineId();

        doReturn(orderLine).when(orderLineRepository).findByLineId(lineId);
        doReturn(orderLine).when(orderLineRepository).save(any(OrderLine.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        MvcResult mockResponse = mockMvc.perform(head("/orderline/cancel/" + lineId)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        verify(orderLineRepository, times(1)).findByLineId(lineId);
        verify(orderLineRepository, times(1)).save(any(OrderLine.class));
        verifyNoMoreInteractions(orderLineRepository);
    }
}
