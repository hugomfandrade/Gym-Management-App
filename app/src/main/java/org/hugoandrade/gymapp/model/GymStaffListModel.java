package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

import java.util.List;

public class GymStaffListModel extends MobileClientModelBase<MVP.RequiredGymStaffListPresenterOps>

    implements MVP.ProvidedGymStaffListModelOps {

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_GET_ALL_STAFF) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                gettingAllStaffRequestResultSuccess(data.getStaffList());
            else
                gettingAllStaffRequestResultFailure(data.getErrorMessage());
        }
    }

    @Override
    public void getAllStaff() {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingAllStaffRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isLoggingIn = getService().getAllStaff();
            if (!isLoggingIn) {
                gettingAllStaffRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingAllStaffRequestResultFailure("Error sending message");
        }
    }

    private void gettingAllStaffRequestResultFailure(String errorMessage) {
        getPresenter().gettingAllStaffOperationResult(false, errorMessage, null);
    }

    private void gettingAllStaffRequestResultSuccess(List<User> userList) {
        getPresenter().gettingAllStaffOperationResult(true, null, userList);
    }
}
