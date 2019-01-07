/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.service;

import com.wenqi.ordermanagement.dto.wxdto.WXOrderDTO;

public interface WXPayService {

    String callUnifiedOrder(long customerOrderId);

    String dealNotification(String strXml);

    WXOrderDTO callOrderQuery(long customerOrderId);

    void callCloseOrder(long customerOrderId);
}