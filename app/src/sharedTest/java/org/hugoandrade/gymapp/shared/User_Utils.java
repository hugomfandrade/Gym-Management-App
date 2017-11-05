package org.hugoandrade.gymapp.shared;

// imports

import org.hugoandrade.gymapp.data.User;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Shared User testing Utils class
 */
@SuppressWarnings("SameParameterValue")
public class User_Utils {

    static final public String TEST_ID = "id";
    static final public String TEST_USER_ID = "userid";
    static final public String TEST_USERNAME = "username";
    static final public String TEST_PASSWORD = "password";
    static final public String TEST_TOKEN = "token";

    static public User newUser() {
        return new User(TEST_ID, TEST_USERNAME, TEST_USER_ID, TEST_TOKEN);
    }

    static public User newSimpleUser() {
        return new User(TEST_USERNAME, TEST_PASSWORD);
    }

    static public void checkUser(User testUser) {
        assertThat(testUser.getID(), is(TEST_ID));
        assertThat(testUser.getUsername(), is(TEST_USERNAME));
        assertThat(testUser.getUserID(), is(TEST_USER_ID));
        assertThat(testUser.getToken(), is(TEST_TOKEN));
    }

    static public void checkSimpleUser(User testUser) {
        assertThat(testUser.getUsername(), is(TEST_USERNAME));
        assertThat(testUser.getPassword(), is(TEST_PASSWORD));
    }

}
