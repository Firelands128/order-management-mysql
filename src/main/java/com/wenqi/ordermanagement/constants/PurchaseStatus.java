/*
 * Created by Wenqi Li <Firelands128@gmail.com>
 * Copyright (C) 2019.
 */

package com.wenqi.ordermanagement.constants;

public enum PurchaseStatus {
    AWAITING_PAYMENT,       //The purchase order is waiting for payment.
    AWAITING_SHIPPING,      //The payment has been confirmed, but provider has not yet shipped.
    SHIPPING,               //The order items has been shipped, but not yet delivery.
    DELIVERED,               //The order has been completed.
    CANCELLED              //The order has been cancelled.
}
