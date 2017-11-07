package org.hugoandrade.gymapp.view.staffmember;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.ExercisePlanRecord;
import org.hugoandrade.gymapp.view.adapter.ExerciseSetListAdapter;

public class WorkoutActivity extends AppCompatActivity {

    /**
     * Constant that represents the name of the intent extra that is paired
     * with a ExercisePlanRecord object.
     */
    private static final String INTENT_EXTRA_EXERCISE_PLAN_RECORD = "intent_extra_exercise_plan_record";

    /**
     * The Exercise Plan of this instance
     */
    private ExercisePlanRecord mExercisePlan;

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context, ExercisePlanRecord exercisePlanRecord) {
        return new Intent(context, WorkoutActivity.class)
                .putExtra(INTENT_EXTRA_EXERCISE_PLAN_RECORD, exercisePlanRecord);
    }

    /**
     * Method used to extract a ExercisePlanRecord object from an Intent
     */
    public static ExercisePlanRecord extractExercisePlanFromIntent(Intent data) {
        return data.getParcelableExtra(INTENT_EXTRA_EXERCISE_PLAN_RECORD);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // extract exercise plan to display
        mExercisePlan = extractExercisePlanFromIntent(getIntent());

        initializeUI();
    }

    private void initializeUI() {
        setContentView(R.layout.activity_member_workout);

        // set up toolbar and title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Workout Details");

        // set up recycler view and adapter to display the exercise plan record
        ExerciseSetListAdapter mExerciseSetListAdapter = new ExerciseSetListAdapter(ExerciseSetListAdapter.MODE_DISPLAY, mExercisePlan);
        RecyclerView rvExercisePlan = (RecyclerView) findViewById(R.id.rv_exercise_plan);
        rvExercisePlan.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvExercisePlan.setAdapter(mExerciseSetListAdapter);
    }
}
