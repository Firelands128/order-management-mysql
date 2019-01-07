/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.service.impl;

import com.wenqi.ordermanagement.dto.CartLineDTO;
import com.wenqi.ordermanagement.entity.CartLine;
import com.wenqi.ordermanagement.exception.ErrorCode;
import com.wenqi.ordermanagement.exception.MyException;
import com.wenqi.ordermanagement.repository.CartLineRepository;
import com.wenqi.ordermanagement.service.CartLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartLineServiceImpl implements CartLineService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CartLineRepository cartLineRepository;

    @Autowired
    public CartLineServiceImpl(CartLineRepository cartLineRepository) {
        this.cartLineRepository = cartLineRepository;
    }

    @Override
    public CartLineDTO createCartLine(CartLineDTO cartLineDTO) {
        CartLine cartLine = new CartLine();
        cartLine.setProductId(cartLineDTO.productId);
        cartLine.setUnitPrice(cartLineDTO.unitPrice);
        cartLine.setQuantity(cartLineDTO.quantity);
        cartLine.setCustomerId(cartLineDTO.customerId);
        CartLine savedCartLine = cartLineRepository.save(cartLine);
        return new CartLineDTO(savedCartLine);
    }

    @Override
    public List<CartLineDTO> getCartLineByCustomerId(long customerId, Pageable pageRequest) {
        if (customerId == 0) {
            logger.error("CustomerId cannot be 0, get cart lines by customer id failed.");
            throw new MyException(ErrorCode.BAD_REQUEST, "CustomerId cannot be 0, get cart lines by customer id failed.");
        }
        List<CartLine> cartLineList = cartLineRepository.findByCustomerId(customerId, pageRequest).getContent();
        if (cartLineList == null) {
            logger.error("Cart line not found, get cart lines by customer id failed.");
            throw new MyException(ErrorCode.NOT_FOUND, "Cart line not found, get cart lines by customer id failed.");
        }
        logger.debug("got a list of cart lines by customer id.");
        List<CartLineDTO> returnList = new ArrayList<>();
        for (CartLine cartLine : cartLineList) {
            returnList.add(new CartLineDTO(cartLine));
        }
        return returnList;
    }

    @Override
    public CartLineDTO updateProductQuantityByCustomerIdAndProductId(long customerId, long productId, int quantity) {
        CartLine cartLine = getCartLineByCustomerIdAndProductId(customerId, productId);
        logger.debug("got cart line by customer id and product id.");
        cartLine.setQuantity(quantity);
        cartLineRepository.save(cartLine);
        logger.debug("updated cart line quantity.");
        return new CartLineDTO(cartLine);
    }

    @Override
    public void deleteCartLineByCustomerIdAndProductId(long customerId, long productId) {
        CartLine cartLine = getCartLineByCustomerIdAndProductId(customerId, productId);
        long cartId = cartLine.getCartId();
        logger.debug("got cart line by customer id and product id.");
        if (cartId == 0) {
            logger.error("CartId cannot be 0, delete cart line by cart line id failed.");
            throw new MyException(ErrorCode.BAD_REQUEST, "CartId cannot be 0, delete cart line by cart line id failed.");
        }
        CartLine found = cartLineRepository.findByCartId(cartId);
        if (found == null) {
            logger.error("Cart Line not found, delete cart line by cart id failed.");
            throw new MyException(ErrorCode.NOT_FOUND, "Cart Line not found, delete cart line by cart id failed.");
        }
        cartLineRepository.delete(found);
        logger.debug("deleted cart line by customer id and product id,");
    }

    private CartLine getCartLineByCustomerIdAndProductId(long customerId, long productId) {
        if (customerId == 0) {
            logger.error("CustomerId cannot be 0, get cart lines by customer id failed.");
            throw new MyException(ErrorCode.BAD_REQUEST, "CustomerId cannot be 0, get cart lines by customer id failed.");
        }
        if (productId == 0) {
            logger.error("ProductId cannot be 0, get cart lines by product id failed.");
            throw new MyException(ErrorCode.BAD_REQUEST, "ProductId cannot be 0, get cart lines by productId id failed.");
        }
        CartLine cartLine = cartLineRepository.findByCustomerIdAndProductId(customerId, productId);
        if (cartLine == null) {
            logger.error("Cart line not found, get cart lines by customer id and product id failed.");
            throw new MyException(ErrorCode.NOT_FOUND,
                    "Cart line not found, get cart lines by customer id and product id failed.");
        }
        return cartLine;
    }
}
