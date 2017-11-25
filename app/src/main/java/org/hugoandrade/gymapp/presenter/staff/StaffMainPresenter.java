package org.hugoandrade.gymapp.presenter.staff;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;
import org.hugoandrade.gymapp.presenter.MobileClientPresenterBase;

import java.util.List;

public class StaffMainPresenter extends MobileClientPresenterBase<MVP.RequiredStaffMainViewOps>

        implements MVP.ProvidedStaffMainPresenterOps {

    @Override
    public void onCreate(MVP.RequiredStaffMainViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the StaffMainModel class to instantiate/manage and
        // "this" to provide MobileClientModel with this MVP.RequiredMobileServicePresenterOps
        // instance.
        super.onCreate(view);
    }

    @Override
    public void notifyServiceIsBound() {
        // Once service is bound, get gym users
        getAllGymUsers();
    }

    private void getAllGymUsers() {
        // disable UI while waiting for web service response
        getView().disableUI();

        // get all gym users
        doGetMyMembers(GlobalData.getUser().getID());
    }

    /**
     * Try getting the members of the staff's "My Members" list via the Service.
     */
    private void doGetMyMembers(String userID) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingMyMembersOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isGetting = getMobileClientService().getMyGymMembers(userID);
            if (!isGetting) {
                gettingMyMembersOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingMyMembersOperationResult(false, "Error sending message", null);
        }
    }

    @Override
    public void sendResults(MobileClientData data) {

        int operationType = data.getOperationType();
        boolean isOperationSuccessful
                = data.getOperationResult() == MobileClientData.OPERATION_SUCCESS;

        if (operationType== MobileClientData.OPERATION_GET_MY_MEMBERS) {
            gettingMyMembersOperationResult(isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getUserList());
        }
    }

    /**
     * Handle the operation result of getting the members of the staff's "My Members" list
     */
    private void gettingMyMembersOperationResult(boolean wasOperationSuccessful,
                                                String errorMessage,
                                                List<User> myMemberList) {

        if (wasOperationSuccessful) {
            // operation was successful, display the retrieved data
            getView().displayMyMembersList(myMemberList);
        }
        else {
            // operation failed, show error message
            if (errorMessage != null)
                getView().reportMessage(errorMessage);
        }

        // enable UI
        getView().enableUI();

    }
}
