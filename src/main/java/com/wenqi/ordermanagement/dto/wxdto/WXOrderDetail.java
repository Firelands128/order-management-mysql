/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.dto.wxdto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@SuppressWarnings({"WeakerAccess"})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class WXOrderDetail {
    public final List<WXGoodsDetail> goodsDetails;
    public Integer cost_price;
    public Integer receipt_id;

    public WXOrderDetail(List<WXGoodsDetail> wxGoodsDetailList) {
        this.goodsDetails = wxGoodsDetailList;
    }

    @Override
    public String toString() {
        return "WXOrderDetail{" +
                "goodsDetails=" + goodsDetails +
                ", cost_price=" + cost_price +
                ", receipt_id=" + receipt_id +
                '}';
    }
}
