/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.dto.wxdto;

import com.fasterxml.jackson.annotation.JsonInclude;

@SuppressWarnings({"WeakerAccess"})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class WXGoodsDetail {
    public String goods_id;
    public String wxpay_goods_id;
    public String goods_name;
    public Integer quantity;
    public Integer price;

    @Override
    public String toString() {
        return "WXGoodsDetail{" +
                "goods_id='" + goods_id + '\'' +
                ", wxpay_goods_id='" + wxpay_goods_id + '\'' +
                ", goods_name='" + goods_name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
