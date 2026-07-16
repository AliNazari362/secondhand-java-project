package Entity.enums;

import Entity.Adv;

/**
 * Discriminates the concrete subtype of an advertisement.
 * Drives the JOINED inheritance strategy used by {@link Adv}.
 */
public enum AdvType {

    /** Advertisement offering a physical product for sale. */
    PRODUCT,

    /** Advertisement offering a service (hourly, daily, fixed-price, etc.). */
    SERVICE
}
