package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

import java.util.List;

public class AddGymMemberModel extends MobileClientModelBase<MVP.RequiredAddGymMemberPresenterOps>

    implements MVP.ProvidedAddGymMemberModelOps {

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_GET_MEMBERS_EXCEPT_MINE) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                gettingGymMembersRequestResultSuccess(data.getUserList());
            else
                gettingGymMembersRequestResultFailure(data.getErrorMessage());
        }
        else if (data.getOperationType() == MobileClientData.OPERATION_ADD_MEMBER_TO_MY_MEMBERS) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                addingMemberToMyMembersRequestResultSuccess(data.getUser());
            else
                addingMemberToMyMembersRequestResultFailure(data.getErrorMessage());
        }
    }

    @Override
    public void getGymMembersExceptMine(String userID) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingGymMembersRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isGetting = getService().getGymMembersExceptMine(userID);
            if (!isGetting) {
                gettingGymMembersRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingGymMembersRequestResultFailure("Error sending message");
        }
    }

    @Override
    public void addMemberToMyMembers(User member, String userID) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingGymMembersRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isGetting = getService().addMemberToMyMembers(member, userID);
            if (!isGetting) {
                gettingGymMembersRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingGymMembersRequestResultFailure("Error sending message");
        }
    }

    private void gettingGymMembersRequestResultFailure(String errorMessage) {
        getPresenter().gettingAllGymMembersOperationResult(false, errorMessage, null);
    }

    private void gettingGymMembersRequestResultSuccess(List<User> myMemberList) {
        getPresenter().gettingAllGymMembersOperationResult(true, null, myMemberList);
    }

    private void addingMemberToMyMembersRequestResultFailure(String errorMessage) {
        getPresenter().addingMemberToMyMembersOperationResult(false, errorMessage, null);
    }

    private void addingMemberToMyMembersRequestResultSuccess(User member) {
        getPresenter().addingMemberToMyMembersOperationResult(true, null, member);
    }
}
