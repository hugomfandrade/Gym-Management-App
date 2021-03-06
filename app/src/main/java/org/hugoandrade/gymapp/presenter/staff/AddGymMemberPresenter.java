package org.hugoandrade.gymapp.presenter.staff;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;
import org.hugoandrade.gymapp.presenter.MobileClientPresenterBase;

import java.util.List;

public class AddGymMemberPresenter extends MobileClientPresenterBase<MVP.RequiredAddGymMemberViewOps>

        implements MVP.ProvidedAddGymMemberPresenterOps {

    @Override
    public void onCreate(MVP.RequiredAddGymMemberViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the ImageModel class to instantiate/manage and
        // "this" to provide MobileClientModel with this MVP.RequiredMobileServicePresenterOps
        // instance.
        super.onCreate(view);
    }

    @Override
    public void notifyServiceIsBound() {
        getGymMembersExceptMine();
    }

    private void getGymMembersExceptMine() {
        getView().disableUI();

        doGetGymMembersExceptMine(GlobalData.getUser().getID());
    }

    @Override
    public void addMemberToMyMembers(User member, String userID) {
        getView().disableUI();

        doAddMemberToMyMembers(member, userID);
    }

    /**
     * Try getting all gym members that are not in the staff's (with ID userID)
     * "My Members" list via the Service.
     */
    private void doGetGymMembersExceptMine(String userID) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingAllGymMembersOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isGetting = getMobileClientService().getGymMembersExceptMine(userID);
            if (!isGetting) {
                gettingAllGymMembersOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingAllGymMembersOperationResult(false, "Error sending message", null);
        }
    }

    /**
     * Try adding the selected member to the staff's "My Members" list via the Service.
     */
    private void doAddMemberToMyMembers(User member, String userID) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            addingMemberToMyMembersOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isGetting = getMobileClientService().addMemberToMyMembers(member, userID);
            if (!isGetting) {
                addingMemberToMyMembersOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            addingMemberToMyMembersOperationResult(false, "Error sending message", null);
        }
    }

    @Override
    public void sendResults(MobileClientData data) {

        int operationType = data.getOperationType();
        boolean isOperationSuccessful
                = data.getOperationResult() == MobileClientData.OPERATION_SUCCESS;

        if (operationType == MobileClientData.OPERATION_GET_MEMBERS_EXCEPT_MINE) {
            gettingAllGymMembersOperationResult(
                    isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getUserList());
        }
        else if (operationType == MobileClientData.OPERATION_ADD_MEMBER_TO_MY_MEMBERS) {
            addingMemberToMyMembersOperationResult(
                    isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getUser());
        }
    }

    /**
     * Handle the operation result of getting all gym members that are not
     * in the "My Members" of the staff
     */
    private void gettingAllGymMembersOperationResult(boolean wasOperationSuccessful,
                                                     String errorMessage,
                                                     List<User> myMemberList) {

        if (wasOperationSuccessful) {

            getView().displayGymMemberList(myMemberList);
        }
        else {
            if (errorMessage != null)
                getView().reportMessage(errorMessage);
        }

        getView().enableUI();

    }

    /**
     * Handle the operation result of adding member to staff's "My Members" list.
     */
    private void addingMemberToMyMembersOperationResult(boolean wasOperationSuccessful,
                                                        String errorMessage,
                                                        User member) {

        if (wasOperationSuccessful) {

            getView().memberAdded(member);
        }
        else {
            if (errorMessage != null)
                getView().reportMessage(errorMessage);
        }

        getView().enableUI();
    }
}
