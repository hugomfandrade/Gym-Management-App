package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.ExercisePlanRecordSuggested;
import org.hugoandrade.gymapp.model.SuggestedPlansModel;

import java.util.List;

public class SuggestedPlansPresenter extends PresenterBase<MVP.RequiredSuggestedPlansViewOps,
                                                       MVP.RequiredSuggestedPlansPresenterOps,
                                                       MVP.ProvidedSuggestedPlansModelOps,
        SuggestedPlansModel>
        implements MVP.ProvidedSuggestedPlansPresenterOps,
                   MVP.RequiredSuggestedPlansPresenterOps {

    @Override
    public void onCreate(MVP.RequiredSuggestedPlansViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the MyGymStaffModel class to instantiate/manage and
        // "this" to provide MyGymStaffModel with this MVP.RequiredMyGymStaffModelModelOps
        // instance.
        super.onCreate(view, SuggestedPlansModel.class, this);
    }

    @Override
    public void onResume() {
        // this activity is focused, so register callback from service
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(MVP.RequiredSuggestedPlansViewOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void notifyServiceIsBound() {
        // Once service is bound, get gym staff of the logged in user's 'My Gym Staff'
        getSuggestedPlans();
    }

    private void getSuggestedPlans() {
        // disable UI while waiting for web service response
        getView().disableUI();

        // get gym staff of the logged in gym member
        getModel().getSuggestedPlans(GlobalData.getUser().getID());
    }

    @Override
    public Context getActivityContext() {
        return getView().getActivityContext();
    }

    @Override
    public Context getApplicationContext() {
        return getView().getApplicationContext();
    }

    @Override
    public void gettingSuggestedPlansOperationResult(boolean wasOperationSuccessful,
                                                 String errorMessage,
                                                     List<ExercisePlanRecordSuggested> suggestedPlanList) {

        if (wasOperationSuccessful) {

            // operation was successful, display list of gym staff
            getView().displaySuggestedPlansList(suggestedPlanList);
        }
        else {
            // operation failed, show error message
            if (errorMessage != null)
                getView().reportMessage(errorMessage);
        }

        // enable UI
        getView().enableUI();

    }
}
