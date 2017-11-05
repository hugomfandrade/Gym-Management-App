package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

public class CreateExerciseModel extends MobileClientModelBase<MVP.RequiredCreateExercisePresenterOps>

    implements MVP.ProvidedCreateExerciseModelOps {

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_CREATE_EXERCISE) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                creatingExerciseRequestResultSuccess(data.getExercise());
            else
                creatingExerciseRequestResultFailure(data.getErrorMessage());
        }
    }

    @Override
    public void createExercise(Exercise exercise) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            creatingExerciseRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isCreating = getService().createExercise(exercise);
            if (!isCreating) {
                creatingExerciseRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            creatingExerciseRequestResultFailure("Error sending message");
        }
    }

    private void creatingExerciseRequestResultFailure(String errorMessage) {
        getPresenter().creatingExerciseOperationResult(false, errorMessage, null);
    }

    private void creatingExerciseRequestResultSuccess(Exercise exercise) {
        getPresenter().creatingExerciseOperationResult(true, null, exercise);
    }
}
