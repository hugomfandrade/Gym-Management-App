package org.hugoandrade.gymapp.presenter.admin;


import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;
import org.hugoandrade.gymapp.presenter.MobileClientPresenterBase;

public class CreateExercisePresenter extends MobileClientPresenterBase<MVP.RequiredCreateExerciseViewOps>

        implements MVP.ProvidedCreateExercisePresenterOps {

    @Override
    public void onCreate(MVP.RequiredCreateExerciseViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the MVP.RequiredCreateExerciseViewOps class to
        // instantiate/manage and "this" to provide MobileClientModel
        // with this MVP.RequiredMobileServicePresenterOps instance.
        super.onCreate(view);
    }

    @Override
    public void notifyServiceIsBound() {
        //No-ops
    }

    @Override
    public void createExercise(String name) {
        // disable UI while waiting for web service response
        getView().disableUI();

        // create exercise with 'name' as name
        doCreateExercise(new Exercise(null, name));
    }

    /**
     * Try creating/saving a new exercise to the Web Service via the Service.
     */
    private void doCreateExercise(Exercise exercise) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            creatingExerciseOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isCreating = getMobileClientService().createExercise(exercise);
            if (!isCreating) {
                creatingExerciseOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            creatingExerciseOperationResult(false, "Error sending message", null);
        }
    }

    @Override
    public void sendResults(MobileClientData data) {

        int operationType = data.getOperationType();
        boolean isOperationSuccessful
                = data.getOperationResult() == MobileClientData.OPERATION_SUCCESS;

        if (operationType == MobileClientData.OPERATION_CREATE_EXERCISE) {
            creatingExerciseOperationResult(
                    isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getExercise());
        }
    }

    /**
     * Handle the operation result of creating a new exercise.
     */
    private void creatingExerciseOperationResult(boolean wasOperationSuccessful, String message, Exercise exercise) {
        if (wasOperationSuccessful) {

            // operation was successful, display exercise in list
            getView().successfulCreateExercise(exercise);
        }
        else {
            // operation failed, show error message
            if (message != null)
                getView().reportMessage(message);
        }

        // enable UI
        getView().enableUI();
    }
}
