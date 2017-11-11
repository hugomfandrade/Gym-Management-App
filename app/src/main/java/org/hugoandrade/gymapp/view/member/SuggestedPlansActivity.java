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
import org.hugoandrade.gymapp.data.ExercisePlanSuggested;
import org.hugoandrade.gymapp.presenter.SuggestedPlansPresenter;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.ActivityBase;
import org.hugoandrade.gymapp.view.adapter.ExercisePlanSuggestedListAdapter;

import java.util.List;

public class SuggestedPlansActivity extends ActivityBase<MVP.RequiredSuggestedPlansViewOps,
                                                     MVP.ProvidedSuggestedPlansPresenterOps,
        SuggestedPlansPresenter>

        implements MVP.RequiredSuggestedPlansViewOps {

    /**
     * Constant that represents the request code used to start the "see suggested plan" activity.
     */
    private static final int SEE_DETAILS_REQUEST_CODE = 400;

    /**
     * View showing "Waiting" and disables UI to be displayed when waiting
     * for the response of a Web request
     */
    private View vProgressBar;

    /**
     * Adapter used to display the suggested plans
     */
    private ExercisePlanSuggestedListAdapter mSuggestedPlanListAdapter;

    /**
     * Message to be displayed when no suggested plan was retrieved
     */
    private TextView tvNoSuggestedPlansMessage;

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context, SuggestedPlansActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();

        super.onCreate(SuggestedPlansPresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_member_suggested_plans);

        // set up toolbar and title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.action_check_suggested_plans);

        vProgressBar = findViewById(R.id.progressBar_waiting);

        // set up recycler view and adapter
        mSuggestedPlanListAdapter = new ExercisePlanSuggestedListAdapter();
        mSuggestedPlanListAdapter.setOnItemClickedListener(new ExercisePlanSuggestedListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(ExercisePlanSuggested exercisePlanSuggested) {
                startActivityForResult(SuggestedPlanDetailsActivity
                                .makeIntent(SuggestedPlansActivity.this,
                                        exercisePlanSuggested),
                        SEE_DETAILS_REQUEST_CODE);
            }
        });
        RecyclerView rvGymUsers = (RecyclerView) findViewById(R.id.rv_suggested_plan);
        rvGymUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvGymUsers.setAdapter(mSuggestedPlanListAdapter);

        // set up no gym my staff message appropriately
        tvNoSuggestedPlansMessage = (TextView) findViewById(R.id.tv_no_suggested_plans);
        tvNoSuggestedPlansMessage.setText(R.string.no_suggested_plans);
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
    public void displaySuggestedPlansList(List<ExercisePlanSuggested> suggestedList) {
        mSuggestedPlanListAdapter.setAll(suggestedList);

        // if list is empty, show "no my gym staff" message
        tvNoSuggestedPlansMessage.setVisibility(suggestedList.size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEE_DETAILS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Successfully built a exercise plan.
                // Extract the new exercise plan and display it

                ExercisePlanSuggested exercisePlanSuggested
                        = SuggestedPlanDetailsActivity.extractExercisePlanSuggestedFromIntent(data);
                mSuggestedPlanListAdapter.remove(exercisePlanSuggested);

                // hide "start building" message
                tvNoSuggestedPlansMessage.setVisibility(mSuggestedPlanListAdapter.getItemCount() != 0?
                        View.GONE : View.VISIBLE);
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
