package org.hugoandrade.gymapp.presenter;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

import java.util.List;

public class ExerciseListPresenter extends MobileClientPresenterBase<MVP.RequiredExerciseListViewOps>

        implements MVP.ProvidedExerciseListPresenterOps {

    @Override
    public void onCreate(MVP.RequiredExerciseListViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the ExerciseListModel class to instantiate/manage and
        // "this" to provide ExerciseListModel with this MVP.RequiredExerciseListModelOps
        // instance.
        super.onCreate(view);
    }

    @Override
    public void notifyServiceIsBound() {
        // Once service is bound, get all gym exercises
        getAllExercises();
    }

    private void getAllExercises() {
        // disable UI while waiting for web service response
        getView().disableUI();

        // get all exercises
        doGetExercises();
    }

    private void doGetExercises() {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingAllExercisesOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isGetting = getMobileClientService().getAllExercises();
            if (!isGetting) {
                gettingAllExercisesOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingAllExercisesOperationResult(false, "Error sending message", null);
        }
    }

    @Override
    public void sendResults(MobileClientData data) {

        int operationType = data.getOperationType();
        boolean isOperationSuccessful
                = data.getOperationResult() == MobileClientData.OPERATION_SUCCESS;

        if (operationType == MobileClientData.OPERATION_GET_ALL_EXERCISES) {
            gettingAllExercisesOperationResult(isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getExerciseList());
        }
    }

    private void gettingAllExercisesOperationResult(boolean wasOperationSuccessful,
                                                   String errorMessage,
                                                   List<Exercise> exerciseList) {

        if (wasOperationSuccessful) {

            // operation was successful, display list of exercises
            getView().displayExerciseList(exerciseList);
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
