package org.hugoandrade.gymapp.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;


import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;
import org.hugoandrade.gymapp.model.service.MobileClientService;

import java.lang.ref.WeakReference;

public class LoginModel implements MVP.ProvidedLoginModelOps {

    private final static String TAG = LoginModel.class.getSimpleName();

    private IMobileClientService mService;

    /**
     * A WeakReference used to access methods in the Presenter layer.
     * The WeakReference enables garbage collection.
     */
    private WeakReference<MVP.RequiredLoginPresenterOps> mPresenter;

    private final Handler mHandler = new MHandler(this);

    private boolean isServiceBound = false;

    @Override
    public void onCreate(MVP.RequiredLoginPresenterOps presenter) {
        // Set the WeakReference.
        mPresenter =
                new WeakReference<>(presenter);

        // Bind to the Service.
        bindService();
    }
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        if (isChangingConfigurations)
            Log.d(TAG,
                    "just a configuration change - unbindService() not called");
        else
            // Unbind from the Services only if onDestroy() is not
            // triggered by a runtime configuration change.
            unbindService();
    }

    private void bindService() {
        if (!isServiceBound) {
            Intent i = MobileClientService.makeIntent(mPresenter.get().getApplicationContext());
            mPresenter.get().getApplicationContext().startService(i);
            mPresenter.get().getApplicationContext().bindService(
                    i,
                    mServiceConnection,
                    Context.BIND_AUTO_CREATE);
            isServiceBound = true;
        }
    }

    private void unbindService() {
        if (isServiceBound) {
            if (mService != null) {
                try {
                    mService.unregisterCallback(mCallback);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }

            mPresenter.get().getApplicationContext().unbindService(mServiceConnection);

            isServiceBound = false;
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            mService = IMobileClientService.Stub.asInterface(binder);
            try {
                mService.registerCallback(mCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            isServiceBound = false;
        }
    };

    @Override
    public void login(String username, String password) {
        if (mService == null) {
            Log.w(TAG, "Service is still not bound");
            loggingInRequestResultFailure("Not bound to the service");
            return;
        }

        try {
            boolean isLoggingIn = mService.login(username, password);
            if (!isLoggingIn) {
                loggingInRequestResultFailure("No Network Connection");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            loggingInRequestResultFailure("Error sending message");
        }
    }

    private void loggingInRequestResultFailure(String errorMessage) {
        mPresenter.get().loginOperationResult(false, errorMessage, null);
    }

    private void loggingInRequestResultSuccess(User user) {
        mPresenter.get().loginOperationResult(true, null, user);
    }

    private void onOperationResult(MobileClientData mobileClientData) {
        if (mobileClientData.getOperationType() == MobileClientData.OPERATION_LOGIN) {
            if (mobileClientData.getOperationResult() == MobileClientData.OPERATION_SUCCESS)
                loggingInRequestResultSuccess(mobileClientData.getUser());
            else
                loggingInRequestResultFailure(mobileClientData.getErrorMessage());
        }
    }

    // -------------------------------
    // MobileClientService Communication callback
    // -------------------------------

    private IMobileClientServiceCallback mCallback = new IMobileClientServiceCallback.Stub() {

        @Override
        public void sendResults(MobileClientData mobileClientData) {
            Message requestMessage = Message.obtain();
            requestMessage.obj = mobileClientData;
            mHandler.sendMessage(requestMessage);
        }
    };


    private static class MHandler extends Handler {

        private final WeakReference<LoginModel> mRef;

        MHandler(LoginModel ref) {
            mRef = new WeakReference<>(ref);
        }

        @Override
        public void handleMessage(Message msg) {
            mRef.get().onOperationResult((MobileClientData) msg.obj);
        }
    }
}
