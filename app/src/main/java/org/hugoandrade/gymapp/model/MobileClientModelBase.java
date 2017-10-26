package org.hugoandrade.gymapp.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.common.ContextView;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;
import org.hugoandrade.gymapp.model.service.MobileClientService;

import java.lang.ref.WeakReference;

public abstract class MobileClientModelBase<RequiredPresenterOps extends MVP.RequiredMobileClientPresenterBaseOps>

        implements MVP.ProvidedMobileClientModelBaseOps<RequiredPresenterOps> {

    protected final String TAG = getClass().getSimpleName();

    private WeakReference<RequiredPresenterOps> mPresenter;

    private final Handler mHandler = new MHandler(this);

    private IMobileClientService mService;

    protected boolean isServiceBound = false;

    public MobileClientModelBase() {
    }

    @Override
    public void onCreate(RequiredPresenterOps presenter) {
        // Set the WeakReference.
        mPresenter =
                new WeakReference<>(presenter);

        // Bind to the Service.
        bindService();
    }

    public void onDestroy(boolean isChangingConfigurations) {
        if (isChangingConfigurations)
            Log.d(TAG,
                    "just a configuration change - unbindService() not called");
        else
            // Unbind from the Services only if onDestroy() is not
            // triggered by a runtime configuration change.
            unbindService();

    }

    protected void bindService() {
        if (!isServiceBound) {
            mPresenter.get().getApplicationContext().bindService(
                    MobileClientService.makeIntent(mPresenter.get().getActivityContext()),
                    mServiceConnection,
                    Context.BIND_AUTO_CREATE);
            isServiceBound = true;
        }
    }

    protected void unbindService() {
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
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mPresenter.get().notifyServiceIsBound();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            isServiceBound = false;
        }
    };

    @Override
    public void registerCallback() {
        if (mService != null)
            try {
                mService.registerCallback(mCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
    }

    protected IMobileClientService getService() {
        return mService;
    }

    protected RequiredPresenterOps getPresenter() {
        return mPresenter.get();
    }

    public abstract void sendResults(MobileClientData data);

    // -------------------------------
    // MobileClientService Communication callback
    // -------------------------------

    private IMobileClientServiceCallback mCallback = new IMobileClientServiceCallback.Stub() {

        @Override
        public void sendResults(MobileClientData mobileClientData) throws RemoteException {
            Message requestMessage = Message.obtain();
            requestMessage.obj = mobileClientData;
            mHandler.sendMessage(requestMessage);
        }
    };

    private static class MHandler extends Handler {

        private final WeakReference<MobileClientModelBase> mRef;

        MHandler(MobileClientModelBase ref) {
            mRef = new WeakReference<>(ref);
        }

        @Override
        public void handleMessage(Message msg) {
            mRef.get().sendResults((MobileClientData) msg.obj);
        }
    }
}
