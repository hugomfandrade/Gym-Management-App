package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.ExercisePlan;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

import java.util.List;

public class HistoryModel extends MobileClientModelBase<MVP.RequiredHistoryPresenterOps>

    implements MVP.ProvidedHistoryModelOps {

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_GET_HISTORY) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                gettingHistoryRequestResultSuccess(data.getExercisePlanList());
            else
                gettingHistoryRequestResultFailure(data.getErrorMessage());
        }
    }

    @Override
    public void getHistory(String userID) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingHistoryRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isGetting = getService().getExercisePlanList(userID);
            if (!isGetting) {
                gettingHistoryRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingHistoryRequestResultFailure("Error sending message");
        }
    }

    private void gettingHistoryRequestResultFailure(String errorMessage) {
        getPresenter().gettingHistoryOperationResult(false, errorMessage, null);
    }

    private void gettingHistoryRequestResultSuccess(List<ExercisePlan> exercisePlanList) {
        getPresenter().gettingHistoryOperationResult(true, null, exercisePlanList);
    }
}
