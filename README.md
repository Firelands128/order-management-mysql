# Order Management Web Service

Order management web service with Spring Boot and MySQL, and interact with Wechat Payment interface.
 Create a job thread to generate purchase order according to the given customer order.

### database structure:
![database schema](images/database%20schema.png "database schema")

### services list:
```java
public interface CartLineService {
   CartLineDTO createCartLine(CartLineDTO cartLineDTO);
   List<CartLineDTO> getCartLineByCustomerId(long customerId, Pageable pageRequest);
   CartLineDTO updateProductQuantityByCustomerIdAndProductId(long customerId, long productId, int quantity);
   void deleteCartLineByCustomerIdAndProductId(long customerId, long productId);
}
```
```java
public interface OrderLineService {
    OrderLine saveOrderLine(OrderLine orderLine);
    List<OrderLine> getOrderLineByCustomerOrderId(long customerOrderId, Pageable pageRequest);
    List<OrderLine> getOrderLineByPurchaseOrderId(long purchaseOrderId, Pageable pageRequest);
    List<OrderLine> createOrderLineOfCustomerOrder(long customerOrderId, List<OrderLine> newOrderLineList);
    void returnItem(long lineId, int quantity);
    void receiveReturnItem(long lineId, int quantity);
    void cancelItem(long lineId);
}
```
```java
public interface CustomerOrderService {
    long createCustomerOrder(CustomerOrderDTO customerOrderDTO);
    void updateCustomerOrderStatusToPaid(long customerOrderId);
    CustomerOrder getCustomerOrderById(long customerOrderId);
    CustomerOrderGotDTO getCustomerOrderIncludeOrderLineByCustomerOrderId(long customerOrderId, Pageable orderLinePageRequest);
    CustomerOrderGotDTO getCustomerOrderIncludePurchaseOrderByCustomerOrderId(long customerOrderId, Pageable purchaseOrderPageRequest, Pageable orderLinePageRequest);
    List<CustomerOrderGotDTO> getCustomerOrderByCustomerIdAndCreateDatetimeRange(long customerId, Date startDate, Date endDate, Pageable customerOrderPageRequest, Pageable purchaseOrderPageRequest, Pageable orderLinePageRequest);
    void cancelCustomerOrderByCustomerOrderId(long customerOrderId);
    void assignWxpayPrepayId(long customerOrderId, String wxpayId);
    void updateWxpayStatus(long customerOrderId, boolean paid);
    void assignWxpayTransId(long customerOrderId, String transId);
    String getWxpayTransId(long customerOrderId);
}
```
```java
public interface PurchaseOrderService {
    List<PurchaseOrder> createPurchaseOrderFromCustomerOrderId(long customerOrderId);
    PurchaseOrderDTO updatePurchaseOrderStatus(long purchaseOrderId, PurchaseStatus status);
    PurchaseOrder getPurchaseOrderById(long purchaseOrderId);
    PurchaseOrderDTO getPurchaseOrderByPurchaseOrderId(long purchaseOrderId, Pageable pageRequest);
    List<PurchaseOrderDTO> getPurchaseOrderByCustomerOrderId(long customerOrderId, Pageable purchaseOrderPageRequest, Pageable orderLinePageRequest);
    List<PurchaseOrderDTO> getPurchaseOrderByProviderIdAndCreateDatetimeRange(long providerId, Date startDate, Date endDate, Pageable purchaseOrderPageRequest, Pageable orderLinePageRequest);
    void cancelPurchaseOrderByPurchaseOrderId(long purchaseOrderId);
}
```
### Controller Details
* ##### CartLineController

**createCartLine**
```java
@RequestMapping(method=POST,
                consumes="application/json")
public org.springframework.http.ResponseEntity<CartLineDTO> 
createCartLine(@RequestBody CartLineDTO cartLineDTO)
```
add an item in user's cart and return a ResponseEntity of the added CartLineDTO   
URL path: "/cart"   
HTTP method: POST   
Parameters:   
cartLineDTO - The data transfer object of cart line   
Returns:   
a ResponseEntity of the added CartLineDTO   

**getCartLineByCustomerId**
```java
@RequestMapping(method=GET,
                value="/customer/{customerId}")
public org.springframework.http.ResponseEntity<java.util.List<CartLineDTO>> 
getCartLineByCustomerId(@PathVariable(value="customerId") long customerId,
                        org.springframework.data.domain.Pageable pageRequest)
```
get a list of shopping cart items and return a ResponseEntity of the list of CartLineDTO   
URL path: "/cart/{customerId}   
HTTP method: GET   
Parameters:   
customerId - The customer ID   
pageRequest - The PageRequest including page size and page index   
Returns:   
a ResponseEntity of the list of CartLineDTO   

