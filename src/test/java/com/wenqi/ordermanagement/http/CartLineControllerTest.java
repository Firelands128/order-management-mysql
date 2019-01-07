/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenqi.ordermanagement.constants.JobStatus;
import com.wenqi.ordermanagement.dto.CartLineDTO;
import com.wenqi.ordermanagement.entity.CartLine;
import com.wenqi.ordermanagement.repository.CartLineRepository;
import com.wenqi.ordermanagement.repository.JobRepository;
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

import java.util.Collections;
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
public class CartLineControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartLineRepository cartLineRepository;

    @MockBean
    private JobRepository jobRepository;

    @Test
    public void testCreateCartLineController() throws Exception {
        CartLine cartLine = generateNewCartLine();

        doReturn(cartLine).when(cartLineRepository).save(any(CartLine.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        MvcResult mockResponse = mockMvc.perform(post("/cart")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(generateNewStringCartLineDTO())
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
        String expectedResponseBody = generateNewStringCartLineDTO();
        String mvcResponse = new String(mockResponse.getResponse().getContentAsByteArray());
        JSONAssert.assertEquals(expectedResponseBody, mvcResponse, true);

        verify(cartLineRepository, times(1)).save(any(CartLine.class));
        verifyNoMoreInteractions(cartLineRepository);
    }

    @Test
    public void testGetCartLineByCustomerIdController() throws Exception {
        CartLine cartLine = generateNewCartLine();
        long customerId = cartLine.getCustomerId();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<CartLine> cartLineList = Collections.singletonList(cartLine);
        Page<CartLine> cartLinePage = new PageImpl<>(cartLineList, pageRequest, cartLineList.size());

        doReturn(cartLinePage).when(cartLineRepository).findByCustomerId(customerId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        MvcResult mockResponse = mockMvc.perform(get("/cart/customer/" + customerId)
                .param("page", String.valueOf(pageRequest.getPageNumber()))
                .param("size", String.valueOf(pageRequest.getPageSize()))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String expectedResponseBody = generateNewStringListCartLineDTO();
        String mvcResponse = new String(mockResponse.getResponse().getContentAsByteArray());
        JSONAssert.assertEquals(expectedResponseBody, mvcResponse, true);

        verify(cartLineRepository, times(1)).findByCustomerId(customerId, pageRequest);
        verifyNoMoreInteractions(cartLineRepository);
    }

    @Test
    public void testUpdateCartLineByCustomerIdAndProductIdController() throws Exception {
        CartLine cartLine = generateNewCartLine();
        long customerId = cartLine.getCustomerId();
        long productId = cartLine.getProductId();
        int quantity = cartLine.getQuantity();
        int newQuantity = quantity + 1;

        doReturn(cartLine).when(cartLineRepository).findByCustomerIdAndProductId(customerId, productId);
        doReturn(cartLine).when(cartLineRepository).save(any(CartLine.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        MvcResult mockResponse = mockMvc.perform(get("/cart")
                .param("customerId", String.valueOf(customerId))
                .param("productId", String.valueOf(productId))
                .param("quantity", String.valueOf(newQuantity))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String expectedResponseBody = generateUpdatedStringCustomerOrderStatusDTO();
        String mvcResponse = new String(mockResponse.getResponse().getContentAsByteArray());
        JSONAssert.assertEquals(expectedResponseBody, mvcResponse, true);

        verify(cartLineRepository, times(1)).findByCustomerIdAndProductId(customerId, productId);
        verify(cartLineRepository, times(1)).save(any(CartLine.class));
        verifyNoMoreInteractions(cartLineRepository);
    }

    @Test
    public void testDeleteCartLineByCustomerIdAndProductIdController() throws Exception {
        CartLine cartLine = generateNewCartLine();
        long cartId = cartLine.getCartId();
        long customerId = cartLine.getCustomerId();
        long productId = cartLine.getProductId();

        doReturn(cartLine).when(cartLineRepository).findByCustomerIdAndProductId(customerId, productId);
        doReturn(cartLine).when(cartLineRepository).findByCartId(cartId);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        MvcResult mockResponse = mockMvc.perform(delete("/cart")
                .param("customerId", String.valueOf(customerId))
                .param("productId", String.valueOf(productId))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andReturn();
        assertThat(mockResponse.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());

        verify(cartLineRepository, times(1)).findByCustomerIdAndProductId(customerId, productId);
        verify(cartLineRepository, times(1)).findByCartId(cartId);
        verify(cartLineRepository, times(1)).delete(any(CartLine.class));
        verifyNoMoreInteractions(cartLineRepository);
    }

    private String generateNewStringCartLineDTO() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        CartLineDTO newCustomerOrderDTO = generateNewCartLineDTO();
        return mapper.writeValueAsString(newCustomerOrderDTO);
    }

    private String generateNewStringListCartLineDTO() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        CartLineDTO newCustomerOrderDTO = generateNewCartLineDTO();
        return mapper.writeValueAsString(Collections.singletonList(newCustomerOrderDTO));
    }

    private String generateUpdatedStringCustomerOrderStatusDTO() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        CartLineDTO updatedCustomerOrderDTO = generateUpdatedCartLineDTO();
        return mapper.writeValueAsString(updatedCustomerOrderDTO);
    }
}
