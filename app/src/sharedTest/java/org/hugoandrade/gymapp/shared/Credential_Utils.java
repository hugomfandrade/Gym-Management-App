package org.hugoandrade.gymapp.shared;

// imports

import org.hugoandrade.gymapp.data.Credential;
import org.hugoandrade.gymapp.data.User;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Shared Credential testing Utils class
 */
@SuppressWarnings("SameParameterValue")
public class Credential_Utils {

    static final public String TEST_ID_1 = "id_1";
    static final public String TEST_ID_2 = "id_2";
    static final public String TEST_USERNAME = "username";
    static final public String TEST_CREDENTIAL = "credential";

    static public Credential newCredential(String id) {
        return new Credential(id, TEST_USERNAME, TEST_CREDENTIAL);
    }

    static public void checkCredential(Credential testCredential, String id) {
        assertThat(testCredential.getID(), is(id));
        assertThat(testCredential.getUsername(), is(TEST_USERNAME));
        assertThat(testCredential.getCredential(), is(TEST_CREDENTIAL));
    }

}