**updateCartLineByCustomerIdAndProductId**
```java
@RequestMapping(method=GET)
public org.springframework.http.ResponseEntity<CartLineDTO>
       updateCartLineByCustomerIdAndProductId(
                    @Param(value="customerId") long customerId,
                    @Param(value="productId") long productId,
                    @Param(value="quantity") int quantity)
```
update a cart line's quantity by customerId and productId and return a ResponseEntity of the added CartLineDTO   
URL path: "/cart"   
Request Parameters: customerId, productId, quantity   
HTTP method: GET   
Parameters:   
customerId - The customer id   
productId - The product id   
quantity - The new quantity   
Returns:   
a ResponseEntity of the added CartLineDTO   

**deleteCartLineByCustomerIdAndProductId**
```java
@RequestMapping(method=DELETE)
public org.springframework.http.ResponseEntity 
deleteCartLineByCustomerIdAndProductId(
                    @Param(value="customerId") long customerId,
                    @Param(value="productId") long productId)
                                                                                ```
delete a cart line by customerId and productId and return a ResponseEntity without response body   
URL path: "/cart"   
Request Parameters: customerId, productId   
HTTP method: DELETE   
Parameters:   
customerId - The customer id   
productId - The product id   
Returns:   
a ResponseEntity without response body   
* ##### OrderLinController

** returnItem **
```java
@RequestMapping(method=HEAD,
                value="/return/{lineId}")
public org.springframework.http.ResponseEntity 
returnItem(@PathVariable(value="lineId") long lineId,
           @RequestParam(value="quantity") int quantity)
```
return an item by line id and parameter quantity then return a ResponseEntity without response body   
URL path: "/orderline/return/{lineId}   
HTTP method: HEAD   
Parameters:   
lineId - The line id   
quantity - The returning quantity   
Returns:   
a ResponseEntity without response body   
**receiveReturnItem**
```java
@RequestMapping(method=HEAD,
                value="/receive/{lineId}")
public org.springframework.http.ResponseEntity 
receiveReturnItem(@PathVariable(value="lineId") long lineId,
                  @RequestParam(value="quantity") int quantity)
```
 received a returned item by line id and received quantity then return a ResponseEntity without response body   
URL path: "/orderline/receive/{lineId}   
HTTP method: HEAD   
Parameters:   
lineId - The line id   
quantity - The received returning quantity   
Returns:   
a ResponseEntity without response body   
**cancelItem**
```java
@RequestMapping(method=HEAD,
                value="/cancel/{lineId}")
public org.springframework.http.ResponseEntity 
cancelItem(@PathVariable(value="lineId")long lineId)
```
cancel an item and update customer order and purchase order status if needed then return a ResponseEntity without response body   
URL path: "/orderline/cancel/{lineId}   
HTTP method: HEAD   
Parameters:   
lineId - The line id   
Returns:   
a ResponseEntity without response body   
* #### CustomerOrderController

