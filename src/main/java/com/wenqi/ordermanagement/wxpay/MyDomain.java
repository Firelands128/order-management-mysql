/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.wxpay;

import com.wenqi.ordermanagement.wxpay.sdk.IWXPayDomain;
import com.wenqi.ordermanagement.wxpay.sdk.WXPayConfig;
import com.wenqi.ordermanagement.wxpay.sdk.WXPayConstants;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"FieldCanBeLocal", "SpellCheckingInspection"})
@Component
public class MyDomain implements IWXPayDomain {
    private final int MIN_SWITCH_PRIMARY_MSEC = 3 * 60 * 1000;  //3 minutes
    private final Map<String, DomainStatics> domainData = new HashMap<>();
    private long switchToAlternateDomainTime = 0;

    public synchronized void report(final String domain, long elapsedTimeMillis, final Exception ex) {
        DomainStatics info = domainData.get(domain);
        if (info == null) {
            info = new DomainStatics(domain);
            domainData.put(domain, info);
        }

        if (ex == null) { //success
            if (info.succCount >= 2) {    //continue succ, clear error count
                info.connectTimeoutCount = info.dnsErrorCount = info.otherErrorCount = 0;
            } else {
                ++info.succCount;
            }
        } else if (ex instanceof ConnectTimeoutException) {
            info.succCount = info.dnsErrorCount = 0;
            ++info.connectTimeoutCount;
        } else if (ex instanceof UnknownHostException) {
            info.succCount = 0;
            ++info.dnsErrorCount;
        } else {
            info.succCount = 0;
            ++info.otherErrorCount;
        }
    }

//    private MyDomain(){}
//    private static class WxpayDomainHolder{
//        private static IWXPayDomain holder = new MyDomain();
//    }
//    public static IWXPayDomain instance(){
//        return WxpayDomainHolder.holder;
//    }

//    public MyDomain() {
//
//    }

    @Override
    public synchronized DomainInfo getDomain(final WXPayConfig config) {
        DomainStatics primaryDomain = domainData.get(WXPayConstants.DOMAIN_API);
        if (primaryDomain == null ||
                primaryDomain.isGood()) {
            return new DomainInfo(WXPayConstants.DOMAIN_API, true);
        }

        long now = System.currentTimeMillis();
        if (switchToAlternateDomainTime == 0) {   //first switch
            switchToAlternateDomainTime = now;
            return new DomainInfo(WXPayConstants.DOMAIN_API2, false);
        } else if (now - switchToAlternateDomainTime < MIN_SWITCH_PRIMARY_MSEC) {
            DomainStatics alternateDomain = domainData.get(WXPayConstants.DOMAIN_API2);
            if (alternateDomain == null ||
                    alternateDomain.isGood() ||
                    alternateDomain.badCount() < primaryDomain.badCount()) {
                return new DomainInfo(WXPayConstants.DOMAIN_API2, false);
            } else {
                return new DomainInfo(WXPayConstants.DOMAIN_API, true);
            }
        } else {  //force switch back
            switchToAlternateDomainTime = 0;
            primaryDomain.resetCount();
            DomainStatics alternateDomain = domainData.get(WXPayConstants.DOMAIN_API2);
            if (alternateDomain != null) {
                alternateDomain.resetCount();
            }
            return new DomainInfo(WXPayConstants.DOMAIN_API, true);
        }
    }

    static class DomainStatics {
        final String domain;
        int succCount = 0;
        int connectTimeoutCount = 0;
        int dnsErrorCount = 0;
        int otherErrorCount = 0;

        DomainStatics(String domain) {
            this.domain = domain;
        }

        void resetCount() {
            succCount = connectTimeoutCount = dnsErrorCount = otherErrorCount = 0;
        }

        boolean isGood() {
            return connectTimeoutCount <= 2 && dnsErrorCount <= 2;
        }

        int badCount() {
            return connectTimeoutCount + dnsErrorCount * 5 + otherErrorCount / 4;
        }
    }
}
