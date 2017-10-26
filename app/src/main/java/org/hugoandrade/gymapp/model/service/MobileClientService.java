package org.hugoandrade.gymapp.model.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

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
import org.hugoandrade.gymapp.data.WaitingUser;
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

public class MobileClientService extends LifecycleLoggingService {

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
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerReceiver(mReceiver,
                new IntentFilter(NetworkChangeBroadcastReceiver.ACTION_NETWORK_CHANGE_RECEIVER));

        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            initMobileServiceClient();
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }

    private final IMobileClientService.Stub mBinder = new IMobileClientService.Stub() {

        @Override // ******* OK *******//
        public boolean login(String username, final String password) throws RemoteException {
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
                    user.setPassword(password);

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
        public boolean signUp(final String username, final String password) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            ArrayList<Pair<String, String>> parameters = new ArrayList<>();
            parameters.add(new Pair<>(User.Entry.REQUEST_TYPE, User.Entry.SIGN_UP));

            ListenableFuture<JsonObject> future =
                    new MobileServiceJsonTable(User.Entry.TABLE_NAME, mMobileServiceClient)
                            .insert(formatter.getAsJsonObject(username, password), parameters);
            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject jsonObject) {
                    Log.e(TAG, "SignUp: " + jsonObject.toString());

                    MobileClientData m = new MobileClientData(
                            MobileClientData.OPERATION_SIGN_UP,
                            MobileClientData.OPERATION_SUCCESS);
                    m.setUser(new User(username, password));

                    try {
                        if (mCallback != null)
                            mCallback.sendResults(m);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e(TAG, "SignUp: " + t.getMessage());
                    reportOperationFailure(MobileClientData.OPERATION_SIGN_UP, t.getMessage());
                }
            });
            return true;
        }

        @Override
        public boolean getAllGymUsers(final String credential) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            try {
                ListenableFuture<JsonElement> future
                        = new MobileServiceJsonTable(credential, mMobileServiceClient)
                        .execute();
                Futures.addCallback(future, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement jsonElement) {
                        List<String> idList = parser.parseStrings(jsonElement, User.Credential.Cols.USER_ID);

                        getUsers(idList, credential);
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        reportOperationFailure(MobileClientData.OPERATION_GET_ALL_USER, t.getMessage());
                    }
                });
            } catch (MobileServiceException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        private void getUsers(List<String> idList, final String credential) {
            new GetUserAsyncTask(credential, mMobileServiceClient)
                    .setOnFinishedListener(new GetUserAsyncTask.OnFinishedListener() {
                        @Override
                        public void onFinished(MobileClientData mobileClientData) {
                            try {
                                if (mCallback != null)
                                    mCallback.sendResults(mobileClientData);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                       idList.toArray(new String[idList.size()]));
        }

        @Override
        public boolean createGymUser(WaitingUser waitingUser) throws RemoteException {
            // ******* OK *******//
            if (mMobileServiceClient == null)
                return false;

            ListenableFuture<JsonObject> future
                    = new MobileServiceJsonTable(WaitingUser.Entry.TABLE_NAME, mMobileServiceClient)
                    .insert(formatter.getAsJsonObject(waitingUser));
            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject jsonObject) {

                    MobileClientData m = new MobileClientData(
                            MobileClientData.OPERATION_CREATE_USER,
                            MobileClientData.OPERATION_SUCCESS);
                    m.setWaitingUser(parser.parseWaitingUser(jsonObject));

                    try {
                        if (mCallback != null)
                            mCallback.sendResults(m);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    reportOperationFailure(MobileClientData.OPERATION_CREATE_USER, t.getMessage());
                }
            });
            return true;
        }

        @Override
        public boolean validateGymUser(final WaitingUser waitingUser) throws RemoteException {
            // ******* OK *******//
            if (mMobileServiceClient == null)
                return false;

            ListenableFuture<JsonElement> future
                    = new MobileServiceJsonTable(WaitingUser.Entry.TABLE_NAME, mMobileServiceClient)
                    .where().field(WaitingUser.Entry.Cols.USERNAME).eq(waitingUser.getUsername())
                    .and().field(WaitingUser.Entry.Cols.CODE).eq(waitingUser.getCode())
                    .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement jsonElement) {
                    Log.e(TAG, "ValidateUser: " + jsonElement.toString());

                    if (jsonElement.getAsJsonArray().size() == 0) {
                        reportOperationFailure(
                                MobileClientData.OPERATION_VALIDATE,
                                "Username-Code combo does not exist");
                        return;
                    }

                    MobileClientData m = new MobileClientData(
                            MobileClientData.OPERATION_VALIDATE,
                            MobileClientData.OPERATION_SUCCESS);
                    m.setWaitingUser(parser.parseWaitingUser(jsonElement.getAsJsonArray().get(0).getAsJsonObject()));

                    try {
                        if (mCallback != null)
                            mCallback.sendResults(m);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e(TAG, "ValidateUser: " + t.getMessage());
                    reportOperationFailure(MobileClientData.OPERATION_VALIDATE, t.getMessage());
                }
            });
            return true;
        }

        private void getCredential(final User user) {
            new GetCredentialAsyncTask(mMobileServiceClient)
                    .setOnFinishedListener(new GetCredentialAsyncTask.OnFinishedListener() {
                        @Override
                        public void onFinished(MobileClientData mobileClientData) {
                            try {
                                if (mCallback != null)
                                    mCallback.sendResults(mobileClientData);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onSuccessfulLogin(User user) {
                            MobileServiceUser mMobileServiceUser = new MobileServiceUser(user.getUserID());
                            mMobileServiceUser.setAuthenticationToken(user.getToken());
                            mMobileServiceClient.setCurrentUser(mMobileServiceUser);
                        }
                    })
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user);
        }

        @Override
        public void registerCallback(IMobileClientServiceCallback cb) throws RemoteException {
            Log.d(TAG, "-- registerCallback -- ");
            // TODO if (mCallback != null) mCallback.onUnregistered();
            mCallback = cb;
        }

        @Override
        public void unregisterCallback(IMobileClientServiceCallback cb) throws RemoteException {
            Log.d(TAG, "-- unregisterCallback -- ");
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

    private static class GetUserAsyncTask extends AsyncTask<String, Void, Void> {

        private static final String TAG = GetUserAsyncTask.class.getSimpleName();

        private final String mCredential;
        private final MobileServiceClient mMobileServiceClient;

        private OnFinishedListener mListener;

        private MobileClientDataJsonParser parser = new MobileClientDataJsonParser();

        GetUserAsyncTask(String credential, MobileServiceClient mobileServiceClient) {

            mCredential = credential;
            mMobileServiceClient = mobileServiceClient;
        }

        GetUserAsyncTask setOnFinishedListener(OnFinishedListener listener) {
            mListener = listener;
            return this;
        }

        @Override
        protected Void doInBackground(String... ids) {

            List<User> userList = new ArrayList<>();

            for (String id : ids) {
                try {
                    JsonElement userResult =
                            new MobileServiceJsonTable(User.Entry.TABLE_NAME, mMobileServiceClient)
                                    .where().field(User.Entry.Cols.ID).eq(id)
                                    .select(User.Entry.Cols.ID, User.Entry.Cols.USERNAME)
                                    .execute().get();

                    if (userResult.getAsJsonArray().size() != 0) {
                        User user = parser.parseUser(userResult.getAsJsonArray().get(0).getAsJsonObject());
                        user.setCredential(mCredential);
                        userList.add(user);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }

            MobileClientData m = new MobileClientData(
                    MobileClientData.OPERATION_GET_ALL_USER,
                    MobileClientData.OPERATION_SUCCESS);
            m.setUserList(userList);

            if (mListener != null)
                mListener.onFinished(m);

            return null;
        }

        interface OnFinishedListener {
            void onFinished(MobileClientData mobileClientData);
        }
    }

    private static class GetCredentialAsyncTask extends AsyncTask<User, Void, Void> {

        @SuppressWarnings("unused")
        private static final String TAG = GetCredentialAsyncTask.class.getSimpleName();

        private final MobileServiceClient mMobileServiceClient;

        private OnFinishedListener mListener;

        GetCredentialAsyncTask(MobileServiceClient mobileServiceClient) {
            mMobileServiceClient = mobileServiceClient;
        }

        GetCredentialAsyncTask setOnFinishedListener(OnFinishedListener listener) {
            mListener = listener;
            return this;
        }

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

                sendFailedLoginResult("Credential not found");

            } catch (InterruptedException | ExecutionException e) {

                sendFailedLoginResult(e.getMessage());
            }
            return null;
        }

        private void sendSuccessfulLoginResult(User user) {
            if (mListener != null)
                mListener.onSuccessfulLogin(user);

            MobileClientData m = new MobileClientData(
                    MobileClientData.OPERATION_LOGIN,
                    MobileClientData.OPERATION_SUCCESS);
            m.setUser(user);


            if (mListener != null)
                mListener.onFinished(m);
        }

        private void sendFailedLoginResult(String message) {

            MobileClientData m = new MobileClientData(
                    MobileClientData.OPERATION_LOGIN,
                    MobileClientData.OPERATION_FAILURE);
            m.setErrorMessage(message);

            if (mListener != null)
                mListener.onFinished(m);
        }

        interface OnFinishedListener {
            void onFinished(MobileClientData mobileClientData);
            void onSuccessfulLogin(User user);
        }
    }

    private void initMobileServiceClient() {
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

    private void initMobileServiceClient(Context context, ServiceFilter filter) throws MalformedURLException {
        if (isClientInitialized)
            return;

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
    }

    private void setContextAndFilter(Context context, ServiceFilter filter){
        if (isClientInitialized)
            return;

        if (context != null)
            mMobileServiceClient.setContext(context);
        if (filter != null)
            mMobileServiceClient = mMobileServiceClient.withFilter(filter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtils.isNetworkAvailable(context)) {
                initMobileServiceClient();
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
                public void onFailure(@NonNull Throwable e) {
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
