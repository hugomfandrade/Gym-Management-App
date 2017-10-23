package org.hugoandrade.gymapp.utils;

/**
 * Class containing static helper methods to perform login related tasks.
 */
public final class LoginUtils {
    /**
     * Ensure this class is only used as a utility.
     */
    private LoginUtils() {
        throw new AssertionError();
    }

    /**
     * This method returns true if the username is at least 4 characters long.
     */
    public static boolean isUsernameAtLeast4CharactersLong(String username) {
        return username != null && username.length() >= 4;
    }

    /**
     * This method returns true if the username is not all spaces.
     */
    public static boolean isUsernameNotAllSpaces(String username) {
        return username != null && username.trim().length() > 0;
    }

    /**
     * This method returns true if the password is at least 8 characters long.
     */
    public static boolean isPasswordAtLeast4CharactersLong(String password) {
        return password != null && password.length() >= 4;
    }

    /**
     * This method returns true if the password is not all spaces.
     */
    public static boolean isPasswordNotAllSpaces(String password) {
        return password != null && password.trim().length() > 0;
    }
}
