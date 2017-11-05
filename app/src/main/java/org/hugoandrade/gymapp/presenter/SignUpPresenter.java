package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.model.SignUpModel;

public class SignUpPresenter extends PresenterBase<MVP.RequiredSignUpViewOps,
                                                   MVP.RequiredSignUpPresenterOps,
                                                   MVP.ProvidedSignUpModelOps,
                                                   SignUpModel>
        implements MVP.ProvidedSignUpPresenterOps,
                   MVP.RequiredSignUpPresenterOps {

    @Override
    public void onCreate(MVP.RequiredSignUpViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the SignUpModel class to instantiate/manage and
        // "this" to provide SignUpModel with this OldMVP.RequiredModelOps
        // instance.
        super.onCreate(view, SignUpModel.class, this);
    }

    @Override
    public void onResume() {
        // this activity is focused, so register callback from service
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(MVP.RequiredSignUpViewOps view) { }

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
    public void validateWaitingUser(String username, String code) {
        // disable UI while waiting for web service response
        getView().disableValidateUI();

        // try to validate username-code combo
        getModel().validateWaitingUser(new WaitingUser(null, username, null, code));
    }

    @Override
    public void signUp(String username, String password) {
        // disable UI while waiting for web service response
        getView().disableSignUpUI();

        // try to sign up with username-password combo
        getModel().signUp(username, password);
    }

    @Override
    public void validateWaitingUserOperationResult(boolean wasOperationSuccessful, String message, WaitingUser waitingUser) {

        if (wasOperationSuccessful) {
            // operation was successful, set up 'choose password' step
            getView().successfulWaitingUser(waitingUser);
        }
        else {
            // operation failed, show error message
            if (message != null)
                getView().reportMessage(message);
        }
        // enable UI
        getView().enableValidateUI();
    }

    @Override
    public void signUpOperationResult(boolean wasOperationSuccessful, String message, User user) {

        if (wasOperationSuccessful) {
            // operation was successful, return to login activity
            getView().successfulSignUp(user);
        }
        else {
            // operation failed, show error message
            if (message != null)
                getView().reportMessage(message);
        }
        // enable UI
        getView().enableSignUpUI();
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
