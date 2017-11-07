package org.hugoandrade.gymapp.model.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlanRecord;
import org.hugoandrade.gymapp.data.ExercisePlanRecordSuggested;
import org.hugoandrade.gymapp.data.ExerciseRecord;
import org.hugoandrade.gymapp.data.ExerciseSet;
import org.hugoandrade.gymapp.data.StaffMember;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MobileClientService extends LifecycleLoggingService {

    private boolean isClientInitialized = false;

    private IMobileClientServiceCallback mCallback;

    private MobileServiceClient mMobileServiceClient;
    private MobileServiceUser mMobileServiceUser;

    private MobileClientDataJsonParser parser = new MobileClientDataJsonParser();
    private MobileClientDataJsonFormatter formatter = new MobileClientDataJsonFormatter();

    /**
     * Factory method that returns an explicit Intent this Service
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
        if (mMobileServiceClient != null)
            mMobileServiceClient.setCurrentUser(mMobileServiceUser);
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

                        getUsers(idList, credential, MobileClientData.OPERATION_GET_ALL_USER);
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

        private void getUsers(List<String> idList, final String credential, final int operationType) {
            new GetUserAsyncTask(credential, mMobileServiceClient)
                    .setOnFinishedListener(new GetUserAsyncTask.OnFinishedListener() {
                        @Override
                        public void onFinished(List<User> userList) {

                            MobileClientData m = new MobileClientData(
                                    operationType,
                                    MobileClientData.OPERATION_SUCCESS);
                            m.setUserList(userList);

                            try {
                                if (mCallback != null)
                                    mCallback.sendResults(m);
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

        @Override
        public boolean getMyGymMembers(String userID) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            ListenableFuture<JsonElement> future
                    = new MobileServiceJsonTable(StaffMember.Entry.TABLE_NAME, mMobileServiceClient)
                    .where().field(StaffMember.Entry.Cols.STAFF_ID).eq(userID)
                    .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {
                    List<String> myMemberList = new ArrayList<>();

                    for (JsonElement item : result.getAsJsonArray())
                        myMemberList.add(parser.parseString(item.getAsJsonObject(), StaffMember.Entry.Cols.MEMBER_ID));

                    getUsers(myMemberList, User.Credential.MEMBER, MobileClientData.OPERATION_GET_MY_MEMBERS);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    reportOperationFailure(MobileClientData.OPERATION_GET_MY_MEMBERS, t.getMessage());
                }
            });

            return true;
        }

        @Override
        public boolean getGymMembersExceptMine(final String userID) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            try {
                ListenableFuture<JsonElement> future
                        = new MobileServiceJsonTable(User.Credential.MEMBER, mMobileServiceClient)
                        .execute();
                Futures.addCallback(future, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {
                        List<String> memberList = new ArrayList<>();

                        for (JsonElement item : result.getAsJsonArray())
                            memberList.add(parser.parseString(item.getAsJsonObject(), User.Credential.Cols.USER_ID));

                        getGymMembersExceptMine(memberList, userID);
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        reportOperationFailure(MobileClientData.OPERATION_GET_MEMBERS_EXCEPT_MINE, t.getMessage());
                    }
                });

            } catch (MobileServiceException e) {
                reportOperationFailure(MobileClientData.OPERATION_GET_MEMBERS_EXCEPT_MINE, e.getMessage());
            }
            return true;
        }

        @Override
        public boolean addMemberToMyMembers(final User member, String userID) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            StaffMember staffMember = new StaffMember(userID, member.getID());

            ListenableFuture<JsonObject> future
                    = new MobileServiceJsonTable(StaffMember.Entry.TABLE_NAME, mMobileServiceClient)
                    .insert(formatter.getAsJsonObject(staffMember));
            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject jsonObject) {

                    MobileClientData m = new MobileClientData(
                            MobileClientData.OPERATION_ADD_MEMBER_TO_MY_MEMBERS,
                            MobileClientData.OPERATION_SUCCESS);
                    m.setUser(member);

                    try {
                        if (mCallback != null)
                            mCallback.sendResults(m);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    reportOperationFailure(MobileClientData.OPERATION_ADD_MEMBER_TO_MY_MEMBERS, t.getMessage());
                }
            });
            return true;
        }

        @Override
        public boolean getMyGymStaff(String userID) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            ListenableFuture<JsonElement> future
                    = new MobileServiceJsonTable(StaffMember.Entry.TABLE_NAME, mMobileServiceClient)
                    .where().field(StaffMember.Entry.Cols.MEMBER_ID).eq(userID)
                    .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {
                    List<String> myMemberList = new ArrayList<>();

                    for (JsonElement item : result.getAsJsonArray())
                        myMemberList.add(parser.parseString(item.getAsJsonObject(), StaffMember.Entry.Cols.STAFF_ID));

                    getUsers(myMemberList, User.Credential.STAFF, MobileClientData.OPERATION_GET_MY_STAFF);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    reportOperationFailure(MobileClientData.OPERATION_GET_MY_STAFF, t.getMessage());
                }
            });

            return true;
        }

        @Override
        public boolean getAllExercises() throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            try {
                ListenableFuture<JsonElement> future
                        = new MobileServiceJsonTable(Exercise.Entry.TABLE_NAME, mMobileServiceClient)
                        .execute();
                Futures.addCallback(future, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {

                        MobileClientData m = new MobileClientData(
                                MobileClientData.OPERATION_GET_ALL_EXERCISES,
                                MobileClientData.OPERATION_SUCCESS);
                        m.setExerciseList(parser.parseExercises(result));

                        try {
                            if (mCallback != null)
                                mCallback.sendResults(m);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        reportOperationFailure(MobileClientData.OPERATION_GET_ALL_EXERCISES, t.getMessage());
                    }
                });
            } catch (MobileServiceException e) {
                e.printStackTrace();
                reportOperationFailure(MobileClientData.OPERATION_GET_ALL_EXERCISES, e.getMessage());
            }

            return true;
        }

        @Override
        public boolean createExercise(Exercise exercise) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            ListenableFuture<JsonObject> future
                    = new MobileServiceJsonTable(Exercise.Entry.TABLE_NAME, mMobileServiceClient)
                    .insert(formatter.getAsJsonObject(exercise));
            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject jsonObject) {

                    MobileClientData m = new MobileClientData(
                            MobileClientData.OPERATION_CREATE_EXERCISE,
                            MobileClientData.OPERATION_SUCCESS);
                    m.setExercise(parser.parseExercise(jsonObject));

                    try {
                        if (mCallback != null)
                            mCallback.sendResults(m);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    reportOperationFailure(MobileClientData.OPERATION_CREATE_EXERCISE, t.getMessage());
                }
            });
            return true;
        }

        @Override
        public boolean createWorkout(final ExercisePlanRecord exercisePlanRecord) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            ListenableFuture<JsonObject> future
                    = new MobileServiceJsonTable(ExercisePlanRecord.Entry.TABLE_NAME, mMobileServiceClient)
                    .insert(formatter.getAsJsonObject(exercisePlanRecord));
            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject jsonObject) {

                    String exercisePlanRecordID = parser.parseString(jsonObject, ExercisePlanRecord.Entry.Cols.ID);

                    for (final ExerciseSet exerciseSet : exercisePlanRecord.getExerciseSetList()) {
                        exerciseSet.setExercisePlanRecordID(exercisePlanRecordID);

                        ListenableFuture<JsonObject> future
                                = new MobileServiceJsonTable(ExerciseSet.Entry.TABLE_NAME, mMobileServiceClient)
                                .insert(formatter.getAsJsonObject(exerciseSet));
                        Futures.addCallback(future, new FutureCallback<JsonObject>() {
                            @Override
                            public void onSuccess(JsonObject jsonObject) {

                                String exerciseSetID = parser.parseString(jsonObject, ExerciseSet.Entry.Cols.ID);

                                for (final ExerciseRecord exerciseRecord : exerciseSet.getExerciseRecordList()) {
                                    exerciseRecord.setExerciseSetID(exerciseSetID);

                                    ListenableFuture<JsonObject> future
                                            = new MobileServiceJsonTable(ExerciseRecord.Entry.TABLE_NAME, mMobileServiceClient)
                                            .insert(formatter.getAsJsonObject(exerciseRecord));
                                    Futures.addCallback(future, new FutureCallback<JsonObject>() {
                                        @Override
                                        public void onSuccess(JsonObject jsonObject) {
                                            Log.d(TAG, "Successfully inserted ExerciseRecord");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.e(TAG, "Error inserting ExerciseRecord: " + t.getMessage());
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Throwable t) {
                                Log.e(TAG, "Error inserting ExerciseSet: " + t.getMessage());
                            }
                        });
                    }

                    MobileClientData m = new MobileClientData(
                            MobileClientData.OPERATION_CREATE_EXERCISE_PLAN,
                            MobileClientData.OPERATION_SUCCESS);

                    try {
                        if (mCallback != null)
                            mCallback.sendResults(m);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    reportOperationFailure(MobileClientData.OPERATION_CREATE_EXERCISE_PLAN, t.getMessage());
                }
            });
            return true;
        }

        @Override
        public boolean createSuggestedWorkout(final ExercisePlanRecordSuggested exercisePlanRecordSuggested) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            ListenableFuture<JsonObject> future
                    = new MobileServiceJsonTable(ExercisePlanRecordSuggested.Entry.TABLE_NAME, mMobileServiceClient)
                    .insert(formatter.getAsJsonObject(exercisePlanRecordSuggested));
            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject jsonObject) {

                    String exercisePlanRecordID = parser.parseString(jsonObject, ExercisePlanRecord.Entry.Cols.ID);

                    for (final ExerciseSet exerciseSet : exercisePlanRecordSuggested.getExerciseSetList()) {
                        exerciseSet.setExercisePlanRecordID(exercisePlanRecordID);

                        ListenableFuture<JsonObject> future
                                = new MobileServiceJsonTable(ExerciseSet.Entry.TABLE_NAME, mMobileServiceClient)
                                .insert(formatter.getAsJsonObject(exerciseSet));
                        Futures.addCallback(future, new FutureCallback<JsonObject>() {
                            @Override
                            public void onSuccess(JsonObject jsonObject) {

                                String exerciseSetID = parser.parseString(jsonObject, ExerciseSet.Entry.Cols.ID);

                                for (final ExerciseRecord exerciseRecord : exerciseSet.getExerciseRecordList()) {
                                    exerciseRecord.setExerciseSetID(exerciseSetID);

                                    ListenableFuture<JsonObject> future
                                            = new MobileServiceJsonTable(ExerciseRecord.Entry.TABLE_NAME, mMobileServiceClient)
                                            .insert(formatter.getAsJsonObject(exerciseRecord));
                                    Futures.addCallback(future, new FutureCallback<JsonObject>() {
                                        @Override
                                        public void onSuccess(JsonObject jsonObject) {
                                            Log.d(TAG, "Successfully inserted ExerciseRecord");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.e(TAG, "Error inserting ExerciseRecord: " + t.getMessage());
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Throwable t) {
                                Log.e(TAG, "Error inserting ExerciseSet: " + t.getMessage());
                            }
                        });
                    }

                    MobileClientData m = new MobileClientData(
                            MobileClientData.OPERATION_CREATE_SUGGESTED_EXERCISE_PLAN,
                            MobileClientData.OPERATION_SUCCESS);

                    try {
                        if (mCallback != null)
                            mCallback.sendResults(m);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    reportOperationFailure(MobileClientData.OPERATION_CREATE_SUGGESTED_EXERCISE_PLAN, t.getMessage());
                }
            });
            return true;
        }

        @Override
        public boolean getExercisePlanRecordList(String userID) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            ListenableFuture<JsonElement> future
                    = new MobileServiceJsonTable(ExercisePlanRecord.Entry.TABLE_NAME, mMobileServiceClient)
                    .where().field(ExercisePlanRecord.Entry.Cols.MEMBER_ID).eq(userID)
                    .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {

                    List<ExercisePlanRecord> exercisePlanRecordList
                            = parser.parseExercisePlanRecords(result);

                    GetExercisePlanRecordInfoTask.instance(mMobileServiceClient)
                            .setOnFinishedListener(new BaseTask.OnFinishedListener<ExercisePlanRecord>() {
                                @Override
                                public void onFinished(List<ExercisePlanRecord> resultList) {

                                    Collections.sort(resultList, new Comparator<ExercisePlanRecord>() {
                                        @Override
                                        public int compare(ExercisePlanRecord o1, ExercisePlanRecord o2) {
                                            return o1.getDatetime().before(o2.getDatetime())? 1 : -1;
                                        }
                                    });

                                    MobileClientData m = new MobileClientData(
                                            MobileClientData.OPERATION_GET_HISTORY,
                                            MobileClientData.OPERATION_SUCCESS);
                                    m.setExercisePlanRecordList(resultList);

                                    try {
                                        if (mCallback != null)
                                            mCallback.sendResults(m);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            exercisePlanRecordList.toArray(
                                    new ExercisePlanRecord[exercisePlanRecordList.size()]));

                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    reportOperationFailure(MobileClientData.OPERATION_GET_HISTORY, t.getMessage());
                }
            });

            return true;
        }

        @Override
        public boolean getExercisePlanRecordSuggestedList(String userID) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            ListenableFuture<JsonElement> future
                    = new MobileServiceJsonTable(ExercisePlanRecordSuggested.Entry.TABLE_NAME, mMobileServiceClient)
                    .where().field(ExercisePlanRecordSuggested.Entry.Cols.MEMBER_ID).eq(userID)
                    .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {

                    List<ExercisePlanRecordSuggested> exercisePlanRecordSuggestedList
                            = parser.parseExercisePlanRecordSuggesteds(result);

                    GetExercisePlanRecordSuggestedInfoTask.instance(mMobileServiceClient)
                            .setOnFinishedListener(new BaseTask.OnFinishedListener<ExercisePlanRecordSuggested>() {
                                @Override
                                public void onFinished(List<ExercisePlanRecordSuggested> resultList) {

                                    Collections.sort(resultList, new Comparator<ExercisePlanRecordSuggested>() {
                                        @Override
                                        public int compare(ExercisePlanRecordSuggested o1, ExercisePlanRecordSuggested o2) {
                                            return o1.getDatetime().before(o2.getDatetime())? 1 : -1;
                                        }
                                    });

                                    MobileClientData m = new MobileClientData(
                                            MobileClientData.OPERATION_GET_HISTORY_SUGGESTED,
                                            MobileClientData.OPERATION_SUCCESS);
                                    m.setExercisePlanRecordSuggestedList(resultList);

                                    try {
                                        if (mCallback != null)
                                            mCallback.sendResults(m);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            exercisePlanRecordSuggestedList.toArray(
                                    new ExercisePlanRecordSuggested[exercisePlanRecordSuggestedList.size()]));

                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    reportOperationFailure(MobileClientData.OPERATION_GET_HISTORY_SUGGESTED, t.getMessage());
                }
            });

            return true;
        }

        @Override
        public boolean dismissSuggestedPlan(final ExercisePlanRecordSuggested exercisePlanRecordSuggested,
                                            final boolean wasDone) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            JsonObject jsonObject = formatter.getAsJsonObject(exercisePlanRecordSuggested);
            jsonObject.addProperty(ExercisePlanRecordSuggested.Entry.Cols.ID, exercisePlanRecordSuggested.getID());

            ListenableFuture<Void> future
                    = new MobileServiceJsonTable(ExercisePlanRecordSuggested.Entry.TABLE_NAME, mMobileServiceClient)
                    .delete(jsonObject);
            Futures.addCallback(future, new FutureCallback<Void>() {
                @Override
                public void onSuccess(@Nullable Void result) {

                    if (wasDone)
                        createWorkout(exercisePlanRecordSuggested);

                    MobileClientData m = new MobileClientData(
                            MobileClientData.OPERATION_DISMISS_SUGGESTED_PLAN,
                            MobileClientData.OPERATION_SUCCESS);
                    m.setExercisePlanRecordSuggested(exercisePlanRecordSuggested);

                    try {
                        if (mCallback != null)
                            mCallback.sendResults(m);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                private void createWorkout(ExercisePlanRecordSuggested exercisePlanRecordSuggested) {
                    final ExercisePlanRecord planRecord = exercisePlanRecordSuggested.getAsExercisePlan();

                    ListenableFuture<JsonObject> future
                            = new MobileServiceJsonTable(ExercisePlanRecord.Entry.TABLE_NAME, mMobileServiceClient)
                            .insert(formatter.getAsJsonObject(planRecord));
                    Futures.addCallback(future, new FutureCallback<JsonObject>() {
                        @Override
                        public void onSuccess(JsonObject jsonObject) {

                            String exercisePlanRecordID = parser.parseString(jsonObject, ExercisePlanRecord.Entry.Cols.ID);

                            for (final ExerciseSet exerciseSet : planRecord.getExerciseSetList()) {
                                exerciseSet.setExercisePlanRecordID(exercisePlanRecordID);

                                ListenableFuture<JsonObject> future
                                        = new MobileServiceJsonTable(ExerciseSet.Entry.TABLE_NAME, mMobileServiceClient)
                                        .insert(formatter.getAsJsonObject(exerciseSet));
                                Futures.addCallback(future, new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onSuccess(JsonObject jsonObject) {

                                        String exerciseSetID = parser.parseString(jsonObject, ExerciseSet.Entry.Cols.ID);

                                        for (final ExerciseRecord exerciseRecord : exerciseSet.getExerciseRecordList()) {
                                            exerciseRecord.setExerciseSetID(exerciseSetID);

                                            ListenableFuture<JsonObject> future
                                                    = new MobileServiceJsonTable(ExerciseRecord.Entry.TABLE_NAME, mMobileServiceClient)
                                                    .insert(formatter.getAsJsonObject(exerciseRecord));
                                            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                                                @Override
                                                public void onSuccess(JsonObject jsonObject) {
                                                    Log.d(TAG, "Successfully inserted ExerciseRecord");
                                                }

                                                @Override
                                                public void onFailure(@NonNull Throwable t) {
                                                    Log.e(TAG, "Error inserting ExerciseRecord: " + t.getMessage());
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Throwable t) {
                                        Log.e(TAG, "Error inserting ExerciseSet: " + t.getMessage());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Throwable t) {
                            Log.e(TAG, "Error inserting ExercisePlanRecord: " + t.getMessage());
                        }
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    reportOperationFailure(MobileClientData.OPERATION_DISMISS_SUGGESTED_PLAN, t.getMessage());
                }
            });

            return true;
        }

        private void getGymMembersExceptMine(List<String> allMemberIDList, String userID) {
            new GetGymMembersExceptMine(allMemberIDList, userID, mMobileServiceClient)
                    .setOnFinishedListener(new GetGymMembersExceptMine.OnFinishedListener() {
                        @Override
                        public void onFinished(List<String> allMemberExceptMineList) {

                            getUsers(allMemberExceptMineList,
                                    User.Credential.MEMBER,
                                    MobileClientData.OPERATION_GET_MEMBERS_EXCEPT_MINE);
                        }
                    })
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                            mMobileServiceUser = new MobileServiceUser(user.getUserID());
                            mMobileServiceUser.setAuthenticationToken(user.getToken());
                            mMobileServiceClient.setCurrentUser(mMobileServiceUser);
                        }
                    })
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user);
        }

        @Override
        public void registerCallback(IMobileClientServiceCallback cb) throws RemoteException {
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

    private static class GetGymMembersExceptMine extends AsyncTask<Void, Void, Void> {

        private static final String TAG = GetGymMembersExceptMine.class.getSimpleName();

        private final List<String> mAllMemberIDList;
        private final String mUserID;
        private final MobileServiceClient mMobileServiceClient;

        private OnFinishedListener mListener;

        private MobileClientDataJsonParser parser = new MobileClientDataJsonParser();

        GetGymMembersExceptMine(List<String> allMemberIDList, String userID, MobileServiceClient mobileServiceClient) {

            mAllMemberIDList = allMemberIDList;
            mUserID = userID;
            mMobileServiceClient = mobileServiceClient;
        }

        GetGymMembersExceptMine setOnFinishedListener(OnFinishedListener listener) {
            mListener = listener;
            return this;
        }

        @Override
        protected Void doInBackground(Void... aVoids) {

            List<String> allMemberExceptMineList = new ArrayList<>();
            allMemberExceptMineList.addAll(mAllMemberIDList);

            try {
                JsonElement result =
                        new MobileServiceJsonTable(StaffMember.Entry.TABLE_NAME, mMobileServiceClient)
                                .where().field(StaffMember.Entry.Cols.STAFF_ID).eq(mUserID)
                                .execute().get();

                for (JsonElement e : result.getAsJsonArray())  {
                    String memberID = parser.parseString(e.getAsJsonObject(), StaffMember.Entry.Cols.MEMBER_ID);

                    for(String id : mAllMemberIDList)
                        if (id.equals(memberID))
                            allMemberExceptMineList.remove(id);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Log.e(TAG, "Error: " + e.getMessage());
            }

            if (mListener != null)
                mListener.onFinished(allMemberExceptMineList);

            return null;
        }

        interface OnFinishedListener {
            void onFinished(List<String> allMemberExceptMineList);
        }
    }

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

            if (mListener != null)
                mListener.onFinished(userList);

            return null;
        }

        interface OnFinishedListener {
            void onFinished(List<User> userList);
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

    private static class GetExercisePlanRecordSuggestedInfoTask extends BaseTask<ExercisePlanRecordSuggested> {

        public static GetExercisePlanRecordSuggestedInfoTask instance(MobileServiceClient mobileServiceClient) {
            return new GetExercisePlanRecordSuggestedInfoTask(mobileServiceClient);
        }

        GetExercisePlanRecordSuggestedInfoTask(MobileServiceClient mobileServiceClient) {
            super(mobileServiceClient);
        }

        @Override
        protected void doProcessing(final ExercisePlanRecordSuggested[] records) {

            final List<String> ids = new ArrayList<>();
            for (ExercisePlanRecordSuggested record : records) {
                if (!ids.contains(record.getMemberID()))
                    ids.add(record.getMemberID());
                if (!ids.contains(record.getStaffID()))
                    ids.add(record.getStaffID());
            }

            final List<User> users = new ArrayList<>();
            for (String id : ids)
                users.add(new User(id));


            GetUserInfoTask.instance(mMobileServiceClient)
                    .setOnFinishedListener(new OnFinishedListener<User>() {
                        @Override
                        public void onFinished(List<User> resultList) {

                            for (ExercisePlanRecordSuggested record : records) {
                                for (User user : resultList) {
                                    if (user.getID().equals(record.getMemberID()))
                                        record.setMember(user);
                                    if (user.getID().equals(record.getStaffID()))
                                        record.setStaff(user);
                                }
                            }
                            getExercisePlanRecordSuggestedInfo(records);
                        }
                    }).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    users.toArray(new User[users.size()]));
        }

        private void getExercisePlanRecordSuggestedInfo(final ExercisePlanRecordSuggested[] records) {
            final List<String> ids = new ArrayList<>();

            for (ExercisePlanRecordSuggested record : records)
                if (!ids.contains(record.getID()))
                    ids.add(record.getID());

            ListenableFuture<JsonElement> future =
                    MobileServiceJsonTableBuilder.instance(ExerciseSet.Entry.TABLE_NAME, mMobileServiceClient)
                            .where(ExerciseSet.Entry.Cols.EXERCISE_PLAN_RECORD_ID, ids.toArray(new String[ids.size()]))
                            .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {

                    List<ExerciseSet> exerciseSetList
                            = parser.parseExerciseSets(result);

                    GetExerciseSetInfoTask.instance(mMobileServiceClient)
                            .setOnFinishedListener(new OnFinishedListener<ExerciseSet>() {
                                @Override
                                public void onFinished(List<ExerciseSet> resultList) {

                                    for (ExercisePlanRecordSuggested record : records) {
                                        for (ExerciseSet set : resultList) {
                                            if (set.getExercisePlanRecordID().equals(record.getID()))
                                                record.addExerciseSet(set);
                                        }
                                        onFetched(record);
                                    }
                                }
                            }).executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            exerciseSetList.toArray(
                                    new ExerciseSet[exerciseSetList.size()]));
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e(TAG, "Error Fetching ExercisePlanReports: " + t.getMessage());

                    for (ExercisePlanRecordSuggested record : records) {
                        onFetched(record);
                    }
                }
            });
        }
    }

    private static class GetExercisePlanRecordInfoTask extends BaseTask<ExercisePlanRecord> {

        public static GetExercisePlanRecordInfoTask instance(MobileServiceClient mobileServiceClient) {
            return new GetExercisePlanRecordInfoTask(mobileServiceClient);
        }

        GetExercisePlanRecordInfoTask(MobileServiceClient mobileServiceClient) {
            super(mobileServiceClient);
        }

        @Override
        protected void doProcessing(final ExercisePlanRecord[] records) {

            final List<String> ids = new ArrayList<>();
            for (ExercisePlanRecord record : records)
                if (!ids.contains(record.getMemberID()))
                    ids.add(record.getMemberID());

            final List<User> users = new ArrayList<>();
            for (String id : ids)
                users.add(new User(id));


            GetUserInfoTask.instance(mMobileServiceClient)
                    .setOnFinishedListener(new OnFinishedListener<User>() {
                        @Override
                        public void onFinished(List<User> resultList) {

                            for (ExercisePlanRecord record : records) {
                                for (User user : resultList) {
                                    if (user.getID().equals(record.getMemberID()))
                                        record.setMember(user);
                                }
                            }
                            getExercisePlanRecordInfo(records);
                        }
                    }).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    users.toArray(new User[users.size()]));
        }

        private void getExercisePlanRecordInfo(final ExercisePlanRecord[] records) {
            final List<String> ids = new ArrayList<>();

            for (ExercisePlanRecord record : records)
                if (!ids.contains(record.getID()))
                    ids.add(record.getID());

            ListenableFuture<JsonElement> future =
                    MobileServiceJsonTableBuilder.instance(ExerciseSet.Entry.TABLE_NAME, mMobileServiceClient)
                            .where(ExerciseSet.Entry.Cols.EXERCISE_PLAN_RECORD_ID, ids.toArray(new String[ids.size()]))
                            .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {

                    List<ExerciseSet> exerciseSetList
                            = parser.parseExerciseSets(result);

                    GetExerciseSetInfoTask.instance(mMobileServiceClient)
                            .setOnFinishedListener(new OnFinishedListener<ExerciseSet>() {
                                @Override
                                public void onFinished(List<ExerciseSet> resultList) {

                                    for (ExercisePlanRecord record : records) {
                                        for (ExerciseSet set : resultList) {
                                            if (set.getExercisePlanRecordID().equals(record.getID()))
                                                record.addExerciseSet(set);
                                        }
                                        onFetched(record);
                                    }
                                }
                            }).executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            exerciseSetList.toArray(
                                    new ExerciseSet[exerciseSetList.size()]));
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e(TAG, "Error Fetching ExercisePlanReports: " + t.getMessage());

                    for (ExercisePlanRecord record : records) {
                        onFetched(record);
                    }
                }
            });
        }
    }

    private static class GetExerciseSetInfoTask extends BaseTask<ExerciseSet> {

        public static GetExerciseSetInfoTask instance(MobileServiceClient mobileServiceClient) {
            return new GetExerciseSetInfoTask(mobileServiceClient);
        }

        GetExerciseSetInfoTask(MobileServiceClient mobileServiceClient) {
            super(mobileServiceClient);
        }

        @Override
        protected void doProcessing(final ExerciseSet[] sets) {

            final List<String> ids = new ArrayList<>();
            for (ExerciseSet set : sets)
                if (!ids.contains(set.getExerciseID()))
                    ids.add(set.getExerciseID());

            final List<Exercise> exercises = new ArrayList<>();
            for (String id : ids)
                exercises.add(new Exercise(id));


            GetExerciseInfoTask.instance(mMobileServiceClient)
                    .setOnFinishedListener(new OnFinishedListener<Exercise>() {
                        @Override
                        public void onFinished(List<Exercise> resultList) {

                            for (ExerciseSet set : sets) {
                                for (Exercise exercise : resultList) {
                                    if (set.getExerciseID().equals(exercise.getID()))
                                        set.setExercise(exercise);
                                }
                            }
                            getExerciseSetInfo(sets);
                        }
                    }).executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            exercises.toArray(new Exercise[exercises.size()]));
        }

        private void getExerciseSetInfo(final ExerciseSet[] sets) {

            final List<String> ids = new ArrayList<>();

            for (ExerciseSet set : sets)
                ids.add(set.getID());

            ListenableFuture<JsonElement> future =
                    MobileServiceJsonTableBuilder.instance(ExerciseRecord.Entry.TABLE_NAME, mMobileServiceClient)
                            .where(ExerciseRecord.Entry.Cols.EXERCISE_SET_ID, ids.toArray(new String[ids.size()]))
                            .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {

                    List<ExerciseRecord> exerciseRecords
                            = parser.parseExerciseRecords(result);

                    for (ExerciseSet set : sets) {
                        for (ExerciseRecord record : exerciseRecords) {
                            if (record.getExerciseSetID().equals(set.getID()))
                                set.addExerciseRecord(record);
                        }
                        onFetched(set);
                    }
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e(TAG, "Error Fetching ExerciseRecords: " + t.getMessage());

                    for (ExerciseSet set : sets) {
                        onFetched(set);
                    }
                }
            });
        }
    }

    private static class GetUserInfoTask extends BaseTask<User> {

        public static GetUserInfoTask instance(MobileServiceClient mobileServiceClient) {
            return new GetUserInfoTask(mobileServiceClient);
        }

        GetUserInfoTask(MobileServiceClient mobileServiceClient) {
            super(mobileServiceClient);
        }

        @Override
        protected void doProcessing(final User[] users) {

            final List<String> ids = new ArrayList<>();

            for (User user : users)
                ids.add(user.getID());

            ListenableFuture<JsonElement> future =
                    MobileServiceJsonTableBuilder.instance(User.Entry.TABLE_NAME, mMobileServiceClient)
                            .where(User.Entry.Cols.ID, ids.toArray(new String[ids.size()]))
                            .select(User.Entry.Cols.ID, User.Entry.Cols.USERNAME)
                            .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {

                    List<User> resultList = parser.parseUsers(result);

                    for (User user : users) {
                        for (User r : resultList) {
                            if (user.getID().equals(r.getID()))
                                user.setUsername(r.getUsername());
                        }
                        onFetched(user);
                    }
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e(TAG, "Error Fetching User: " + t.getMessage());

                    for (User user : users) {
                        onFetched(user);
                    }
                }
            });

        }
    }

    private static class GetExerciseInfoTask extends BaseTask<Exercise> {

        public static GetExerciseInfoTask instance(MobileServiceClient mobileServiceClient) {
            return new GetExerciseInfoTask(mobileServiceClient);
        }

        GetExerciseInfoTask(MobileServiceClient mobileServiceClient) {
            super(mobileServiceClient);
        }

        @Override
        protected void doProcessing(final Exercise[] exercises) {

            final List<String> ids = new ArrayList<>();

            for (Exercise exercise : exercises)
                ids.add(exercise.getID());

            ListenableFuture<JsonElement> future =
                    MobileServiceJsonTableBuilder.instance(Exercise.Entry.TABLE_NAME, mMobileServiceClient)
                            .where(Exercise.Entry.Cols.ID, ids.toArray(new String[ids.size()]))
                            .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {

                    List<Exercise> resultList = parser.parseExercises(result);

                    for (Exercise exercise : exercises) {
                        for (Exercise r : resultList) {
                            if (exercise.getID().equals(r.getID())) {
                                exercise.setName(r.getName());
                            }
                        }
                        onFetched(exercise);
                    }
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e(TAG, "Error Fetching Exercise: " + t.getMessage());

                    for (Exercise exercise : exercises) {
                        onFetched(exercise);
                    }
                }
            });

        }
    }

    private static abstract class BaseTask<T> extends AsyncTask<T, Void, Void> {

        protected final String TAG = getClass().getSimpleName();

        final MobileServiceClient mMobileServiceClient;

        private int numberOfItems = 0;

        private List<T> resultList = new ArrayList<>();

        protected OnFinishedListener<T> mListener;


        MobileClientDataJsonParser parser = new MobileClientDataJsonParser();

        BaseTask(MobileServiceClient mobileServiceClient) {
            mMobileServiceClient = mobileServiceClient;
        }

        BaseTask<T> setOnFinishedListener(OnFinishedListener<T> listener) {
            mListener = listener;
            return this;
        }

        @Override
        protected Void doInBackground(T[] ts) {
            numberOfItems = ts.length;
            if (numberOfItems == 0)
                finish();
            else
                doProcessing(ts);
            return null;
        }

        protected abstract void doProcessing(T[] ts);

        void onFetched(T obj) {
            resultList.add(obj);
            if (resultList.size() == numberOfItems)
                finish();
        }

        private void finish() {
            if (mListener != null)
                mListener.onFinished(resultList);
        }

        interface OnFinishedListener<T> {
            void onFinished(List<T> resultList);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(
                    intent.getAction(),
                    NetworkChangeBroadcastReceiver.ACTION_NETWORK_CHANGE_RECEIVER)) {
                boolean isNetworkConnected
                        = NetworkChangeBroadcastReceiver.extractNetworkConnectionState(intent);
                if (isNetworkConnected) {
                    initMobileServiceClient();
                } else {
                    destroyMobileServiceClient();
                }
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
