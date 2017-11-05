package org.hugoandrade.gymapp.view.member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.ExercisePlanRecord;
import org.hugoandrade.gymapp.presenter.HistoryPresenter;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.ActivityBase;
import org.hugoandrade.gymapp.view.adapter.ExercisePlanReportListAdapter;

import java.util.List;

public class HistoryActivity extends ActivityBase<MVP.RequiredHistoryViewOps,
                                                  MVP.ProvidedHistoryPresenterOps,
                                                  HistoryPresenter>

        implements MVP.RequiredHistoryViewOps {

    /**
     * View showing "Waiting" and disables UI to be displayed when waiting
     * for the response of a Web request
     */
    private View vProgressBar;

    /**
     * Adapter used to display all exercise plans of the gym member
     */
    private ExercisePlanReportListAdapter mExercisePlanRecordListAdapter;

    /**
     * Message to be displayed when no exercise plans was retrieved
     */
    private TextView tvNoExercisePlanRecordMessage;

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context, HistoryActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();

        super.onCreate(HistoryPresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_member_my_history);

        // set up toolbar and title appropriately
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.action_check_my_history);

        vProgressBar = findViewById(R.id.progressBar_waiting);

        // set up recycler view and adapter
        mExercisePlanRecordListAdapter = new ExercisePlanReportListAdapter();
        mExercisePlanRecordListAdapter.setOnItemClickedListener(new ExercisePlanReportListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(ExercisePlanRecord exercisePlanRecord) {
                // Member selected a exercise plan. start activity to display the details
                startActivity(WorkoutActivity.makeIntent(
                        HistoryActivity.this,
                        exercisePlanRecord));
            }
        });
        RecyclerView rvGymUsers = (RecyclerView) findViewById(R.id.rv_exercise_plan_record);
        rvGymUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvGymUsers.setAdapter(mExercisePlanRecordListAdapter);

        // set up no gym users message appropriately
        tvNoExercisePlanRecordMessage = (TextView) findViewById(R.id.tv_no_exercise_plan_record);
        tvNoExercisePlanRecordMessage.setText(R.string.no_history);
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
    public void displayExercisePlanRecordList(List<ExercisePlanRecord> exercisePlanRecordList) {
        mExercisePlanRecordListAdapter.setAll(exercisePlanRecordList);

        // if list is empty, show "no exercise plans" message
        tvNoExercisePlanRecordMessage.setVisibility(exercisePlanRecordList.size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void reportMessage(String message) {
        UIUtils.showSnackBar(findViewById(android.R.id.content), message);
    }
}
