package org.hugoandrade.gymapp.view;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.InputType;
import android.widget.Toolbar;

import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.view.admin.CreateExerciseActivity;
import org.hugoandrade.gymapp.view.admin.ExerciseListActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withInputType;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Admin_Test {

    @Rule
    public ActivityTestRule<ExerciseListActivity> mActivityRule
            = new ActivityTestRule<>(ExerciseListActivity.class);

    @Before
    public void stubExerciseIntent() {

        Intent intent = new Intent()
                .putExtra( "intent_extra_exercise", new Exercise("id", "exercise_name"));
        Instrumentation.ActivityResult result
                = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);

        intending(hasComponent(CreateExerciseActivity.class.getName())).respondWith(result);
    }

    /**
     * Test to check if exercise is listed
     */
    @Test
    public void isThereAPlaceForUserToEnterTheirUsername() {
        Toolbar toolbar = (Toolbar) mActivityRule.getActivity().findViewById(R.id.toolbar);

        onView(withId(R.id.action_add_exercise)).perform(click());

        // Assert that the data we set up above is shown.
        /*onView(withRecyclerView(R.id.scroll_view).atPosition(3))
                .check(matches(hasDescendant(withText("Some content"))));
        onView(withId(R.id.phoneNumber)).check(matches(withText(phoneNumber)));

        mActivityRule.getActivity().onActivityResult()
        onView(ViewMatchers.withId(R.id.et_username)).check(matches(isDisplayed()));/**/
    }
}
