package org.hugoandrade.gymapp.view;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.InputType;

import org.hugoandrade.gymapp.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withInputType;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Login_Test {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    private String mUsernameToBeTyped;
    private String mUsernameShortToBeTyped;
    private String mUsernameAllSpacesToBeTyped;
    private String mPasswordToBeTyped;
    private String mPasswordShortToBeTyped;
    private String mPasswordAllSpacesToBeTyped;

    @Before
    public void setupStrings() {
        // Specify strings for unit tests.
        mUsernameToBeTyped = "example";
        mUsernameShortToBeTyped = "exp";
        mUsernameAllSpacesToBeTyped = "            ";
        mPasswordToBeTyped = "password";
        mPasswordShortToBeTyped = "pwd";
        mPasswordAllSpacesToBeTyped = "          ";
    }

    /**
     * Test to check if there is a place for
     * the user to enter their username
     */
    @Test
    public void isThereAPlaceForUserToEnterTheirUsername() {
        onView(ViewMatchers.withId(R.id.et_username)).check(matches(isDisplayed()));
    }

    /**
     * Test to check if there is a place for
     * the user to enter their password
     */
    @Test
    public void isThereAPlaceForUserToEnterTheirPassword() {
        onView(withId(R.id.et_password)).check(matches(isDisplayed()));
    }

    /**
     * Test to check if the password is not
     * displayed as plain test
     */
    @Test
    public void checkPasswordIsNotDisplayedAsPlainText() {
        onView(allOf(withId(R.id.et_password), withInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)))
                .check(matches(isDisplayed()));
    }

    /**
     * Test to check if the login button is not clickable.
     */
    @Test
    public void loginWithShortPassword() {
        // Type text and then press the button.
        onView(withId(R.id.et_username)).perform(clearText());
        onView(withId(R.id.et_password)).perform(clearText());
        onView(withId(R.id.et_username))
                .perform(typeText(mUsernameToBeTyped));
        onView(withId(R.id.et_password))
                .perform(typeText(mPasswordShortToBeTyped));
        onView(withId(R.id.tv_login)).check(matches(not(isClickable())));
    }

    /**
     * Test to check if the login button is not clickable.
     */
    @Test
    public void loginWithAllSpacesPassword() {
        // Type text and then press the button.
        onView(withId(R.id.et_username)).perform(clearText());
        onView(withId(R.id.et_password)).perform(clearText());
        onView(withId(R.id.et_username))
                .perform(typeText(mUsernameToBeTyped));
        onView(withId(R.id.et_password))
                .perform(typeText(mPasswordAllSpacesToBeTyped));
        onView(withId(R.id.tv_login)).check(matches(not(isClickable())));
    }

    /**
     * Test to check if the login button is not clickable.
     */
    @Test
    public void loginWithShortUsername() {
        // Type text and then press the button.
        onView(withId(R.id.et_username)).perform(clearText());
        onView(withId(R.id.et_password)).perform(clearText());
        onView(withId(R.id.et_username))
                .perform(typeText(mUsernameShortToBeTyped));
        onView(withId(R.id.et_password))
                .perform(typeText(mPasswordToBeTyped));
        onView(withId(R.id.tv_login)).check(matches(not(isClickable())));
    }

    /**
     * Test to check if the login button is not clickable.
     */
    @Test
    public void loginAllSpacesUsername() {
        // Type text and then press the button.
        onView(withId(R.id.et_username)).perform(clearText());
        onView(withId(R.id.et_password)).perform(clearText());
        onView(withId(R.id.et_username))
                .perform(typeText(mUsernameAllSpacesToBeTyped));
        onView(withId(R.id.et_password))
                .perform(typeText(mPasswordToBeTyped));
        onView(withId(R.id.tv_login)).check(matches(not(isClickable())));
    }

    /**
     * Test to check if the login button is clickable.
     */
    @Test
    public void loginWithValidCredentials() {
        // Type text and then press the button.
        onView(withId(R.id.et_username)).perform(clearText());
        onView(withId(R.id.et_password)).perform(clearText());
        onView(withId(R.id.et_username))
                .perform(typeText(mUsernameToBeTyped));
        onView(withId(R.id.et_password))
                .perform(typeText(mPasswordToBeTyped));
        onView(withId(R.id.tv_login)).check(matches(isClickable()));
    }
}
