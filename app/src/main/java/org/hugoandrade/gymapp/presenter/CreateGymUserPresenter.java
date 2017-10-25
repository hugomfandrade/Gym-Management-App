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
        getView().disableUI();

        getModel().createGymUser(new WaitingUser(username, credential));
    }

    @Override
    public void creatingGymUserOperationResult(boolean wasOperationSuccessful, String message, WaitingUser waitingUser) {
        if (wasOperationSuccessful) {

            getView().successfulCreateGymUser(waitingUser);
        }
        else {
            if (message != null)
                getView().reportMessage(message);
        }

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
