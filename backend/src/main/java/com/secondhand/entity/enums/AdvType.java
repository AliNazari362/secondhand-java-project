package com.secondhand.entity.enums;

import com.secondhand.entity.Adv;

/**
 * Discriminates the concrete subtype of an advertisement.
 * Drives the JOINED inheritance strategy used by {@link Adv}.
 */
public enum AdvType {

    /** Advertisement offering a physical product for sale. */
    PRODUCT,

    /** Advertisement offering a com.secondhand.service (hourly, daily, fixed-price, etc.). */
    SERVICE
}
