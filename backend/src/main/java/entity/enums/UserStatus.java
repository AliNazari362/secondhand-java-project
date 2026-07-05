package entity.enums;

/**
 * Represents the account status of a user on the platform.
 * Controls whether a user is allowed to log in and perform actions.
 */
public enum UserStatus {

    /** The user's account is active and fully functional. */
    ACTIVE,

    /** The user's account has been banned by an admin; login and actions are blocked. */
    BANNED
}
