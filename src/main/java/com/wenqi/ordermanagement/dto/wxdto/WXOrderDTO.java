/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.dto.wxdto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@SuppressWarnings({"WeakerAccess"})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class WXOrderDTO {
    public final Long customerOrderId;
    public final String wxpayTransId;
    public final String tradeState;
    public final String tradeStateDesc;
    public final String bankType;
    public final Double total;
    public final String attach;
    public Date timeEnd;

    public WXOrderDTO(Map<String, String> data) {
        this.customerOrderId = Long.valueOf(data.get("out_trade_no"));
        this.wxpayTransId = data.get("transaction_id");
        this.tradeState = data.get("trade_state");
        this.tradeStateDesc = data.get("trade_state_desc");
        this.bankType = data.get("bank_type");
        this.total = data.get("total_fee") == null ? null : Double.valueOf(data.get("total_fee")) / 100;
        this.attach = data.get("attach");
        if (data.get("time_end") != null) {
            this.timeEnd = new SimpleDateFormat("yyyyMMddHHmmss").parse(data.get("time_end"), new ParsePosition(0));
        }

    }

    @Override
    public String toString() {
        return "WXOrderDTO{" +
                "customerOrderId=" + customerOrderId +
                ", wxpayTransId='" + wxpayTransId + '\'' +
                ", tradeState='" + tradeState + '\'' +
                ", tradeStateDesc='" + tradeStateDesc + '\'' +
                ", bankType='" + bankType + '\'' +
                ", total=" + total +
                ", attach='" + attach + '\'' +
                ", timeEnd=" + timeEnd +
                '}';
    }
}
