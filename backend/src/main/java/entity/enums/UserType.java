package entity.enums;

/**
 * Defines the role of a person in the system.
 * Drives authorization decisions and access control throughout the platform.
 */
public enum UserType {

    /** A regular registered user who can post advertisements and interact with the platform. */
    USER,

    /** A platform administrator with elevated privileges to moderate content and manage users. */
    ADMIN
}
