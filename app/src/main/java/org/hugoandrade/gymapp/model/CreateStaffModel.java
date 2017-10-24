package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

public class CreateStaffModel extends MobileClientModelBase<MVP.RequiredCreateStaffPresenterOps>

    implements MVP.ProvidedCreateStaffModelOps {

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_CREATE_STAFF) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                creatingUserRequestResultSuccess(data.getWaitingUser());
            else
                creatingUserRequestResultFailure(data.getErrorMessage());
        }
    }

    @Override
    public void createUser(WaitingUser waitingUser) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            creatingUserRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isLoggingIn = getService().createUser(waitingUser);
            if (!isLoggingIn) {
                creatingUserRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            creatingUserRequestResultFailure("Error sending message");
        }
    }

    private void creatingUserRequestResultFailure(String errorMessage) {
        getPresenter().creatingStaffOperationResult(false, errorMessage, null);
    }

    private void creatingUserRequestResultSuccess(WaitingUser waitingUser) {
        getPresenter().creatingStaffOperationResult(true, null, waitingUser);
    }
}
