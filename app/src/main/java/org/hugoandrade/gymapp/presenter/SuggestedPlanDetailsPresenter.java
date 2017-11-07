package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.ExercisePlanRecordSuggested;
import org.hugoandrade.gymapp.model.SuggestedPlanDetailsModel;

import java.util.Calendar;
import java.util.List;

public class SuggestedPlanDetailsPresenter extends PresenterBase<MVP.RequiredSuggestedPlanDetailsViewOps,
                                                                 MVP.RequiredSuggestedPlanDetailsPresenterOps,
                                                                 MVP.ProvidedSuggestedPlanDetailsModelOps,
                                                                 SuggestedPlanDetailsModel>
        implements MVP.ProvidedSuggestedPlanDetailsPresenterOps,
                   MVP.RequiredSuggestedPlanDetailsPresenterOps {

    @Override
    public void onCreate(MVP.RequiredSuggestedPlanDetailsViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the SuggestedPlanDetailsModel class to instantiate/manage and
        // "this" to provide SuggestedPlanDetailsModel with this MVP.RequiredSuggestedPlanDetailsModelOps
        // instance.
        super.onCreate(view, SuggestedPlanDetailsModel.class, this);
    }

    @Override
    public void onResume() {
        // this activity is focused, so register callback from service
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(MVP.RequiredSuggestedPlanDetailsViewOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void notifyServiceIsBound() {
        // No-ops
    }


    @Override
    public void dismissSuggestedPlan(ExercisePlanRecordSuggested suggestedPlan, boolean wasDone) {
        // disable UI while waiting for web service response
        getView().disableUI();

        // dismiss exercise plan
        suggestedPlan.setDatetime(Calendar.getInstance());
        getModel().dismissSuggestedPlan(suggestedPlan, wasDone);
    }

    @Override
    public void dismissingSuggestedPlanOperationResult(boolean wasOperationSuccessful,
                                                       String errorMessage,
                                                       ExercisePlanRecordSuggested suggestedPlan) {

        if (wasOperationSuccessful) {

            // operation was successful, display list of gym staff
            getView().suggestedPlanDismissed(suggestedPlan);
        }
        else {
            // operation failed, show error message
            if (errorMessage != null)
                getView().reportMessage(errorMessage);
        }

        // enable UI
        getView().enableUI();
    }

    @Override
    public Context getActivityContext() {
        return getView().getActivityContext();
    }

    @Override
    public Context getApplicationContext() {
        return getView().getApplicationContext();
    }
}
