package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

import java.util.List;

public class StaffMainModel extends MobileClientModelBase<MVP.RequiredStaffMainPresenterOps>

    implements MVP.ProvidedStaffMainModelOps {

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_GET_MY_MEMBERS) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                gettingMyMembersRequestResultSuccess(data.getUserList());
            else
                gettingMyMembersRequestResultFailure(data.getErrorMessage());
        }
    }

    @Override
    public void getMyMembers(String userID) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingMyMembersRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isGetting = getService().getMyGymMembers(userID);
            if (!isGetting) {
                gettingMyMembersRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingMyMembersRequestResultFailure("Error sending message");
        }
    }

    private void gettingMyMembersRequestResultFailure(String errorMessage) {
        getPresenter().gettingMyMembersOperationResult(false, errorMessage, null);
    }

    private void gettingMyMembersRequestResultSuccess(List<User> myMemberList) {
        getPresenter().gettingMyMembersOperationResult(true, null, myMemberList);
    }
}
