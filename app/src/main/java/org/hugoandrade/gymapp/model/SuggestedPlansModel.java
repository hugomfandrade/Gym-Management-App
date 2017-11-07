package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.ExercisePlanRecordSuggested;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.MobileClientModelBase;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

import java.util.List;

public class SuggestedPlansModel extends MobileClientModelBase<MVP.RequiredSuggestedPlansPresenterOps>

    implements MVP.ProvidedSuggestedPlansModelOps {

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_GET_HISTORY_SUGGESTED) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                gettingSuggestedPlansRequestResultSuccess(data.getExercisePlanRecordSuggestedList());
            else
                gettingSuggestedPlansRequestResultFailure(data.getErrorMessage());
        }
    }

    @Override
    public void getSuggestedPlans(String userID) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingSuggestedPlansRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isGetting = getService().getExercisePlanRecordSuggestedList(userID);
            if (!isGetting) {
                gettingSuggestedPlansRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingSuggestedPlansRequestResultFailure("Error sending message");
        }
    }

    private void gettingSuggestedPlansRequestResultFailure(String errorMessage) {
        getPresenter().gettingSuggestedPlansOperationResult(false, errorMessage, null);
    }

    private void gettingSuggestedPlansRequestResultSuccess(List<ExercisePlanRecordSuggested> suggestedPlanList) {
        getPresenter().gettingSuggestedPlansOperationResult(true, null, suggestedPlanList);
    }
}
