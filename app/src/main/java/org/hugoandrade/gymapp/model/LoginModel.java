package org.hugoandrade.gymapp.model;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;
import org.hugoandrade.gymapp.model.service.MobileClientService;
import org.hugoandrade.gymapp.provider.StorageOps;

public class LoginModel extends MobileClientModelBase<MVP.RequiredLoginPresenterOps>

        implements MVP.ProvidedLoginModelOps {

    private StorageOps mStorageOps;

    @Override
    public void onCreate(MVP.RequiredLoginPresenterOps presenter) {
        super.onCreate(presenter);

        mStorageOps = new StorageOps(getPresenter().getActivityContext(), this);
    }

    @Override
    protected void bindService() {
        if (!isServiceBound) {
            getPresenter().getApplicationContext().startService(
                    MobileClientService.makeIntent(getPresenter().getApplicationContext()));
        }
        super.bindService();
    }

    @Override
    public void getLastLogin() {
        try {
            mStorageOps.getLastLoginUser();
        } catch (RemoteException e) {
            e.printStackTrace();
            displayLastLogin(new User());
        }
    }

    @Override
    public void insertLastLogin(User user) {
        try {
            mStorageOps.deleteAll();
            mStorageOps.insertLastLogin(user);
        } catch (RemoteException e) {
            Log.d(TAG, "exception " + e);
        }
    }

    public void displayLastLogin(User user) {
        getPresenter().displayLastLogin(user);
    }

    @Override
    public void login(String username, String password) {
        if (getService() == null) {
            Log.w(TAG, "Service is still not bound");
            loggingInRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isLoggingIn = getService().login(username, password);
            if (!isLoggingIn) {
                loggingInRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            loggingInRequestResultFailure("Error sending message");
        }
    }

    @Override
    public void sendResults(MobileClientData data) {
        if (data.getOperationType() == MobileClientData.OPERATION_LOGIN) {
            if (data.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                loggingInRequestResultSuccess(data.getUser());
            else
                loggingInRequestResultFailure(data.getErrorMessage());
        }
    }

    private void loggingInRequestResultFailure(String errorMessage) {
        getPresenter().loginOperationResult(false, errorMessage, null);
    }

    private void loggingInRequestResultSuccess(User user) {
        getPresenter().loginOperationResult(true, null, user);
    }
}
