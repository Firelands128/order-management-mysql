/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.constants;

public enum CustomerStatus {
    UNPAID,                 //The order has been created, but not paid yet.
    PAID,                   //The order has been paid.
    CANCELLED              //The order has been cancelled.
}
