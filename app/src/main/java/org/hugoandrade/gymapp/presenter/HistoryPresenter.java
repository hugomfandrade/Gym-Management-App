package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.ExercisePlanRecord;
import org.hugoandrade.gymapp.model.HistoryModel;

import java.util.List;

public class HistoryPresenter extends PresenterBase<MVP.RequiredHistoryViewOps,
                                                    MVP.RequiredHistoryPresenterOps,
                                                    MVP.ProvidedHistoryModelOps,
                                                    HistoryModel>
        implements MVP.ProvidedHistoryPresenterOps,
                   MVP.RequiredHistoryPresenterOps {

    @Override
    public void onCreate(MVP.RequiredHistoryViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the HistoryModel class to instantiate/manage and
        // "this" to provide HistoryModel with this MVP.RequiredHistoryModelOps
        // instance.
        super.onCreate(view, HistoryModel.class, this);
    }

    @Override
    public void onResume() {
        // this activity is focused, so register callback from service
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(MVP.RequiredHistoryViewOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void notifyServiceIsBound() {
        // Once service is bound, get all exercise plans of the logged in gym member
        getHistory(getView().getUserID());
    }

    private void getHistory(String userID) {
        // disable UI while waiting for web service response
        getView().disableUI();

        // get all exercise plans recorded by the gym member
        getModel().getHistory(userID);
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
    public void gettingHistoryOperationResult(boolean wasOperationSuccessful,
                                              String errorMessage,
                                              List<ExercisePlanRecord> exercisePlanRecordList) {

        if (wasOperationSuccessful) {

            // operation was successful, display exercise plan records
            getView().displayExercisePlanRecordList(exercisePlanRecordList);
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
