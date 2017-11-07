package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.ExercisePlanRecordSuggested;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

public class SuggestedPlanDetailsModel extends MobileClientModelBase<MVP.RequiredSuggestedPlanDetailsPresenterOps>

    implements MVP.ProvidedSuggestedPlanDetailsModelOps {

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_DISMISS_SUGGESTED_PLAN) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                dismissingSuggestedPlanRequestResultSuccess(data.getExercisePlanRecordSuggested());
            else
                dismissingSuggestedPlanRequestResultFailure(data.getErrorMessage());
        }
    }

    @Override
    public void dismissSuggestedPlan(ExercisePlanRecordSuggested suggestedPlan, boolean wasDone) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            dismissingSuggestedPlanRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isGetting = getService().dismissSuggestedPlan(suggestedPlan, wasDone);
            if (!isGetting) {
                dismissingSuggestedPlanRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            dismissingSuggestedPlanRequestResultFailure("Error sending message");
        }
    }

    private void dismissingSuggestedPlanRequestResultFailure(String errorMessage) {
        getPresenter().dismissingSuggestedPlanOperationResult(false, errorMessage, null);
    }

    private void dismissingSuggestedPlanRequestResultSuccess(ExercisePlanRecordSuggested suggestedPlan) {
        getPresenter().dismissingSuggestedPlanOperationResult(true, null, suggestedPlan);
    }
}
