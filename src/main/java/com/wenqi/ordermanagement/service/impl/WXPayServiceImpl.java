/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenqi.ordermanagement.dto.CustomerOrderGotDTO;
import com.wenqi.ordermanagement.dto.wxdto.WXGoodsDetail;
import com.wenqi.ordermanagement.dto.wxdto.WXOrderDTO;
import com.wenqi.ordermanagement.dto.wxdto.WXOrderDetail;
import com.wenqi.ordermanagement.entity.OrderLine;
import com.wenqi.ordermanagement.exception.ErrorCode;
import com.wenqi.ordermanagement.exception.MyException;
import com.wenqi.ordermanagement.service.CustomerOrderService;
import com.wenqi.ordermanagement.service.OrderLineService;
import com.wenqi.ordermanagement.service.WXPayService;
import com.wenqi.ordermanagement.wxpay.MyConfig;
import com.wenqi.ordermanagement.wxpay.sdk.WXPay;
import com.wenqi.ordermanagement.wxpay.sdk.WXPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WXPayServiceImpl implements WXPayService {
    private final OrderLineService orderLineService;
    private final CustomerOrderService customerOrderService;
    private final MyConfig myConfig;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private WXPay wxPay;

    @Autowired
    public WXPayServiceImpl(MyConfig myConfig, OrderLineService orderLineService, CustomerOrderService customerOrderService) {
        this.myConfig = myConfig;
        this.orderLineService = orderLineService;
        this.customerOrderService = customerOrderService;
        try {
            this.wxPay = new WXPay(myConfig);
        } catch (Exception e) {
            logger.error("Generate a new weixin pay object from configuration failed in weixin pay service.");
            logger.error("Error message is: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String callUnifiedOrder(long customerOrderId) {
        if (customerOrderId == 0) {
            logger.error("The customer order id cannot be 0.");
            throw new MyException(ErrorCode.BAD_REQUEST, "The customer order id cannot be 0.");
        }

        Map<String, String> reqData = this.fillRequestData(customerOrderId);

        Map<String, String> response;
        try {
            response = wxPay.unifiedOrder(reqData);
        } catch (Exception e) {
            logger.error("Calling unified order API failed in weixin pay service.");
            logger.error("Error message is: " + e.getMessage());
            e.printStackTrace();
            throw new MyException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
        this.checkReturnCode(response);
        this.checkResultCode(response);
        customerOrderService.assignWxpayPrepayId(customerOrderId, response.get("prepay_id"));
        return response.get("code_url");
    }

    @Override
    public String dealNotification(String strXml) {
        Map<String, String> data = this.processResponseXml(strXml);
        this.checkReturnCode(data);
        this.checkResultCode(data);
        Map<String, String> returnData = new HashMap<>();
        long customerOrderId = Long.valueOf(data.get("out_trade_no"));

        Pageable pageRequest = PageRequest.of(0, 2000);
        BigDecimal expectedCustomerOrderTotal = customerOrderService.getCustomerOrderIncludePurchaseOrderByCustomerOrderId(customerOrderId, pageRequest, pageRequest).total;
        BigDecimal receivedCustomerOrderTotal = new BigDecimal(data.get("total_fee"));
        if (!expectedCustomerOrderTotal.equals(receivedCustomerOrderTotal)) {
            logger.error("Customer order total fee is incorrect.");
            throw new MyException(ErrorCode.BAD_REQUEST, "Customer order total fee is incorrect.");
        }

        customerOrderService.updateWxpayStatus(customerOrderId, true);
        customerOrderService.assignWxpayTransId(customerOrderId, data.get("transaction_id"));
        returnData.put("return_code", "SUCCESS");
        returnData.put("return_msg", "OK");
        return this.mapToXml(returnData);
    }

    @Override
    public WXOrderDTO callOrderQuery(long customerOrderId) {
        if (customerOrderId == 0) {
            logger.error("The customer order id cannot be 0.");
            throw new MyException(ErrorCode.BAD_REQUEST, "The customer order id cannot be 0.");
        }
        Map<String, String> reqData = new HashMap<>();
        String wxpayTransId = customerOrderService.getWxpayTransId(customerOrderId);
        if (wxpayTransId == null) {
            reqData.put("out_trade_no", String.valueOf(customerOrderId));
        } else {
            reqData.put("transaction_id", wxpayTransId);
        }
        Map<String, String> response;
        try {
            response = wxPay.orderQuery(reqData);
        } catch (Exception e) {
            logger.error("Calling order query API failed in weixin pay service.");
            logger.error("Error message is: " + e.getMessage());
            e.printStackTrace();
            throw new MyException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
        this.checkReturnCode(response);
        this.checkResultCode(response);
        this.checkOrderId(response.get("out_trade_no"), customerOrderId);
        return new WXOrderDTO(response);
    }

    @Override
    public void callCloseOrder(long customerOrderId) {
        if (customerOrderId == 0) {
            logger.error("The customer order id cannot be 0.");
            throw new MyException(ErrorCode.BAD_REQUEST, "The customer order id cannot be 0.");
        }
        Map<String, String> reqData = new HashMap<>();
        reqData.put("out_trade_no", String.valueOf(customerOrderId));
        Map<String, String> response;
        try {
            response = wxPay.closeOrder(reqData);
        } catch (Exception e) {
            logger.error("Calling close order API failed in weixin pay service.");
            logger.error("Error message is: " + e.getMessage());
            e.printStackTrace();
            throw new MyException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
        this.checkReturnCode(response);
        this.checkResultCode(response);
    }

    private Map<String, String> fillRequestData(long customerOrderId) {
        Map<String, String> reqData = new HashMap<>();
        Pageable pageRequest = PageRequest.of(0, 2000);
        List<OrderLine> orderLineList = orderLineService.getOrderLineByCustomerOrderId(customerOrderId, pageRequest);

        if (orderLineList.size() == 0) {
            logger.error("The customer order(id: " + customerOrderId + ") doesn't contain any order line.");
            throw new MyException(ErrorCode.BAD_REQUEST,
                    "The customer order(id: " + customerOrderId + ") doesn't contain any order line.");
        } else if (orderLineList.size() > 1) {
            reqData.put("body", "multiple product");
        } else {
            long productId = orderLineList.get(0).getProductId();
            reqData.put("body", "single product-" + productId);//TODO product name
        }

        List<WXGoodsDetail> wxGoodsDetailList = new ArrayList<>();
        for (OrderLine orderLine : orderLineList) {
            WXGoodsDetail wxGoodsDetail = new WXGoodsDetail();
            wxGoodsDetail.goods_id = String.valueOf(orderLine.getProductId());
            wxGoodsDetail.goods_name = "product name";//TODO add product name
            wxGoodsDetail.quantity = orderLine.getQuantity();
            wxGoodsDetail.price = orderLine.getUnitPrice().multiply(new BigDecimal(100)).intValue();
            wxGoodsDetailList.add(wxGoodsDetail);
        }
        WXOrderDetail wxOrderDetail = new WXOrderDetail(wxGoodsDetailList);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            reqData.put("detail", mapper.writeValueAsString(wxOrderDetail));
        } catch (JsonProcessingException e) {
            logger.error("Convert wxOrderDetail to map object failed in weixin pay service");
            logger.error("Error message is: " + e.getMessage());
            e.printStackTrace();
            throw new MyException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
        //TODO add additional information reqData.put("attach", )

        CustomerOrderGotDTO gotCustomerOrderDTO = customerOrderService.getCustomerOrderIncludePurchaseOrderByCustomerOrderId(customerOrderId, pageRequest, pageRequest);
        reqData.put("out_trade_no", String.valueOf(customerOrderId));
        reqData.put("fee_type", myConfig.getFeeType());
        reqData.put("total_fee", String.valueOf(gotCustomerOrderDTO.total.multiply(new BigDecimal(100)).intValue()));

        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("Get local host ip address failed in weixin pay service.");
            logger.error("Error message is: " + e.getMessage());
            e.printStackTrace();
            throw new MyException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
        reqData.put("spbill_create_ip", ip);

//        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//        Date startTime = gotCustomerOrderDTO.createDatetime;
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(startTime);
//        calendar.add(Calendar.HOUR_OF_DAY, 1);
//        Date expireTime = calendar.getTime();
//        String startTimeString = dateFormat.format(startTime);
//        String expireTimeString = dateFormat.format(expireTime);
//        reqData.put("time_start", startTimeString);
//        reqData.put("time_expire", expireTimeString);

        //TODO add coupon information reqData.put("goods_tag", "")
        reqData.put("notify_url", myConfig.getNotifyUrl());
        reqData.put("trade_type", myConfig.getTradeType());
        reqData.put("product_id", String.valueOf(customerOrderId));
        return reqData;
    }

    private Map<String, String> processResponseXml(String strXml) {
        Map<String, String> returnMap = null;
        try {
            returnMap = wxPay.processResponseXml(strXml);
        } catch (Exception e) {
            logger.error("Processing response xml failed.");
            logger.error("Error message is: " + e.getMessage());
            e.printStackTrace();
        }
        return returnMap;
    }

    private String mapToXml(Map<String, String> data) {
        String returnStr;
        try {
            returnStr = WXPayUtil.mapToXml(data);
        } catch (Exception e) {
            logger.error("Convert Map to XML failed in weixin pay service.");
            logger.error("Error message is: " + e.getMessage());
            e.printStackTrace();
            throw new MyException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
        return returnStr;
    }

    private void checkReturnCode(Map<String, String> data) {
        if (!data.get("return_code").equals("SUCCESS")) {
            logger.error("Calling API failed.");
            logger.error("Return code is FAIL.");
            logger.error("Return message is: " + data.get("return_msg"));
            throw new MyException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void checkResultCode(Map<String, String> data) {
        if (!data.get("result_code").equals("SUCCESS")) {
            logger.error("Transaction failed.");
            logger.error("Error code is: " + data.get("err_code"));
            logger.error("Error code description is: " + data.get("err_code_des"));
            throw new MyException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void checkOrderId(String receivedOrderId, long expectedOrderId) {
        if (!receivedOrderId.equals(String.valueOf(expectedOrderId))) {
            logger.error("Received customer order id is incorrect.");
            throw new MyException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
