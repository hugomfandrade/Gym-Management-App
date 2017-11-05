package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.model.CreateGymUserModel;

public class CreateGymUserPresenter extends PresenterBase<MVP.RequiredCreateGymUserViewOps,
                                                          MVP.RequiredCreateGymUserPresenterOps,
                                                          MVP.ProvidedCreateGymUserModelOps,
                                                          CreateGymUserModel>

        implements MVP.ProvidedCreateGymUserPresenterOps,
                   MVP.RequiredCreateGymUserPresenterOps {

    @Override
    public void onCreate(MVP.RequiredCreateGymUserViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the MVP.RequiredCreateGymUserViewOps class to
        // instantiate/manage and "this" to provide CreateGymUserModel
        // with this MVP.ProvidedCreateGymUserModelOps instance.
        super.onCreate(view, CreateGymUserModel.class, this);
    }

    @Override
    public void onResume() {
        // this activity is focused, so register callback from service
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(MVP.RequiredCreateGymUserViewOps view) { }

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
    public void createGymUser(String username, String credential) {
        // disable UI while waiting for web service response
        getView().disableUI();

        // create gym user with 'username' as username and of type 'credential'
        getModel().createGymUser(new WaitingUser(username, credential));
    }

    @Override
    public void creatingGymUserOperationResult(boolean wasOperationSuccessful, String message, WaitingUser waitingUser) {
        if (wasOperationSuccessful) {

            // operation was successful, show generated code
            getView().successfulCreateGymUser(waitingUser);
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
