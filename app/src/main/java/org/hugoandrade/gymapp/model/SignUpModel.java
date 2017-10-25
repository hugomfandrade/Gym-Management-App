package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

public class SignUpModel extends MobileClientModelBase<MVP.RequiredSignUpPresenterOps>

        implements MVP.ProvidedSignUpModelOps {

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_SIGN_UP) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                signingUpRequestResultSuccess(data.getUser());
            else
                signingUpRequestResultFailure(data.getErrorMessage());
        }
        else if (data.getOperationType() == MobileClientData.OPERATION_VALIDATE) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                validatingUserRequestResultSuccess(data.getWaitingUser());
            else
                validatingUserRequestResultFailure(data.getErrorMessage());
        }
    }

    @Override
    public void validateWaitingUser(WaitingUser waitingUser) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            validatingUserRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isValidatingUser = getService().validateGymUser(waitingUser);
            if (!isValidatingUser) {
                validatingUserRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            validatingUserRequestResultFailure("Error sending message");
        }
    }

    @Override
    public void signUp(String username, String password) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            signingUpRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isLoggingIn = getService().signUp(username, password);
            if (!isLoggingIn) {
                signingUpRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            signingUpRequestResultFailure("Error sending message");
        }
    }

    private void signingUpRequestResultFailure(String errorMessage) {
        getPresenter().signUpOperationResult(false, errorMessage, null);
    }

    private void signingUpRequestResultSuccess(User user) {
        getPresenter().signUpOperationResult(true, null, user);
    }

    private void validatingUserRequestResultFailure(String errorMessage) {
        getPresenter().validateWaitingUserOperationResult(false, errorMessage, null);
    }

    private void validatingUserRequestResultSuccess(WaitingUser waitingUser) {
        getPresenter().validateWaitingUserOperationResult(true, null, waitingUser);
    }
}
