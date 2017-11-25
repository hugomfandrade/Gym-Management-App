package org.hugoandrade.gymapp.presenter.member;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;
import org.hugoandrade.gymapp.presenter.MobileClientPresenterBase;

import java.util.List;

public class MyGymStaffPresenter extends MobileClientPresenterBase<MVP.RequiredMyGymStaffViewOps>

        implements MVP.ProvidedMyGymStaffPresenterOps {

    @Override
    public void onCreate(MVP.RequiredMyGymStaffViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the MyGymStaffModel class to instantiate/manage and
        // "this" to provide MobileClientModel with this MVP.RequiredMobileServicePresenterOps
        // instance.
        super.onCreate(view);
    }

    @Override
    public void notifyServiceIsBound() {
        // Once service is bound, get gym staff of the logged in user's 'My Gym Staff'
        getMyGymStaff();
    }

    private void getMyGymStaff() {
        // disable UI while waiting for web service response
        getView().disableUI();

        // get gym staff of the logged in gym member
        doGetMyGymStaff(GlobalData.getUser().getID());
    }

    /**
     * Try getting the staff of the member with ID userID via the Service.
     */
    private void doGetMyGymStaff(String userID) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingMyGymStaffOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isGetting = getMobileClientService().getMyGymStaff(userID);
            if (!isGetting) {
                gettingMyGymStaffOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingMyGymStaffOperationResult(false, "Error sending message", null);
        }
    }

    @Override
    public void sendResults(MobileClientData data) {

         int operationType = data.getOperationType();
         boolean isOperationSuccessful
                 = data.getOperationResult() == MobileClientData.OPERATION_SUCCESS;

         if (operationType == MobileClientData.OPERATION_GET_MY_STAFF) {
             gettingMyGymStaffOperationResult(isOperationSuccessful,
                     data.getErrorMessage(),
                     data.getUserList());
        }
    }

    /**
     * Handle the operation result of getting the member's staffs
     */
    private void gettingMyGymStaffOperationResult(boolean wasOperationSuccessful,
                                                  String errorMessage,
                                                  List<User> staffList) {

        if (wasOperationSuccessful) {

            // operation was successful, display list of gym staff
            getView().displayGymStaffList(staffList);
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
