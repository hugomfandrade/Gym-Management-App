package org.hugoandrade.gymapp.view.staffmember;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlan;
import org.hugoandrade.gymapp.data.ExercisePlanSuggested;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.presenter.BuildWorkoutPresenter;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.ActivityBase;
import org.hugoandrade.gymapp.view.adapter.ExerciseSetListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BuildWorkoutActivity extends ActivityBase<MVP.RequiredBuildWorkoutViewOps,
                                                       MVP.ProvidedBuildWorkoutPresenterOps,
                                                       BuildWorkoutPresenter>

        implements MVP.RequiredBuildWorkoutViewOps {

    /**
     * Constant that represents the name of the intent extra that is paired
     * with a User object that represents the selected user.
     */
    private static final String INTENT_EXTRA_USER = "intent_extract_user";

    /**
     * Constant that represents the request code used to start the "build exercise plan" activity.
     */
    private static final int BUILD_EXERCISE_PLAN_REQUEST_CODE = 250;

    /**
     * List of all exercises
     */
    private List<Exercise> mExerciseList = new ArrayList<>();

    /**
     * The Exercise Plan of this instance
     */
    private ExercisePlan mExercisePlan;

    /**
     * The selected user
     */
    private User mMember;

    /**
     * View showing "Waiting" and disables UI to be displayed when waiting
     * for the response of a Web request
     */
    private View vProgressBar;

    /**
     * Adapter used to display the Exercise Plan (list of Exercise Set)
     */
    private ExerciseSetListAdapter mExerciseSetListAdapter;

    /**
     * TextView to be displayed when ExercisePlan is empty
     */
    private TextView tvStartBuilding;

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context, User member) {
        return new Intent(context, BuildWorkoutActivity.class)
                .putExtra(INTENT_EXTRA_USER, member);
    }

    /**
     * Method used to extract the selected User from an Intent
     */
    public static User extractUserFromIntent(Intent data) {
        return data.getParcelableExtra(INTENT_EXTRA_USER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // extract the selected User
        mMember = extractUserFromIntent(getIntent());

        // build empty exercise plan
        mExercisePlan = ExercisePlan.empty(mMember, Calendar.getInstance());

        initializeUI();

        super.onCreate(BuildWorkoutPresenter.class, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_workout, menu);

        // show menu item only if exercise plan is not empty
        menu.findItem(R.id.action_save_workout).setVisible(
                mExercisePlan.getExerciseSetList() != null &&
                mExercisePlan.getExerciseSetList().size() != 0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_workout: {
                // The way it is saved depends on the logged in User
                if (GlobalData.getUser().getCredential().equals(User.Credential.STAFF))
                    // save the suggested exercise plan
                    getPresenter().createSuggestedWorkout(
                            new ExercisePlanSuggested(GlobalData.getUser(), mExercisePlan));
                else if (GlobalData.getUser().getCredential().equals(User.Credential.MEMBER))
                    // save the exercise plan
                    getPresenter().createWorkout(mExercisePlan);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_member_build_workout);

        // set up toolbar and title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Build Workout");

        vProgressBar = findViewById(R.id.progressBar_waiting);

        // set up recycler view and adapter to display the exercise plan record
        mExerciseSetListAdapter = new ExerciseSetListAdapter(ExerciseSetListAdapter.MODE_DISPLAY, mExercisePlan);
        RecyclerView rvExercisePlan = (RecyclerView) findViewById(R.id.rv_exercise_plan);
        rvExercisePlan.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvExercisePlan.setAdapter(mExerciseSetListAdapter);

        // set up message to start building the exercise plan
        tvStartBuilding = (TextView) findViewById(R.id.tv_create_exercise);
        tvStartBuilding.setText(R.string.action_click_to_start);
        tvStartBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createExercisePlan();
            }
        });
    }

    private void createExercisePlan() {
        Bundle options = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions
                    .makeSceneTransitionAnimation(BuildWorkoutActivity.this).toBundle();
        }

        startActivityForResult(
                SelectExercisesActivity.makeIntent(this, mExerciseList, mMember),
                BUILD_EXERCISE_PLAN_REQUEST_CODE,
                options);
    }

    @Override
    public void disableUI() {
        vProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void enableUI() {
        vProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setExerciseList(List<Exercise> exerciseList) {
        mExerciseList.clear();
        mExerciseList.addAll(exerciseList);
    }

    @Override
    public void exercisePlanCreated(ExercisePlan exercisePlan) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BUILD_EXERCISE_PLAN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Successfully built a exercise plan.
                // Extract the new exercise plan and display it

                mExercisePlan = SelectExercisesActivity.extractExercisePlanFromIntent(data);
                mExerciseSetListAdapter.set(mExercisePlan);

                // hide "start building" message
                tvStartBuilding.setVisibility(View.GONE);
            }

            invalidateOptionsMenu();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void reportMessage(String message) {
        UIUtils.showSnackBar(findViewById(android.R.id.content), message);
    }
}
