package org.hugoandrade.gymapp.presenter;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

public class SignUpPresenter extends MobileClientPresenterBase<MVP.RequiredSignUpViewOps>

        implements MVP.ProvidedSignUpPresenterOps {

    @Override
    public void onCreate(MVP.RequiredSignUpViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the SignUpModel class to instantiate/manage and
        // "this" to provide SignUpModel with this OldMVP.RequiredModelOps
        // instance.
        super.onCreate(view);
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
        doValidateWaitingUser(new WaitingUser(null, username, null, code));
    }

    @Override
    public void signUp(String username, String password) {
        // disable UI while waiting for web service response
        getView().disableSignUpUI();

        // try to sign up with username-password combo
        doSignUp(username, password);
    }

    /**
     * Try validating the username-code combo  via the Service.
     */
    private void doValidateWaitingUser(WaitingUser waitingUser) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            validateWaitingUserOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isValidatingUser = getMobileClientService().validateGymUser(waitingUser);
            if (!isValidatingUser) {
                validateWaitingUserOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            validateWaitingUserOperationResult(false, "Error sending message", null);
        }
    }

    /**
     * Try signing up user with username-password via the Service.
     */
    private void doSignUp(String username, String password) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            signUpOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isSigningUp = getMobileClientService().signUp(username, password);
            if (!isSigningUp) {
                signUpOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            signUpOperationResult(false, "Error sending message", null);
        }
    }

    @Override
    public void sendResults(MobileClientData data) {

        int operationType = data.getOperationType();
        boolean isOperationSuccessful
                = data.getOperationResult() == MobileClientData.OPERATION_SUCCESS;

        if (operationType == MobileClientData.OPERATION_SIGN_UP) {
            signUpOperationResult(isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getUser());
        }
        else if (data.getOperationType() == MobileClientData.OPERATION_VALIDATE) {
            validateWaitingUserOperationResult(isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getWaitingUser());
        }
    }

    /**
     * Handle the operation result of validating the user with the username-code combo.
     */
    private void validateWaitingUserOperationResult(boolean wasOperationSuccessful, String message, WaitingUser waitingUser) {

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

    /**
     * Handle the operation result of signing up.
     */
    private void signUpOperationResult(boolean wasOperationSuccessful, String message, User user) {

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
}
