package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlanRecord;
import org.hugoandrade.gymapp.model.BuildWorkoutModel;

import java.util.List;

public class BuildWorkoutPresenter extends PresenterBase<MVP.RequiredBuildWorkoutViewOps,
                                                         MVP.RequiredBuildWorkoutPresenterOps,
                                                         MVP.ProvidedBuildWorkoutModelOps,
                                                         BuildWorkoutModel>
        implements MVP.ProvidedBuildWorkoutPresenterOps,
                   MVP.RequiredBuildWorkoutPresenterOps {

    @Override
    public void onCreate(MVP.RequiredBuildWorkoutViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the BuildWorkoutModel class to instantiate/manage and
        // "this" to provide BuildWorkoutModel with this MVP.RequiredBuildWorkoutModelOps
        // instance.
        super.onCreate(view, BuildWorkoutModel.class, this);
    }

    @Override
    public void onResume() {
        // this activity is focused, so register callback from service
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(MVP.RequiredBuildWorkoutViewOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
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
        getModel().getExercises();
    }


    @Override
    public void createWorkout(ExercisePlanRecord exercisePlanRecord) {
        // disable UI while waiting for web service response
        getView().disableUI();

        // save exercise plan record in web service
        getModel().createWorkout(exercisePlanRecord);
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
    public void gettingAllExercisesOperationResult(boolean wasOperationSuccessful,
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

    @Override
    public void creatingExercisePlanOperationResult(boolean wasOperationSuccessful, String errorMessage, ExercisePlanRecord exercisePlanRecord) {

        if (wasOperationSuccessful) {

            // operation was successful, return to previous activity
            getView().exercisePlanCreated(exercisePlanRecord);
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
