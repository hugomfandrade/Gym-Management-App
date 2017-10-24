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
        // passing in the ImageModel class to instantiate/manage and
        // "this" to provide ImageModel with this OldMVP.RequiredModelOps
        // instance.
        super.onCreate(view, SignUpModel.class, this);
    }

    @Override
    public void onResume() { }

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
        getView().disableValidateUI();

        getModel().validateWaitingUser(new WaitingUser(null, username, null, code));
    }

    @Override
    public void signUp(String username, String password) {
        getView().disableSignUpUI();

        getModel().signUp(username, password);
    }

    @Override
    public void validateWaitingUserOperationResult(boolean wasOperationSuccessful, String message, WaitingUser waitingUser) {
        getView().enableValidateUI();

        if (wasOperationSuccessful) {
            getView().successfulWaitingUser(waitingUser);
        }
        else {
            if (message != null)
                getView().reportMessage(message);
        }
    }

    @Override
    public void signUpOperationResult(boolean wasOperationSuccessful, String message, User user) {
        getView().enableSignUpUI();

        if (wasOperationSuccessful) {
            getView().successfulSignUp(user);
        }
        else {
            if (message != null)
                getView().reportMessage(message);
        }
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
