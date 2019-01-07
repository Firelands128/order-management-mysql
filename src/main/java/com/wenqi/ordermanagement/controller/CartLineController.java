/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.controller;

import com.wenqi.ordermanagement.dto.CartLineDTO;
import com.wenqi.ordermanagement.service.CartLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/cart", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CartLineController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CartLineService cartLineService;

    @Autowired
    public CartLineController(CartLineService cartLineService) {
        this.cartLineService = cartLineService;
    }

    /**
     * add an item in user's cart and
     * return a {@link ResponseEntity} of the added {@link CartLineDTO}
     * <p>
     * URL path: "/cart"
     * </p>
     * <p>
     * HTTP method: POST
     * </p>
     *
     * @param cartLineDTO The data transfer object of cart line
     * @return a {@link ResponseEntity} of the added {@link CartLineDTO}
     */

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CartLineDTO> createCartLine(@RequestBody CartLineDTO cartLineDTO) {
        logger.info("Create cart line: " + cartLineDTO);
        CartLineDTO createdCartLine = cartLineService.createCartLine(cartLineDTO);
        return new ResponseEntity<>(createdCartLine, HttpStatus.CREATED);
    }

    /**
     * get a list of shopping cart items and
     * return a {@link ResponseEntity} of the list of {@link CartLineDTO}
     * <p>
     * URL path: "/cart/{customerId}
     * </p>
     * <p>
     * HTTP method: GET
     * </p>
     *
     * @param customerId  The customer ID
     * @param pageRequest The {@link org.springframework.data.domain.PageRequest} including page size and page index
     * @return a {@link ResponseEntity} of the list of {@link CartLineDTO}
     */
    @RequestMapping(method = RequestMethod.GET, value = "/customer/{customerId}")
    public ResponseEntity<List<CartLineDTO>> getCartLineByCustomerId(@PathVariable("customerId") long customerId,
                                                                     Pageable pageRequest) {
        logger.info("Get cart line by customer id: " + customerId);
        List<CartLineDTO> found = cartLineService.getCartLineByCustomerId(customerId, pageRequest);
        return new ResponseEntity<>(found, HttpStatus.OK);
    }

    /**
     * update a cart line's quantity by customerId and productId and
     * return a {@link ResponseEntity} of the added {@link CartLineDTO}
     * <p>
     * URL path: "/cart"
     * </p>
     * <p>
     * Request Parameters: customerId, productId, quantity
     * </p>
     * <p>
     * HTTP method: GET
     * </p>
     *
     * @param customerId The customer id
     * @param productId  The product id
     * @param quantity   The new quantity
     * @return a {@link ResponseEntity} of the {@link CartLineDTO}
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<CartLineDTO> updateCartLineByCustomerIdAndProductId(@Param("customerId") long customerId,
                                                                              @Param("productId") long productId,
                                                                              @Param("quantity") int quantity) {
        logger.info("Update cart line by customer id: " + customerId + " and product id: " + productId + ".");
        CartLineDTO updatedCartLine = cartLineService.updateProductQuantityByCustomerIdAndProductId(customerId, productId, quantity);
        return new ResponseEntity<>(updatedCartLine, HttpStatus.OK);
    }

    /**
     * delete a cart line by customerId and productId and
     * return a {@link ResponseEntity} without response body
     * <p>
     * URL path: "/cart"
     * </p>
     * <p>
     * Request Parameters: customerId, productId
     * </p>
     * <p>
     * HTTP method: DELETE
     * </p>
     *
     * @param customerId The customer id
     * @param productId  The product id
     * @return a {@link ResponseEntity} without response body
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity deleteCartLineByCustomerIdAndProductId(@Param("customerId") long customerId,
                                                                 @Param("productId") long productId) {
        logger.info("Delete cart line by customer id: " + customerId + " and product id: " + productId + ".");
        cartLineService.deleteCartLineByCustomerIdAndProductId(customerId, productId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
