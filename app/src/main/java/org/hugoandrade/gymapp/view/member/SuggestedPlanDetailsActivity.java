package org.hugoandrade.gymapp.view.member;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.ExercisePlanSuggested;
import org.hugoandrade.gymapp.presenter.member.SuggestedPlanDetailsPresenter;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.ActivityBase;
import org.hugoandrade.gymapp.view.adapter.ExerciseSetListAdapter;
import org.hugoandrade.gymapp.view.dialog.SimpleBuilderDialog;

public class SuggestedPlanDetailsActivity extends ActivityBase<MVP.RequiredSuggestedPlanDetailsViewOps,
                                                     MVP.ProvidedSuggestedPlanDetailsPresenterOps,
        SuggestedPlanDetailsPresenter>

        implements MVP.RequiredSuggestedPlanDetailsViewOps {

    /**
     * Constant that represents the name of the intent extra that is paired
     * with a ExercisePlanSuggested object.
     */
    private static final String INTENT_EXTRA_SUGGESTED_EXERCISE_PLAN = "intent_extra_suggested_exercise_plan";

    /**
     * View showing "Waiting" and disables UI to be displayed when waiting
     * for the response of a Web request
     */
    private View vProgressBar;

    /**
     * The Suggested Exercise Plan of this instance
     */
    private ExercisePlanSuggested mExercisePlanSuggested;

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context, ExercisePlanSuggested suggestedPlan) {
        return new Intent(context, SuggestedPlanDetailsActivity.class)
                .putExtra(INTENT_EXTRA_SUGGESTED_EXERCISE_PLAN, suggestedPlan);
    }

    /**
     * Method used to extract a ExerciseRecord object from an Intent
     */
    public static ExercisePlanSuggested extractExercisePlanSuggestedFromIntent(Intent data) {
        return data.getParcelableExtra(INTENT_EXTRA_SUGGESTED_EXERCISE_PLAN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // extract suggested exercise plan to display
        mExercisePlanSuggested = extractExercisePlanSuggestedFromIntent(getIntent());

        initializeUI();

        super.onCreate(SuggestedPlanDetailsPresenter.class, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_suggested_plan, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_dismiss: {
                // Member wants to dismiss suggested plan
                String title = "Dismiss Suggestion";
                String message = "Are you sure you want to dismiss this plan?";
                SimpleBuilderDialog builderDialog =
                        new SimpleBuilderDialog(getActivityContext(), title, message);
                builderDialog.setOnDialogResultListener(new SimpleBuilderDialog.OnDialogResultListener() {
                    @Override
                    public void onResult(DialogInterface dialog, @SimpleBuilderDialog.Result int result) {
                        if (result == SimpleBuilderDialog.YES) {
                            // YES clicked. add member to My Members' list
                            getPresenter().dismissSuggestedPlan(mExercisePlanSuggested, false);
                        }
                        dialog.dismiss();
                    }
                });
                return true;
            }
            case R.id.action_done: {
                // Member wants to dismiss and set as done the suggested plan
                // Member wants to dismiss suggested plan
                String title = "Accept Suggestion";
                String message = "Are you sure you want to mark this plan as done?";
                SimpleBuilderDialog builderDialog =
                        new SimpleBuilderDialog(getActivityContext(), title, message);
                builderDialog.setOnDialogResultListener(new SimpleBuilderDialog.OnDialogResultListener() {
                    @Override
                    public void onResult(DialogInterface dialog, @SimpleBuilderDialog.Result int result) {
                        if (result == SimpleBuilderDialog.YES) {
                            // YES clicked. add member to My Members' list
                            getPresenter().dismissSuggestedPlan(mExercisePlanSuggested, true);
                        }
                        dialog.dismiss();
                    }
                });
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_member_suggested_plan);

        // set up toolbar and title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Suggested Plan");

        vProgressBar = findViewById(R.id.progressBar_waiting);

        Log.e(TAG, Integer.toString(mExercisePlanSuggested.getExerciseSetList().size()));
        // set up recycler view and adapter to display the exercise plan record
        ExerciseSetListAdapter mExerciseSetListAdapter = new ExerciseSetListAdapter(
                ExerciseSetListAdapter.MODE_DISPLAY,
                mExercisePlanSuggested);//.getAsExercisePlan());
        RecyclerView rvExercisePlan = (RecyclerView) findViewById(R.id.rv_exercise_plan);
        rvExercisePlan.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvExercisePlan.setAdapter(mExerciseSetListAdapter);
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
    public void suggestedPlanDismissed(ExercisePlanSuggested suggestedPlan) {
        setResult(Activity.RESULT_OK, new Intent().putExtra(INTENT_EXTRA_SUGGESTED_EXERCISE_PLAN, suggestedPlan));
        finish();
    }

    @Override
    public void reportMessage(String message) {
        UIUtils.showSnackBar(findViewById(android.R.id.content), message);
    }
}
