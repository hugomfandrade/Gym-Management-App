package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.LoginModel;

public class LoginPresenter extends PresenterBase<MVP.RequiredLoginViewOps,
                                                  MVP.RequiredLoginPresenterOps,
                                                  MVP.ProvidedLoginModelOps,
                                                  LoginModel>
        implements MVP.ProvidedLoginPresenterOps,
                   MVP.RequiredLoginPresenterOps {

    @Override
    public void onCreate(MVP.RequiredLoginViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the LoginModel class to instantiate/manage and
        // "this" to provide LoginModel with this MVP.RequiredLoginModelOps
        // instance.
        super.onCreate(view, LoginModel.class, this);

        // get last used username-password from StorageProvider
        getModel().getLastLogin();
    }

    @Override
    public void onResume() {
        // this activity is focused, so register callback from service
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(MVP.RequiredLoginViewOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void notifyServiceIsBound() {
        // No-ops
    }

    @Override
    public void login(String username, String password) {
        // disable UI while waiting for web service response
        getView().disableUI();

        // try to login with username-password combo
        getModel().login(username, password);
    }

    @Override
    public void loginOperationResult(boolean wasOperationSuccessful, String message, User user) {
        if (wasOperationSuccessful) {

            // operation was successful, go to main activity and store username-password
            // in the StorageProvider
            getModel().insertLastLogin(user);

            GlobalData.initializeUser(user);

            getView().successfulLogin(user.getCredential());
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
    public void displayLastLogin(User user) {
        getView().displayLastLogin(user);
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
