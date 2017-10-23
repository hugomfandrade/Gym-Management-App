package org.hugoandrade.gymapp.model.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceJsonTable;
import com.squareup.okhttp.OkHttpClient;

import org.hugoandrade.gymapp.DevConstants;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.IMobileClientService;
import org.hugoandrade.gymapp.model.IMobileClientServiceCallback;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;
import org.hugoandrade.gymapp.model.aidl.MobileClientDataJsonFormatter;
import org.hugoandrade.gymapp.model.aidl.MobileClientDataJsonParser;
import org.hugoandrade.gymapp.presenter.broadcastreceiver.NetworkChangeBroadcastReceiver;
import org.hugoandrade.gymapp.utils.NetworkUtils;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MobileClientService extends Service {

    private String TAG = getClass().getSimpleName();

    private boolean isClientInitialized = false;

    private IMobileClientServiceCallback mCallback;

    private MobileServiceClient mMobileServiceClient;

    private MobileClientDataJsonParser parser = new MobileClientDataJsonParser();
    private MobileClientDataJsonFormatter formatter = new MobileClientDataJsonFormatter();

    /**
     * Factory method that returns an explicit Intent for downloading
     * and image.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context, MobileClientService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerForNetworkChangeReceiver(true);
        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            initNuadaMobileServiceClient();
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;//mRequestMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;//super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerForNetworkChangeReceiver(false);
    }

    private final IMobileClientService.Stub mBinder = new IMobileClientService.Stub() {

        @Override // ******* OK *******//
        public boolean login(String username, String password) throws RemoteException {
            // ******* OK *******//
            if (mMobileServiceClient == null)
                return false;

            ListenableFuture<JsonObject> future
                    = new MobileServiceJsonTable(User.Entry.TABLE_NAME, mMobileServiceClient)
                    .insert(formatter.getAsJsonObject(username, password));
            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject jsonObject) {

                    User user = parser.parseUser(jsonObject);

                    getCredential(user);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    reportOperationFailure(MobileClientData.OPERATION_LOGIN, t.getMessage());
                }
            });
            return true;
        }

        @Override
        public boolean getAllStaff() throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            try {
                ListenableFuture<JsonElement> future
                        = new MobileServiceJsonTable(User.Credential.STAFF, mMobileServiceClient)
                        .execute();
                Futures.addCallback(future, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement jsonElement) {
                        List<String> idList = parser.parseStrings(jsonElement, User.Credential.Cols.USER_ID);

                        List<User> userList = new ArrayList<>();

                        for (String id : idList) {
                            try {
                                JsonElement userResult = new MobileServiceJsonTable(User.Entry.TABLE_NAME, mMobileServiceClient)
                                        .where().field(User.Entry.Cols.ID).eq(id)
                                        .select(User.Entry.Cols.ID, User.Entry.Cols.USERNAME)
                                        .execute().get();

                                if (userResult.getAsJsonArray().size() != 0) {
                                    userList.add(parser.parseUser(userResult.getAsJsonArray().get(0).getAsJsonObject()));
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                        MobileClientData m = new MobileClientData(
                                MobileClientData.OPERATION_GET_ALL_STAFF,
                                MobileClientData.OPERATION_SUCCESS);
                        m.setStaffList(userList);
                        try {
                            if (mCallback != null)
                                mCallback.sendResults(m);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        reportOperationFailure(MobileClientData.OPERATION_GET_ALL_STAFF, t.getMessage());
                    }
                });
            } catch (MobileServiceException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        public boolean createStaff(String username) throws RemoteException {
            return false;
        }

        private void getCredential(final User user) {
            new AsyncTask<User, Void, Void>() {

                @Override
                protected Void doInBackground(User... users) {
                    User user = users[0];

                    String userID = user.getID();

                    try {
                        JsonElement adminResult =
                                new MobileServiceJsonTable(User.Credential.ADMIN, mMobileServiceClient)
                                        .where().field(User.Credential.Cols.USER_ID).eq(userID)
                                        .execute().get();

                        if (adminResult.getAsJsonArray().size() != 0) {
                            user.setCredential(User.Credential.ADMIN);
                            sendSuccessfulLoginResult(user);
                            return null;
                        }

                        JsonElement staffResult =
                                new MobileServiceJsonTable(User.Credential.STAFF, mMobileServiceClient)
                                        .where().field(User.Credential.Cols.USER_ID).eq(userID)
                                        .execute().get();

                        if (staffResult.getAsJsonArray().size() != 0) {
                            user.setCredential(User.Credential.STAFF);
                            sendSuccessfulLoginResult(user);
                            return null;
                        }

                        JsonElement memberResult =
                                new MobileServiceJsonTable(User.Credential.MEMBER, mMobileServiceClient)
                                        .where().field(User.Credential.Cols.USER_ID).eq(userID)
                                        .execute().get();

                        if (memberResult.getAsJsonArray().size() != 0) {
                            user.setCredential(User.Credential.MEMBER);
                            sendSuccessfulLoginResult(user);
                            return null;
                        }

                        reportOperationFailure(MobileClientData.OPERATION_LOGIN, "Credential not found");

                    } catch (InterruptedException | ExecutionException e) {

                        reportOperationFailure(MobileClientData.OPERATION_LOGIN, e.getMessage());
                    }
                    return null;
                }

                private void sendSuccessfulLoginResult(User user) {
                    Log.e(TAG, ":: " + user.getUserID());
                    Log.e(TAG, ":: " + user.getToken());
                    MobileServiceUser mMobileServiceUser = new MobileServiceUser(user.getUserID());
                    mMobileServiceUser.setAuthenticationToken(user.getToken());
                    mMobileServiceClient.setCurrentUser(mMobileServiceUser);

                    MobileClientData m = new MobileClientData(
                            MobileClientData.OPERATION_LOGIN,
                            MobileClientData.OPERATION_SUCCESS);
                    m.setUser(user);

                    try {
                        if (mCallback != null)
                            mCallback.sendResults(m);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user);
        }

        @Override
        public void registerCallback(IMobileClientServiceCallback cb) throws RemoteException {
            // TODO if (mCallback != null) mCallback.onUnregistered();
            mCallback = cb;
        }

        @Override
        public void unregisterCallback(IMobileClientServiceCallback cb) throws RemoteException {
            if (mCallback == cb)
                mCallback = null;
        }

        private void reportOperationFailure(int operationType, String message) {

            MobileClientData m = new MobileClientData(
                    operationType,
                    MobileClientData.OPERATION_FAILURE);
            m.setErrorMessage(message);

            try {
                if (mCallback != null)
                    mCallback.sendResults(m);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };


    private void initNuadaMobileServiceClient () {
        if (isClientInitialized)
            setContextAndFilter(getApplicationContext(), new ProgressFilter());
        else {
            try {
                initMobileServiceClient(getApplicationContext(), new ProgressFilter());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
    private void destroyMobileServiceClient() {
        isClientInitialized = false;
        mMobileServiceClient = null;
    }

    private void registerForNetworkChangeReceiver(boolean register) {
        if (register) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(NetworkChangeBroadcastReceiver.ACTION_NETWORK_CHANGE_RECEIVER);
            getApplicationContext().registerReceiver(mReceiver, filter);
        }
        else {
            getApplicationContext().unregisterReceiver(mReceiver);
        }
    }

    private boolean initMobileServiceClient(Context context, ServiceFilter filter) throws MalformedURLException {
        if (isClientInitialized)
            return false;
        else {


            mMobileServiceClient = new MobileServiceClient(
                    DevConstants.APP_URL,
                    context);
            // Extend timeout from default of 10s to 20s
            mMobileServiceClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });
            if (filter != null)
                mMobileServiceClient = mMobileServiceClient.withFilter(filter);
            isClientInitialized = true;
            return true;
        }
    }

    private boolean setContextAndFilter(Context context, ServiceFilter filter){
        if (isClientInitialized)
            return false;
        else {
            if(context != null)
                mMobileServiceClient.setContext(context);
            if(filter != null)
                mMobileServiceClient = mMobileServiceClient.withFilter(filter);
            return true;
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtils.isNetworkAvailable(context)) {
                initNuadaMobileServiceClient();
            }
            else {
                destroyMobileServiceClient();
            }
        }
    };

    private class ProgressFilter implements ServiceFilter {
        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(
                ServiceFilterRequest request,
                NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();
            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);
            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }
}
