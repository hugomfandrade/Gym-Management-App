package org.hugoandrade.gymapp.view.member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlanRecord;
import org.hugoandrade.gymapp.view.adapter.ExerciseSetListAdapter;
import org.hugoandrade.gymapp.view.dialog.ExercisePlanBuilderDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SelectExercisesActivity extends AppCompatActivity  {

    @SuppressWarnings("unused") private final String TAG = getClass().getSimpleName();

    /**
     * Constant that represents the name of the intent extra that is paired
     * with a ExercisePlanRecord object.
     */
    private static final String INTENT_EXTRA_EXERCISE_PLAN = "intent_extract_exercise_plan_record";

    /**
     * Constant that represents the name of the intent extra that is paired
     * with a list of Exercise object.
     */
    private static final String INTENT_EXTRA_EXERCISE_LIST = "intent_extract_exercise_list";

    /**
     * The Exercise Plan of this instance
     */
    private ExercisePlanRecord mExercisePlan = ExercisePlanRecord.empty(GlobalData.getUser(), Calendar.getInstance());

    /**
     * List of all exercises
     */
    private List<Exercise> mExerciseList;

    /**
     * Adapter used to display the Exercise Plan (list of Exercise Set)
     */
    private ExerciseSetListAdapter mExerciseSetListAdapter;

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context, List<Exercise> exerciseList) {
        return new Intent(context, SelectExercisesActivity.class)
                .putParcelableArrayListExtra(INTENT_EXTRA_EXERCISE_LIST, new ArrayList<>(exerciseList));
    }

    /**
     * Method used to extract a ExercisePlanRecord object from an Intent
     */
    public static ExercisePlanRecord extractExercisePlanFromIntent(Intent data) {
        return data.getParcelableExtra(INTENT_EXTRA_EXERCISE_PLAN);
    }

    /**
     * Method used to extract a list of Exercises from an Intent
     */
    public static List<Exercise> extractExerciseListFromIntent(Intent data) {
        return data.getParcelableArrayListExtra(INTENT_EXTRA_EXERCISE_LIST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // extract the list of exercises used in the build of exercises
        mExerciseList = extractExerciseListFromIntent(getIntent());

        setResult(RESULT_CANCELED);

        initializeUI();
    }

    private void initializeUI() {
        setContentView(R.layout.activity_select_exercises);

        // set up 'set', 'cancel' and 'dismiss' views
        View vGoBack = findViewById(R.id.v_go_back);
        vGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        View tvCancel = findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        View tvSet = findViewById(R.id.tv_set);
        tvSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        // setup 'add exercise' button
        View tvAddExercise = findViewById(R.id.tv_add_exercise);
        tvAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // The member wants to add new exercises to the exercise plan being built.
                // show dialog with all exercises.
                ExercisePlanBuilderDialog dialog
                        = new ExercisePlanBuilderDialog(SelectExercisesActivity.this, mExerciseList);
                dialog.setOnSaveClickedListener(new ExercisePlanBuilderDialog.OnSaveClickedListener() {
                    @Override
                    public void onSaveClicked(List<Exercise> exerciseList) {
                        // add selected exercises to the exercise plann that is being built
                        mExercisePlan.addExercises(exerciseList);
                        mExerciseSetListAdapter.set(mExercisePlan);
                    }
                });
                dialog.show();
            }
        });

        // set up recycler view and adapter to display the exercise plan record being built
        mExerciseSetListAdapter = new ExerciseSetListAdapter(ExerciseSetListAdapter.MODE_EDIT, mExercisePlan);
        RecyclerView rvExercises = (RecyclerView) findViewById(R.id.rv_exercise);
        rvExercises.setHasFixedSize(true);
        rvExercises.setAdapter(mExerciseSetListAdapter);
        rvExercises.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void confirm() {
        // return the built exercise plan record to the previous activity as an intent extra
        setResult(RESULT_OK, new Intent().putExtra(INTENT_EXTRA_EXERCISE_PLAN, mExercisePlan));
        onBackPressed();
    }

    private void goBack() {
        onBackPressed();
    }
}
