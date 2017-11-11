package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlan;
import org.hugoandrade.gymapp.data.ExercisePlanSuggested;
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
    public void createWorkout(ExercisePlan exercisePlan) {
        // disable UI while waiting for web service response
        getView().disableUI();

        // save exercise plan record in web service
        getModel().createWorkout(exercisePlan);
    }

    @Override
    public void createSuggestedWorkout(ExercisePlanSuggested exercisePlanSuggested) {
        // disable UI while waiting for web service response
        getView().disableUI();

        // save suggested exercise plan record in web service
        getModel().createSuggestedWorkout(exercisePlanSuggested);
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
    public void creatingExercisePlanOperationResult(boolean wasOperationSuccessful, String errorMessage, ExercisePlan exercisePlan) {

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

    @Override
    public void creatingExercisePlanSuggestedOperationResult(boolean wasOperationSuccessful,
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
