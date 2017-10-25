package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

import java.util.List;

public class GymUserListModel extends MobileClientModelBase<MVP.RequiredGymUserListPresenterOps>

    implements MVP.ProvidedGymUserListModelOps {

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_GET_ALL_USER) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                gettingAllGymUsersRequestResultSuccess(data.getUserList());
            else
                gettingAllGymUsersRequestResultFailure(data.getErrorMessage());
        }
    }

    @Override
    public void getAllGymUsers(String credential) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingAllGymUsersRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isLoggingIn = getService().getAllGymUsers(credential);
            if (!isLoggingIn) {
                gettingAllGymUsersRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingAllGymUsersRequestResultFailure("Error sending message");
        }
    }

    private void gettingAllGymUsersRequestResultFailure(String errorMessage) {
        getPresenter().gettingAllGymUsersOperationResult(false, errorMessage, null);
    }

    private void gettingAllGymUsersRequestResultSuccess(List<User> userList) {
        getPresenter().gettingAllGymUsersOperationResult(true, null, userList);
    }
}
