package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.model.CreateExerciseModel;

public class CreateExercisePresenter extends PresenterBase<MVP.RequiredCreateExerciseViewOps,
                                                          MVP.RequiredCreateExercisePresenterOps,
                                                          MVP.ProvidedCreateExerciseModelOps,
                                                          CreateExerciseModel>

        implements MVP.ProvidedCreateExercisePresenterOps,
                   MVP.RequiredCreateExercisePresenterOps {

    @Override
    public void onCreate(MVP.RequiredCreateExerciseViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the MVP.RequiredCreateExerciseViewOps class to
        // instantiate/manage and "this" to provide CreateExerciseModel
        // with this MVP.ProvidedCreateExerciseModelOps instance.
        super.onCreate(view, CreateExerciseModel.class, this);
    }

    @Override
    public void onResume() {
        // this activity is focused, so register callback from service
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(MVP.RequiredCreateExerciseViewOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
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
        getModel().createExercise(new Exercise(null, name));
    }

    @Override
    public void creatingExerciseOperationResult(boolean wasOperationSuccessful, String message, Exercise exercise) {
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

    @Override
    public Context getActivityContext() {
        return getView().getActivityContext();
    }

    @Override
    public Context getApplicationContext() {
        return getView().getApplicationContext();
    }
}
