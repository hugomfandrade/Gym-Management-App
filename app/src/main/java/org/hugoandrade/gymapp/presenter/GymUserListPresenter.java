package org.hugoandrade.gymapp.presenter;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

import java.util.List;

public class GymUserListPresenter extends MobileClientPresenterBase<MVP.RequiredGymUserListViewOps>

        implements MVP.ProvidedGymUserListPresenterOps {

    @Override
    public void onCreate(MVP.RequiredGymUserListViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the GymUserListModel class to instantiate/manage and
        // "this" to provide GymUserListModel with this MVP.RequiredGymUserListModelOps
        // instance.
        super.onCreate(view);
    }

    @Override
    public void notifyServiceIsBound() {
        // Once service is bound, get all gym users that have the selected 'credential'
        getAllGymUsers();
    }

    private void getAllGymUsers() {
        // disable UI while waiting for web service response
        getView().disableUI();

        // get all gym users with the selected credential
        doGetAllGymUsers(getView().getCredential());
    }

    private void doGetAllGymUsers(String credential) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingAllGymUsersOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isGetting = getMobileClientService().getAllGymUsers(credential);
            if (!isGetting) {
                gettingAllGymUsersOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingAllGymUsersOperationResult(false, "Error sending message", null);
        }
    }

    @Override
    public void sendResults(MobileClientData data) {

        int operationType = data.getOperationType();
        boolean isOperationSuccessful
                = data.getOperationResult() == MobileClientData.OPERATION_SUCCESS;

        if (operationType == MobileClientData.OPERATION_GET_ALL_USER) {
            gettingAllGymUsersOperationResult(isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getUserList());
        }
    }

    private void gettingAllGymUsersOperationResult(boolean wasOperationSuccessful,
                                                   String errorMessage,
                                                   List<User> userList) {

        if (wasOperationSuccessful) {

            // operation was successful, display gym users
            getView().displayGymUserList(userList);
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
