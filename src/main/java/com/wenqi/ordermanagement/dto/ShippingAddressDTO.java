/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.dto;

public class ShippingAddressDTO {
    public String addressLine1;
    public String addressLine2;
    public String city;
    public String province;
    public String country;
    public String phone;

    @Override
    public String toString() {
        return "ShippingAddressDTO{" +
                "addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", country='" + country + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
