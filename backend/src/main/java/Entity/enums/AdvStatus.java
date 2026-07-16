package Entity.enums;

/**
 * Represents the lifecycle status of an advertisement.
 * Controls visibility and workflow transitions throughout the platform.
 */
public enum AdvStatus {

    /** Advertisement submitted by the user and awaiting admin review. */
    PENDING,

    /** Advertisement approved by an admin and publicly visible on the platform. */
    ACTIVE,

    /** Advertisement rejected by an admin; rejection explanation should be provided. */
    REJECTED,

    /** The advertised product or service has been sold/fulfilled; no longer available. */
    SOLD,

    /** Advertisement was removed by the owner or an admin; hidden from public listings. */
    DELETED
}
