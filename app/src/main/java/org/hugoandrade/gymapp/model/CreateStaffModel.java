package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

import java.util.List;

public class CreateStaffModel extends MobileClientModelBase<MVP.RequiredCreateStaffPresenterOps>

    implements MVP.ProvidedCreateStaffModelOps {

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_CREATE_STAFF) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                creatingStaffRequestResultSuccess(data.getUsername(), data.getCode());
            else
                creatingStaffRequestResultFailure(data.getErrorMessage());
        }
    }

    @Override
    public void createStaff(String username) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            creatingStaffRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isLoggingIn = getService().createStaff(username);
            if (!isLoggingIn) {
                creatingStaffRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            creatingStaffRequestResultFailure("Error sending message");
        }
    }

    private void creatingStaffRequestResultFailure(String errorMessage) {
        getPresenter().creatingStaffOperationResult(false, errorMessage, null, null);
    }

    private void creatingStaffRequestResultSuccess(String username, String code) {
        getPresenter().creatingStaffOperationResult(true, null, username, code);
    }
}
