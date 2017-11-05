package org.hugoandrade.gymapp.view.admin;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.presenter.ExerciseListPresenter;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.ActivityBase;
import org.hugoandrade.gymapp.view.adapter.ExerciseListAdapter;

import java.util.List;

public class ExerciseListActivity extends ActivityBase<MVP.RequiredExerciseListViewOps,
                                                       MVP.ProvidedExerciseListPresenterOps,
                                                       ExerciseListPresenter>

        implements MVP.RequiredExerciseListViewOps {

    /**
     * Constant that represents the request code used to start the "create exercise" activity.
     */
    private static final int ADD_EXERCISE_REQUEST_CODE = 300;

    /**
     * View showing "Waiting" and disables UI to be displayed when waiting
     * for the response of a Web request
     */
    private View vProgressBar;

    /**
     * Adapter used to display all exercises
     */
    private ExerciseListAdapter mExerciseListAdapter;

    /**
     * Message to be displayed when no exercise was retrieved
     */
    private TextView tvNoExerciseMessage;

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context, ExerciseListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();

        super.onCreate(ExerciseListPresenter.class, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // change icon color
        Drawable drawable = menu.findItem(R.id.action_add_exercise).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_exercise: {
                // start "create exercise" activity when ths menu item is selected
                startActivityForResult(CreateExerciseActivity.makeIntent(ExerciseListActivity.this),
                                       ADD_EXERCISE_REQUEST_CODE);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_admin_list_of_exercises);

        // set up toolbar and title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.gym_exercises);

        vProgressBar = findViewById(R.id.progressBar_waiting);

        // set up recycler view and adapter
        mExerciseListAdapter = new ExerciseListAdapter();
        RecyclerView rvGymUsers = (RecyclerView) findViewById(R.id.rv_exercises);
        rvGymUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvGymUsers.setAdapter(mExerciseListAdapter);

        // set up no exercises message
        tvNoExerciseMessage = (TextView) findViewById(R.id.tv_no_exercises);
        tvNoExerciseMessage.setText(R.string.no_exercises);
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
    public void displayExerciseList(List<Exercise> exerciseList) {
        mExerciseListAdapter.setAll(exerciseList);

        // if list is empty, show "no exercises" message
        tvNoExerciseMessage.setVisibility(exerciseList.size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_EXERCISE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Successfully created an exercise.
                // Extract the new exercise and add to the list
                Exercise newExercise = CreateExerciseActivity.extractExerciseFromIntent(data);
                mExerciseListAdapter.add(newExercise);

                tvNoExerciseMessage.setVisibility(View.INVISIBLE);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void reportMessage(String message) {
        UIUtils.showSnackBar(findViewById(android.R.id.content), message);
    }
}