**createCustomerOrder**
```java
@RequestMapping(method=POST,
                consumes="application/json")
public org.springframework.http.ResponseEntity<CustomerOrderGotDTO> 
createCustomerOrder(@RequestBody CustomerOrderDTO newCustomerOrderDTO)
```
add a customer order and return a ResponseEntity of the added CustomerOrderDTO   
URL path: "/customerorder"   
HTTP method: POST   
Parameters:   
newCustomerOrderDTO - The data transfer object of customer order   
Returns:   
a ResponseEntity of the added CustomerOrderDTO   
**paidCustomerOrderStatus**
```java
@RequestMapping(method=GET,
                value="/paid/{customerOrderId}")
public org.springframework.http.ResponseEntity<CustomerOrderGotDTO> 
paidCustomerOrderStatus(
      @PathVariable(value="customerOrderId") long customerOrderId)
```
update a customer order status to paid and return a ResponseEntity of the added CustomerOrderDTO   
URL path: "/customerorder/paid/{customerOrderId}    
HTTP method: GET   
Parameters:   
customerOrderId - The customer order id   
Returns:   
a ResponseEntity of the CustomerOrderDTO   
**getCustomerOrderByCustomerOrderId**
```java
@RequestMapping(method=GET,
                value="/{customerOrderId}")
public org.springframework.http.ResponseEntity<CustomerOrderGotDTO> 
getCustomerOrderByCustomerOrderId(
      @PathVariable(value="customerOrderId") long customerOrderId,
      @Qualifier(value="purchase") org.springframework.data.domain.Pageable   
                                   purchaseOrderPageRequest,
      @Qualifier(value="line") org.springframework.data.domain.Pageable 
                               orderLinePageRequest)
```
get a customer order by customer order id and return a ResponseEntity of the added CustomerOrderDTO   
URL path: "/customerorder/{customerOrderId}   
HTTP method: GET   
Parameters:   
customerOrderId - The customer order id   
purchaseOrderPageRequest - The PageRequest of purchase order   
orderLinePageRequest - The PageRequest of order line   
Returns:  
a ResponseEntity of the CustomerOrderDTO  
**getCustomerOrderByCustomerIdAndCreateDatetimeRange**
```java
@RequestMapping(method=GET,
                value="/customer/{customerId}")
public org.springframework.http.ResponseEntity
      <java.util.List<CustomerOrderGotDTO>>
getCustomerOrderByCustomerIdAndCreateDatetimeRange(
      @PathVariable(value="customerId") long customerId,
      @RequestParam(value="startDate")
        @DateTimeFormat(pattern="yyyy-MM-dd")
          java.util.Date startDate,
      @RequestParam(value="endDate")
        @DateTimeFormat(pattern="yyyy-MM-dd")
          java.util.Date endDate,
      @Qualifier(value="customer")
        org.springframework.data.domain.Pageable
          customerOrderPageRequest,
      @Qualifier(value="purchase")
        org.springframework.data.domain.Pageable 
          purchaseOrderPageRequest,
      @Qualifier(value="line")
        org.springframework.data.domain.Pageable
          orderLinePageRequest)
```
get a list of customer orders by customer id and create date time range and return a ResponseEntity of a list of the added CustomerOrderDTO   
URL path: "/customerorder/customer/{customerId}   
HTTP method: GET   
Parameters:  
customerId - The customer id  
startDate - The start date  
endDate - The end date  
customerOrderPageRequest - The PageRequest of customer order  
purchaseOrderPageRequest - The PageRequest of purchase order  
orderLinePageRequest - The PageRequest of order line  
Returns:  
a ResponseEntity of a list of the CustomerOrderDTO  
**cancelCustomerOrderByCustomerOrderId**
```java
@RequestMapping(method=HEAD,
                value="/cancel/{customerOrderId}")
public org.springframework.http.ResponseEntity 
cancelCustomerOrderByCustomerOrderId(
    @PathVariable(value="customerOrderId") long customerOrderId)
```
cancel a customer order by customer order id and return a ResponseEntity without response body   
URL path: "/customerorder/cancel/{customerOrderId}  
HTTP method: HEAD  
Parameters:  
customerOrderId - The customer order id  
Returns:  
a ResponseEntity without response body  
* #### PurchaseOrderController

