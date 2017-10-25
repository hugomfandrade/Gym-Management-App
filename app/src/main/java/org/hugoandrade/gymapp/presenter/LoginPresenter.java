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
        // passing in the ImageModel class to instantiate/manage and
        // "this" to provide ImageModel with this OldMVP.RequiredModelOps
        // instance.
        super.onCreate(view, LoginModel.class, this);
    }

    @Override
    public void onResume() {
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
        getView().setLoggingInStatus(true);

        getModel().login(username, password);
    }

    @Override
    public void loginOperationResult(boolean wasOperationSuccessful, String message, User user) {
        if (wasOperationSuccessful) {
            GlobalData.initializeUser(user);

            getView().successfulLogin(user.getCredential());
        }
        else {
            if (message != null)
                getView().reportMessage(message);
        }

        getView().setLoggingInStatus(false);
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
