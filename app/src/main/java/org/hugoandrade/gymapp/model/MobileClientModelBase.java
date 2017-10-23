package org.hugoandrade.gymapp.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

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

    private boolean isServiceBound = false;

    public MobileClientModelBase() {
    }

    @Override
    public void onCreate(RequiredPresenterOps presenter) {
        mPresenter = new WeakReference<>(presenter);
        bindToMobileServiceClientService();
    }

    public void onDestroy(boolean isChangingConfigurations) {
        if (!isChangingConfigurations)
            unbindToMobileServiceClientService();

    }

    private void bindToMobileServiceClientService() {
        if (!isServiceBound) {
            mPresenter.get().getApplicationContext().bindService(
                    MobileClientService.makeIntent(mPresenter.get().getActivityContext()),
                    mServiceConnection,
                    Context.BIND_AUTO_CREATE);
            isServiceBound = true;
        }
    }

    private void unbindToMobileServiceClientService() {
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
            mPresenter.get().notifyServiceIsBound(); /**/
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            isServiceBound = false;
        }
    };

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
