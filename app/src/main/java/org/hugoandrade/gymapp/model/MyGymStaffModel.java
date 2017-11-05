package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

import java.util.List;

public class MyGymStaffModel extends MobileClientModelBase<MVP.RequiredMyGymStaffPresenterOps>

    implements MVP.ProvidedMyGymStaffModelOps {

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_GET_MY_STAFF) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                gettingMyGymStaffRequestResultSuccess(data.getUserList());
            else
                gettingMyGymStaffRequestResultFailure(data.getErrorMessage());
        }
    }

    @Override
    public void getMyGymStaff(String userID) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingMyGymStaffRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isGetting = getService().getMyGymStaff(userID);
            if (!isGetting) {
                gettingMyGymStaffRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingMyGymStaffRequestResultFailure("Error sending message");
        }
    }

    private void gettingMyGymStaffRequestResultFailure(String errorMessage) {
        getPresenter().gettingMyGymStaffOperationResult(false, errorMessage, null);
    }

    private void gettingMyGymStaffRequestResultSuccess(List<User> staffList) {
        getPresenter().gettingMyGymStaffOperationResult(true, null, staffList);
    }
}
