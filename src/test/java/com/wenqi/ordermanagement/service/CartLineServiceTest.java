/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.service;

import com.wenqi.ordermanagement.constants.JobStatus;
import com.wenqi.ordermanagement.dto.CartLineDTO;
import com.wenqi.ordermanagement.entity.CartLine;
import com.wenqi.ordermanagement.repository.CartLineRepository;
import com.wenqi.ordermanagement.repository.JobRepository;
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

import static com.wenqi.ordermanagement.utils.GenerateClass.generateNewCartLine;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartLineServiceTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CartLineService cartLineService;

    @MockBean
    private CartLineRepository cartLineRepository;

    @MockBean
    private JobRepository jobRepository;

    @Test
    public void testCreateCartLineService() {
        CartLine cartLine = generateNewCartLine();
        CartLineDTO cartLineDTO = new CartLineDTO(cartLine);

        doReturn(cartLine).when(cartLineRepository).save(any(CartLine.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        CartLineDTO createdCartLineDTO = cartLineService.createCartLine(cartLineDTO);
        logger.debug("Called cart line service, created a cart line: " + createdCartLineDTO);
        assertThat(createdCartLineDTO).isEqualToComparingFieldByField(cartLineDTO);

        verify(cartLineRepository, times(1)).save(any(CartLine.class));
        verifyNoMoreInteractions(cartLineRepository);
    }

    @Test
    public void testGetCartLineByCustomerIdService() {
        CartLine cartLine = generateNewCartLine();
        long customerId = cartLine.getCustomerId();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<CartLine> cartLineList = Collections.singletonList(cartLine);
        Page<CartLine> cartLinePage = new PageImpl<>(cartLineList, pageRequest, cartLineList.size());

        doReturn(cartLinePage).when(cartLineRepository).findByCustomerId(customerId, pageRequest);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        List<CartLineDTO> gotCartLineDTOList = cartLineService.getCartLineByCustomerId(customerId, pageRequest);
        logger.debug("Cart line got " + gotCartLineDTOList);
        gotCartLineDTOList.forEach(cartLineDTO -> assertThat(cartLineDTO.customerId).isEqualTo(customerId));

        verify(cartLineRepository, times(1)).findByCustomerId(customerId, pageRequest);
        verifyNoMoreInteractions(cartLineRepository);
    }

    @Test
    public void testUpdateCartLineByCustomerIdAndProductIdService() {
        CartLine cartLine = generateNewCartLine();
        long customerId = cartLine.getCustomerId();
        long productId = cartLine.getProductId();
        int quantity = cartLine.getQuantity();
        int newQuantity = quantity + 1;

        doReturn(cartLine).when(cartLineRepository).findByCustomerIdAndProductId(customerId, productId);
        doReturn(cartLine).when(cartLineRepository).save(any(CartLine.class));
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        CartLineDTO updatedCartLine = cartLineService.updateProductQuantityByCustomerIdAndProductId(customerId, productId, newQuantity);
        logger.debug("Cart line got by customer id: " + customerId + " and product id: " + productId);
        assertThat(updatedCartLine.customerId).isEqualTo(customerId);
        assertThat(updatedCartLine.productId).isEqualTo(productId);
        assertThat(updatedCartLine.quantity).isEqualTo(newQuantity);

        verify(cartLineRepository, times(1)).findByCustomerIdAndProductId(customerId, productId);
        verify(cartLineRepository, times(1)).save(any(CartLine.class));
        verifyNoMoreInteractions(cartLineRepository);
    }

    @Test
    public void testDeleteCartLineByCustomerIdAndProductIdService() {
        CartLine cartLine = generateNewCartLine();
        long cartId = cartLine.getCartId();
        long customerId = cartLine.getCustomerId();
        long productId = cartLine.getProductId();

        doReturn(cartLine).when(cartLineRepository).findByCustomerIdAndProductId(customerId, productId);
        doReturn(cartLine).when(cartLineRepository).findByCartId(cartId);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.AWAITING_PROCESS);
        doReturn(null).when(jobRepository).findTop1ByJobStatus(JobStatus.PENDING);

        cartLineService.deleteCartLineByCustomerIdAndProductId(customerId, productId);
        logger.debug("Cart line deleted by cart id: " + cartId);

        verify(cartLineRepository, times(1)).findByCustomerIdAndProductId(customerId, productId);
        verify(cartLineRepository, times(1)).findByCartId(cartId);
        verify(cartLineRepository, times(1)).delete(any(CartLine.class));
        verifyNoMoreInteractions(cartLineRepository);
    }
}