**updatePurchaseOrderStatusToShipping**
```java
@RequestMapping(method=GET,
                value="/shipped/{purchaseOrderId}")
public org.springframework.http.ResponseEntity<PurchaseOrderDTO> 
updatePurchaseOrderStatusToShipping(
      @PathVariable(value="purchaseOrderId") long purchaseOrderId)
```
update a purchase order status to shipping and return a ResponseEntity of the PurchaseOrderDTO  
URL path: "/purchaseorder/shipped/{purchaseOrderId}  
HTTP method: GET  
Parameters:  
purchaseOrderId - The purchase order id  
Returns:  
a ResponseEntity of the PurchaseOrderDTO  
**updatePurchaseOrderStatusToDelivery**
```java
@RequestMapping(method=GET,
                value="/delivered/{purchaseOrderId}")
public org.springframework.http.ResponseEntity<PurchaseOrderDTO> 
updatePurchaseOrderStatusToDelivery(
      @PathVariable(value="purchaseOrderId") long purchaseOrderId)
```
update a purchase order status to delivery and return a ResponseEntity of the PurchaseOrderDTO  
URL path: "/purchaseorder/delivered/{purchaseOrderId}  
HTTP method: GET  
Parameters:  
purchaseOrderId - The purchase order id  
Returns:  
a ResponseEntity of the PurchaseOrderDTO  
**getPurchaseOrderById**
```java
@RequestMapping(method=GET,
                value="/{purchaseOrderId}")
public org.springframework.http.ResponseEntity<PurchaseOrderDTO> 
getPurchaseOrderById(
      @PathVariable(value="purchaseOrderId") long purchaseOrderId,
      org.springframework.data.domain.Pageable pageRequest)
```
get a purchase order by purchase order id and return a ResponseEntity of the PurchaseOrderDTO   
URL path: "/purchaseorder/{purchaseOrderId}   
HTTP method: GET   
Parameters:  
purchaseOrderId - The purchase order id  
pageRequest - The PageRequest of purchase order  
Returns:  
a ResponseEntity of the PurchaseOrderDTO  
**getPurchaseOrderByProviderId**
```java
@RequestMapping(method=GET,
                value="/provider/{providerId}")
public org.springframework.http.ResponseEntity<java.util.List<PurchaseOrderDTO>> 
getPurchaseOrderByProviderId(
      @PathVariable(value="providerId") long providerId,
      @RequestParam(value="startDate") 
        @DateTimeFormat(pattern="yyyy-MM-dd")
          java.util.Date startDate,
      @RequestParam(value="endDate")
        @DateTimeFormat(pattern="yyyy-MM-dd")
          java.util.Date endDate,
      @Qualifier(value="purchase")
        org.springframework.data.domain.Pageable
          purchaseOrderPageRequest,
      @Qualifier(value="line")
        org.springframework.data.domain.Pageable
          orderLinePageRequest)
```
get a list of purchase orders by provider id and create datetime range then return a ResponseEntity of list of the PurchaseOrderDTO   
URL path: "/purchaseorder/provider/{providerId}  
HTTP method: GET  
Parameters:  
providerId - The provider id  
startDate - The start date  
endDate - The end date  
purchaseOrderPageRequest - The PageRequest of purchase order  
orderLinePageRequest - The PageRequest of order line  
Returns:  
a ResponseEntity of list of the PurchaseOrderDTO  
**cancelPurchaseOrderByPurchaseOrderId**
```java
@RequestMapping(method=HEAD,
                value="/cancel/{purchaseOrderId}")
public org.springframework.http.ResponseEntity 
cancelPurchaseOrderByPurchaseOrderId(
      @PathVariable(value="purchaseOrderId") long purchaseOrderId)
```
cancel a purchase order by purchase order id and udpate customer order status if needed then return a ResponseEntity without response body   
URL path: "/purchaseorder/cancel/{purchaseOrderId}  
HTTP method: HEAD  
Parameters:  
purchaseOrderId - The purchase order id  
Returns:  
a ResponseEntity without response body  
* #### WeChatPayController

**callUnifiedOrder**
```java
@RequestMapping(method=GET,
                value="/unifiedorder/{customerOrderId}")
public org.springframework.http.ResponseEntity<java.lang.String> 
callUnifiedOrder(@PathVariable(value="customerOrderId") long customerOrderId)
```
call wechat unified order API and return a ResponseEntity of payment url  
URL path: "/wxpay/unifiedorder/{customerOrderId}  
HTTP method: GET  
Parameters:  
customerOrderId - The customer order id  
Returns:  
a ResponseEntity of payment url  
**unifiedOrderRecallUrl**
```java
@RequestMapping(method=POST,
                value="/unifiedorder/notify",
                consumes="application/xml")
public org.springframework.http.ResponseEntity<java.lang.String> 
unifiedOrderRecallUrl(@RequestBody java.lang.String strXml)
```
callback API of wechat unified order API and return a ResponseEntity of return message  
URL path: "/wxpay/unifiedorder/notify  
HTTP method: POST  
Parameters:  
strXml - The callback message  
Returns:  
a ResponseEntity of return message  
**callOrderQuery**
```java
@RequestMapping(method=GET,
                value="/orderquery/{customerOrderId}")
public org.springframework.http.ResponseEntity<WXOrderDTO> c
allOrderQuery(@PathVariable(value="customerOrderId") long customerOrderId)
```
call wechat order query API and return a ResponseEntity of order details  
URL path: "/wxpay/orderquer/{customerOrderId}  
HTTP method: GET  
Parameters:  
customerOrderId - The CustomerOrderId  
Returns:  
a ResponseEntity of order details  
**callCloseOrder**
```java
@RequestMapping(method=HEAD,
                value="/closeorder/{customerOrderId}")
public org.springframework.http.ResponseEntity<WXOrderDTO> 
callCloseOrder(@PathVariable(value="customerOrderId") long customerOrderId)
```
call wechat close order API and return a ResponseEntity without response body  
URl path: "/wxpay/closeorder/{customerOrderId}  
HTTP method: HEAD  
Parameters:  
customerOrderId - The customer order id  
Returns:  
a ResponseEntity without response body  
 