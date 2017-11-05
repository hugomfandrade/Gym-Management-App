package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.model.ExerciseListModel;

import java.util.List;

public class ExerciseListPresenter extends PresenterBase<MVP.RequiredExerciseListViewOps,
                                                         MVP.RequiredExerciseListPresenterOps,
                                                         MVP.ProvidedExerciseListModelOps,
                                                         ExerciseListModel>
        implements MVP.ProvidedExerciseListPresenterOps,
                   MVP.RequiredExerciseListPresenterOps {

    @Override
    public void onCreate(MVP.RequiredExerciseListViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the ExerciseListModel class to instantiate/manage and
        // "this" to provide ExerciseListModel with this MVP.RequiredExerciseListModelOps
        // instance.
        super.onCreate(view, ExerciseListModel.class, this);
    }

    @Override
    public void onResume() {
        // this activity is focused, so register callback from service
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(MVP.RequiredExerciseListViewOps view) { }

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
