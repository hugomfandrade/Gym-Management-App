package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

import java.util.List;

public class ExerciseListModel extends MobileClientModelBase<MVP.RequiredExerciseListPresenterOps>

    implements MVP.ProvidedExerciseListModelOps {

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_GET_ALL_EXERCISES) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                gettingAllExercisesRequestResultSuccess(data.getExerciseList());
            else
                gettingAllExercisesRequestResultFailure(data.getErrorMessage());
        }
    }

    @Override
    public void getExercises() {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingAllExercisesRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isLoggingIn = getService().getAllExercises();
            if (!isLoggingIn) {
                gettingAllExercisesRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingAllExercisesRequestResultFailure("Error sending message");
        }
    }

    private void gettingAllExercisesRequestResultFailure(String errorMessage) {
        getPresenter().gettingAllExercisesOperationResult(false, errorMessage, null);
    }

    private void gettingAllExercisesRequestResultSuccess(List<Exercise> exerciseList) {
        getPresenter().gettingAllExercisesOperationResult(true, null, exerciseList);
    }
}
