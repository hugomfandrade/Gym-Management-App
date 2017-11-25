package org.hugoandrade.gymapp.presenter;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlan;
import org.hugoandrade.gymapp.data.ExercisePlanSuggested;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

import java.util.List;

public class BuildWorkoutPresenter extends MobileClientPresenterBase<MVP.RequiredBuildWorkoutViewOps>

        implements MVP.ProvidedBuildWorkoutPresenterOps {

    @Override
    public void onCreate(MVP.RequiredBuildWorkoutViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the BuildWorkoutModel class to instantiate/manage and
        // "this" to provide BuildWorkoutModel with this MVP.RequiredBuildWorkoutModelOps
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

    @Override
    public void createWorkout(ExercisePlan exercisePlan) {
        // disable UI while waiting for web service response
        getView().disableUI();

        // save exercise plan record in web service
        doCreateWorkout(exercisePlan);
    }

    @Override
    public void createSuggestedWorkout(ExercisePlanSuggested exercisePlanSuggested) {
        // disable UI while waiting for web service response
        getView().disableUI();

        // save suggested exercise plan record in web service
        doCreateSuggestedWorkout(exercisePlanSuggested);
    }

    private void doCreateWorkout(ExercisePlan exercisePlan) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            creatingExercisePlanOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isCreating = getMobileClientService().createWorkout(exercisePlan);
            if (!isCreating) {
                creatingExercisePlanOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            creatingExercisePlanOperationResult(false, "Error sending message", null);
        }
    }

    private void doCreateSuggestedWorkout(ExercisePlanSuggested exercisePlanSuggested) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            creatingExercisePlanSuggestedOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isCreating = getMobileClientService().createSuggestedWorkout(exercisePlanSuggested);
            if (!isCreating) {
                creatingExercisePlanSuggestedOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            creatingExercisePlanSuggestedOperationResult(false, "Error sending message", null);
        }
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
            gettingAllExercisesOperationResult(
                    isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getExerciseList());
        }
        else if (operationType == MobileClientData.OPERATION_CREATE_EXERCISE_PLAN) {
            creatingExercisePlanOperationResult(
                    isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getExercisePlan());
        }
        else if (operationType == MobileClientData.OPERATION_CREATE_SUGGESTED_EXERCISE_PLAN) {
            creatingExercisePlanSuggestedOperationResult(
                    isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getExercisePlanSuggested());
        }
    }

    private void gettingAllExercisesOperationResult(boolean wasOperationSuccessful,
                                                    String errorMessage,
                                                    List<Exercise> exerciseList) {

        if (wasOperationSuccessful) {

            // operation was successful, set list of exercises that is used when building
            // exercise plan record
            getView().setExerciseList(exerciseList);
        }
        else {
            // operation failed, show error message
            if (errorMessage != null)
                getView().reportMessage(errorMessage);
        }

        // enable UI
        getView().enableUI();

    }

    private void creatingExercisePlanOperationResult(boolean wasOperationSuccessful,
                                                     String errorMessage,
                                                     ExercisePlan exercisePlan) {

        if (wasOperationSuccessful) {

            // operation was successful, return to previous activity
            getView().exercisePlanCreated(exercisePlan);
        }
        else {
            // operation failed, show error message
            if (errorMessage != null)
                getView().reportMessage(errorMessage);
        }

        // enable UI
        getView().enableUI();
    }

    private void creatingExercisePlanSuggestedOperationResult(boolean wasOperationSuccessful,
                                                              String errorMessage,
                                                              ExercisePlanSuggested exercisePlanSuggested) {

        if (wasOperationSuccessful) {

            // operation was successful, return to previous activity
            getView().exercisePlanCreated(null);
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
