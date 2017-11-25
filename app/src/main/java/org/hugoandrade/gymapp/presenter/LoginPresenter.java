package org.hugoandrade.gymapp.presenter;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;
import org.hugoandrade.gymapp.model.service.MobileClientService;
import org.hugoandrade.gymapp.provider.StorageOps;

public class LoginPresenter extends MobileClientPresenterBase<MVP.RequiredLoginViewOps>

        implements MVP.ProvidedLoginPresenterOps,
                   StorageOps.StorageOpsListener {

    private StorageOps mStorageOps;

    @Override
    public void onCreate(MVP.RequiredLoginViewOps view) {

        // Start service
        view.getApplicationContext()
                .startService(MobileClientService.makeIntent(view.getApplicationContext()));

        // Invoke the special onCreate() method in PresenterBase,
        // passing in the LoginModel class to instantiate/manage and
        // "this" to provide LoginModel with this MVP.RequiredLoginModelOps
        // instance.
        super.onCreate(view);

        // Initialize StorageOps
        mStorageOps = new StorageOps(getActivityContext(), this);

        // get last used username-password from StorageProvider
        getLastLoginUser();
    }

    @Override
    public void notifyServiceIsBound() {
        // No-ops
    }

    @Override
    public void login(String username, String password) {
        // disable UI while waiting for web service response
        getView().disableUI();

        // try to login with username-password combo
        doLogin(username, password);
    }

    private void doLogin(String username, String password) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            loginOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isLoggingIn = getMobileClientService().login(username, password);
            if (!isLoggingIn) {
                loginOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            loginOperationResult(false, "Error sending message", null);
        }
    }

    @Override
    public void displayLastLogin(User user) {
        getView().displayLastLogin(user);
    }

    private void getLastLoginUser() {
        try {
            mStorageOps.getLastLoginUser();
        } catch (RemoteException e) {
            e.printStackTrace();
            displayLastLogin(new User());
        }
    }

    private void insertLastLogin(User user) {
        try {
            mStorageOps.deleteAll();
            mStorageOps.insertLastLogin(user);
        } catch (RemoteException e) {
            Log.d(TAG, "exception " + e);
        }
    }

    @Override
    public void sendResults(MobileClientData data) {

        int operationType = data.getOperationType();
        boolean isOperationSuccessful
                = data.getOperationResult() == MobileClientData.OPERATION_SUCCESS;

        if (operationType == MobileClientData.OPERATION_LOGIN) {
            loginOperationResult(
                    isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getUser());
        }
    }

    private void loginOperationResult(boolean wasOperationSuccessful, String message, User user) {
        if (wasOperationSuccessful) {

            // operation was successful, go to main activity and store username-password
            // in the StorageProvider
            insertLastLogin(user);

            GlobalData.initializeUser(user);

            getView().successfulLogin(user.getCredential());
        }
        else {
            // operation failed, show error message
            if (message != null)
                getView().reportMessage(message);
        }

        // enable UI
        getView().enableUI();
    }
}
