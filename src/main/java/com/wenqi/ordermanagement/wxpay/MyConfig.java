/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.wxpay;

import com.wenqi.ordermanagement.wxpay.sdk.IWXPayDomain;
import com.wenqi.ordermanagement.wxpay.sdk.WXPayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@SuppressWarnings({"FieldCanBeLocal", "SpellCheckingInspection"})
@Component
public class MyConfig extends WXPayConfig {
    private final IWXPayDomain myDomain;
    private final String appId = "appId";
    private final String mchId = "mchId";
    private final String privateKey = "privateKey";
    private final String notifyUrl = "notifyUrl"; //TODO set notification url
    private final String tradeType = "NATIVE";
    private final String feeType = "CNY";
    private final int httpConnectTimeoutMs = 8000;
    private final int httpReadTimeoutMs = 10000;
    private byte[] certData;

    @Autowired
    public MyConfig(IWXPayDomain myDomain) {
//        String certPath = "/path/to/apiclient_cert.p12";
//        File file = new File(certPath);
//        InputStream certStream = new FileInputStream(file);
//        this.certData = new byte[(int) file.length()];
//        certStream.read(this.certData);
//        certStream.close();
        this.myDomain = myDomain;
    }

    @Override
    public String getAppID() {
        return appId;
    }

    @Override
    public String getMchID() {
        return mchId;
    }

    @Override
    public String getPrivateKey() {
        return privateKey;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public String getTradeType() {
        return tradeType;
    }

    public String getFeeType() {
        return feeType;
    }

    public InputStream getCertStream() {
        return new ByteArrayInputStream(this.certData);
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return httpConnectTimeoutMs;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return httpReadTimeoutMs;
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
        return myDomain;
    }
}
