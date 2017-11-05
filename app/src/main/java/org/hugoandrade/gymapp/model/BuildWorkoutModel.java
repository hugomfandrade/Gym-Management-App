package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlanRecord;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

import java.util.List;

public class BuildWorkoutModel extends MobileClientModelBase<MVP.RequiredBuildWorkoutPresenterOps>

    implements MVP.ProvidedBuildWorkoutModelOps {

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_GET_ALL_EXERCISES) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                gettingAllExercisesRequestResultSuccess(data.getExerciseList());
            else
                gettingAllExercisesRequestResultFailure(data.getErrorMessage());
        }
        else if (data.getOperationType() == MobileClientData.OPERATION_CREATE_EXERCISE_PLAN) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                creatingExercisePlanRequestResultSuccess(data.getExercisePlan());
            else
                creatingExercisePlanRequestResultFailure(data.getErrorMessage());
        }
    }

    @Override
    public void createWorkout(ExercisePlanRecord exercisePlanRecord) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            creatingExercisePlanRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isCreating = getService().createWorkout(exercisePlanRecord);
            if (!isCreating) {
                creatingExercisePlanRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            creatingExercisePlanRequestResultFailure("Error sending message");
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
            boolean isGetting = getService().getAllExercises();
            if (!isGetting) {
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

    private void creatingExercisePlanRequestResultFailure(String errorMessage) {
        getPresenter().creatingExercisePlanOperationResult(false, errorMessage, null);
    }

    private void creatingExercisePlanRequestResultSuccess(ExercisePlanRecord exercisePlanRecord) {
        getPresenter().creatingExercisePlanOperationResult(true, null, exercisePlanRecord);
    }
}
