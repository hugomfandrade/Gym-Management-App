package org.hugoandrade.gymapp.shared;

// imports

import org.hugoandrade.gymapp.data.WaitingUser;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Shared WaitingUser testing Utils class
 */
@SuppressWarnings("SameParameterValue")
public class WaitingUser_Utils {

    static final public String TEST_ID_1 = "id_1";
    static final public String TEST_ID_2 = "id_2";
    static final public String TEST_USERNAME = "username";
    static final public String TEST_CREDENTIAL = "credential";
    static final public String TEST_CODE = "code";

    static public WaitingUser newWaitingUser(String id) {
        return new WaitingUser(id, TEST_USERNAME, TEST_CREDENTIAL, TEST_CODE);
    }

    static public void checkWaitingUser(WaitingUser testWaitingUser, String id) {
        assertThat(testWaitingUser.getID(), is(id));
        assertThat(testWaitingUser.getUsername(), is(TEST_USERNAME));
        assertThat(testWaitingUser.getCredential(), is(TEST_CREDENTIAL));
        assertThat(testWaitingUser.getCode(), is(TEST_CODE));
    }
}
