package org.hugoandrade.gymapp.presenter;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

public class CreateGymUserPresenter extends MobileClientPresenterBase<MVP.RequiredCreateGymUserViewOps>

        implements MVP.ProvidedCreateGymUserPresenterOps {

    @Override
    public void onCreate(MVP.RequiredCreateGymUserViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the MVP.RequiredCreateGymUserViewOps class to
        // instantiate/manage and "this" to provide CreateGymUserModel
        // with this MVP.ProvidedCreateGymUserModelOps instance.
        super.onCreate(view);
    }

    @Override
    public void notifyServiceIsBound() {
        //No-ops
    }

    @Override
    public void createGymUser(String username, String credential) {
        // disable UI while waiting for web service response
        getView().disableUI();

        // create gym user with 'username' as username and of type 'credential'
        doCreateGymUser(new WaitingUser(username, credential));
    }

    private void doCreateGymUser(WaitingUser waitingUser) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            creatingGymUserOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isCreating = getMobileClientService().createGymUser(waitingUser);
            if (!isCreating) {
                creatingGymUserOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            creatingGymUserOperationResult(false, "Error sending message", null);
        }
    }

    @Override
    public void sendResults(MobileClientData data) {

        int operationType = data.getOperationType();
        boolean isOperationSuccessful
                = data.getOperationResult() == MobileClientData.OPERATION_SUCCESS;

        if (operationType == MobileClientData.OPERATION_CREATE_USER) {
            creatingGymUserOperationResult(
                    isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getWaitingUser());
        }
    }

    private void creatingGymUserOperationResult(boolean wasOperationSuccessful, String message, WaitingUser waitingUser) {
        if (wasOperationSuccessful) {

            // operation was successful, show generated code
            getView().successfulCreateGymUser(waitingUser);
        }
        else {
            // operation failed, show error message
            if (message != null)
                getView().reportMessage(message);
        }

        // enable UI
        getView().enableUI();
    }

    @Override
    public Context getActivityContext() {
        return getView().getActivityContext();
    }

    @Override
    public Context getApplicationContext() {
        return getView().getApplicationContext();
    }
}
