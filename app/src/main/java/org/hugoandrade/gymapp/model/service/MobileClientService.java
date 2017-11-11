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
import org.hugoandrade.gymapp.data.ExercisePlan;
import org.hugoandrade.gymapp.data.ExercisePlanSuggested;
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
import java.util.Arrays;
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

    static final int numberOfItemsInWhereClause = 10;

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
    /**
     *  Local-side IPC implementation stub class and constructs the stub
     *  and attaches it to the interface.
     *  */
    private final IMobileClientService.Stub mBinder = new IMobileClientService.Stub() {

        /**
         * Login Operation. Returns false if MobileServiceClient is null (no network
         * connection). Search in the table User in the Web Service for username-password
         * combination and calls back the operation result.
         */
        @Override // ******* OK *******//
        public boolean login(String username, final String password) throws RemoteException {
            // ******* OK *******//
            if (mMobileServiceClient == null)
                return false;

            // Search for username-password combination in User table through an insert operation
            ListenableFuture<JsonObject> future
                    = new MobileServiceJsonTable(User.Entry.TABLE_NAME, mMobileServiceClient)
                    .insert(formatter.getAsJsonObject(username, password));
            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject jsonObject) {
                    // Parse jsonObject to User. Set password so that it
                    // can be stored in the StorageProvider
                    User user = parser.parseUser(jsonObject);
                    user.setPassword(password);

                    // get credential of the user (Admin, Staff, Member)
                    getCredentialOfLoggedInUser(user);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    // Report operation was not successful and the message
                    reportOperationFailure(MobileClientData.OPERATION_LOGIN, t.getMessage());
                }
            });
            return true;
        }

        /**
         * SignUp Operation. Returns false if MobileServiceClient is null (no network
         * connection). Insert in the table User in the Web Service the username-password
         * combination and calls back the operation result.
         */
        @Override
        public boolean signUp(final String username, final String password) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            // Insert the username-password combination in the User table through an
            // insert operation with the SignUp parameters
            ArrayList<Pair<String, String>> parameters = new ArrayList<>();
            parameters.add(new Pair<>(User.Entry.REQUEST_TYPE, User.Entry.SIGN_UP));

            ListenableFuture<JsonObject> future =
                    new MobileServiceJsonTable(User.Entry.TABLE_NAME, mMobileServiceClient)
                            .insert(formatter.getAsJsonObject(username, password), parameters);
            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject jsonObject) {
                    // Callback the username-password combination used
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
                    // Report operation was not successful and the message
                    reportOperationFailure(MobileClientData.OPERATION_SIGN_UP, t.getMessage());
                }
            });
            return true;
        }

        /**
         * Get All Gym Users Operation. Returns false if MobileServiceClient is null (no network
         * connection). Query the table of the same name as the parameter 'credential'
         * in the Web Service for all users of that type.
         */
        @Override
        public boolean getAllGymUsers(final String credential) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            try {
                // Search for all users of the type 'credential' that are in the table with the
                // same name
                ListenableFuture<JsonElement> future
                        = new MobileServiceJsonTable(credential, mMobileServiceClient)
                        .execute();
                Futures.addCallback(future, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement jsonElement) {
                        // get all userIDs...
                        List<String> idList = parser.parseStrings(jsonElement, User.Credential.Cols.USER_ID);

                        // and return them as Users by fetching the table User for those ids
                        getUsers(idList, MobileClientData.OPERATION_GET_ALL_USER);
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        // Report operation was not successful and the message
                        reportOperationFailure(MobileClientData.OPERATION_GET_ALL_USER, t.getMessage());
                    }
                });
            } catch (MobileServiceException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        /**
         * Create Gym Users Operation. Returns false if MobileServiceClient is null (no network
         * connection). Insert the WaitingUser object into WaitingUser table in the Web Service
         * and, if successful, callback the resultingWaitingUser object which now has a code
         * generated in the WebService.
         */
        @Override
        public boolean createGymUser(WaitingUser waitingUser) throws RemoteException {
            // ******* OK *******//
            if (mMobileServiceClient == null)
                return false;

            // Insert the WaitingUser object into the WaitingUser table through an
            // insert operation.
            ListenableFuture<JsonObject> future
                    = new MobileServiceJsonTable(WaitingUser.Entry.TABLE_NAME, mMobileServiceClient)
                    .insert(formatter.getAsJsonObject(waitingUser));
            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject jsonObject) {
                    // Callback the resulting parsed JsonObject
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
                    // Report operation was not successful and the message
                    reportOperationFailure(MobileClientData.OPERATION_CREATE_USER, t.getMessage());
                }
            });
            return true;
        }

        /**
         * Validate Gym User Operation. Returns false if MobileServiceClient is null (no network
         * connection). Query the WaitingUser table in the Web Service to check if the
         * username-code combination exists and, if it in fact exists, callback the resulting
         * WaitingUser object.
         */
        @Override
        public boolean validateGymUser(final WaitingUser waitingUser) throws RemoteException {
            // ******* OK *******//
            if (mMobileServiceClient == null)
                return false;

            // Query the remote WaitingUser table to check if username-code combination exists
            ListenableFuture<JsonElement> future
                    = new MobileServiceJsonTable(WaitingUser.Entry.TABLE_NAME, mMobileServiceClient)
                    .where().field(WaitingUser.Entry.Cols.USERNAME).eq(waitingUser.getUsername())
                    .and().field(WaitingUser.Entry.Cols.CODE).eq(waitingUser.getCode())
                    .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement jsonElement) {

                    if (jsonElement.getAsJsonArray().size() == 0) {
                        // Report operation was not successful and the message
                        reportOperationFailure(
                                MobileClientData.OPERATION_VALIDATE,
                                "Username-Code combo does not exist");
                        return;
                    }

                    // ... otherwise, callback the resulting parsed JsonObject
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
                    // Report operation was not successful and the message
                    reportOperationFailure(MobileClientData.OPERATION_VALIDATE, t.getMessage());
                }
            });
            return true;
        }

        /**
         * Get My Gym Members Operation. Returns false if MobileServiceClient is null (no network
         * connection). Query the StaffMember table to get all the gym members whose ID is
         * associated with the gym staff whose ID is 'userID'.
         */
        @Override
        public boolean getMyGymMembers(String userID) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            // Get all gym members whose ID is associated with userID
            ListenableFuture<JsonElement> future
                    = new MobileServiceJsonTable(StaffMember.Entry.TABLE_NAME, mMobileServiceClient)
                    .where().field(StaffMember.Entry.Cols.STAFF_ID).eq(userID)
                    .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {
                    // Parse JsonElement (JsonArray) into list of StaffMembers,
                    // get the IDs of the gym members, get them as User objects,
                    // and callback the result
                    List<StaffMember> staffMemberList = parser.parseStaffMembers(result);
                    List<String> myMemberIDList = new ArrayList<>();

                    for (StaffMember staffMember : staffMemberList)
                        myMemberIDList.add(staffMember.getMemberID());

                    // get MemberID(s) as User objects and callback the result
                    getUsers(myMemberIDList, MobileClientData.OPERATION_GET_MY_MEMBERS);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    // Report operation was not successful and the message
                    reportOperationFailure(MobileClientData.OPERATION_GET_MY_MEMBERS, t.getMessage());
                }
            });

            return true;
        }

        /**
         * Get Gym Members Except Mine Operation. Returns false if MobileServiceClient is null
         * (no network connection). Query the Member table to get all the gym members, and then
         * exclude those which already are associated with the Gym Staff with id 'userID'.
         */
        @Override
        public boolean getGymMembersExceptMine(final String userID) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            try {
                // Query User table to get all gym members
                ListenableFuture<JsonElement> future
                        = new MobileServiceJsonTable(User.Credential.MEMBER, mMobileServiceClient)
                        .execute();
                Futures.addCallback(future, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {
                        // Get all their IDs by parsing the JsonElement (JsonArray) into list of
                        // Strings, and call 'getGymMembersExceptMine' function.
                        List<String> memberIDList = new ArrayList<>();

                        for (JsonElement item : result.getAsJsonArray())
                            memberIDList.add(parser.parseString(item.getAsJsonObject(), User.Credential.Cols.USER_ID));

                        getGymMembersExceptMine(memberIDList, userID);
                    }

                    @Override
                    public void onFailure(@NonNull Throwable t) {
                        // Report operation was not successful and the message
                        reportOperationFailure(MobileClientData.OPERATION_GET_MEMBERS_EXCEPT_MINE, t.getMessage());
                    }
                });

            } catch (MobileServiceException e) {
                // Report operation was not successful and the message
                reportOperationFailure(MobileClientData.OPERATION_GET_MEMBERS_EXCEPT_MINE, e.getMessage());
            }
            return true;
        }

        /**
         * Add Member To My Gym Members Operation. Returns false if MobileServiceClient is null
         * (no network connection). Insert the StaffMember object into the StaffMember table
         * so that from now on these two User(s) are associated.
         */
        @Override
        public boolean addMemberToMyMembers(final User member, String userID) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            // create StaffMember object and insert it into the Staff Member table
            StaffMember staffMember = new StaffMember(userID, member.getID());

            ListenableFuture<JsonObject> future
                    = new MobileServiceJsonTable(StaffMember.Entry.TABLE_NAME, mMobileServiceClient)
                    .insert(formatter.getAsJsonObject(staffMember));
            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject jsonObject) {
                    // Callback the original Gym Member so that it can be added to the list
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
                    // Report operation was not successful and the message
                    reportOperationFailure(MobileClientData.OPERATION_ADD_MEMBER_TO_MY_MEMBERS, t.getMessage());
                }
            });
            return true;
        }

        /**
         * Get My Gym Staff Operation. Returns false if MobileServiceClient is null (no network
         * connection). Query the StaffMember table to get all the gym staff whose ID is
         * associated with the gym member whose ID is 'userID'.
         */
        @Override
        public boolean getMyGymStaff(String userID) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            // Get all gym staff whose ID is associated with userID
            ListenableFuture<JsonElement> future
                    = new MobileServiceJsonTable(StaffMember.Entry.TABLE_NAME, mMobileServiceClient)
                    .where().field(StaffMember.Entry.Cols.MEMBER_ID).eq(userID)
                    .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {
                    // Parse JsonElement (JsonArray) into list of StaffMembers,
                    // get the IDs of the gym staff, get them as User objects,
                    // and callback the result
                    List<StaffMember> staffMemberList = parser.parseStaffMembers(result);
                    List<String> myStaffIDList = new ArrayList<>();

                    for (StaffMember staffMember : staffMemberList)
                        myStaffIDList.add(staffMember.getStaffID());

                    // get StaffID(s) as User objects and callback the result
                    getUsers(myStaffIDList, MobileClientData.OPERATION_GET_MY_STAFF);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    // Report operation was not successful and the message
                    reportOperationFailure(MobileClientData.OPERATION_GET_MY_STAFF, t.getMessage());
                }
            });

            return true;
        }

        /**
         * Get All Exercises Operation. Returns false if MobileServiceClient is null (no network
         * connection). Query the table Exercise in the Web Service in order to get
         * all exercises available.
         */
        @Override
        public boolean getAllExercises() throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            try {
                // Search for all exercises in the table Exercise
                ListenableFuture<JsonElement> future
                        = new MobileServiceJsonTable(Exercise.Entry.TABLE_NAME, mMobileServiceClient)
                        .execute();
                Futures.addCallback(future, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {
                        // Callback the parsed JsonElement(JsonArray) as a list of exercises
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
                        // Report operation was not successful and the message
                        reportOperationFailure(MobileClientData.OPERATION_GET_ALL_EXERCISES, t.getMessage());
                    }
                });
            } catch (MobileServiceException e) {
                // Report operation was not successful and the message
                reportOperationFailure(MobileClientData.OPERATION_GET_ALL_EXERCISES, e.getMessage());
            }

            return true;
        }

        /**
         * Create Exercise Operation. Returns false if MobileServiceClient is null (no network
         * connection). Insert into the table Exercise in the Web Service the Exercise object.
         */
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

                    // Callback the parsed JsonObject as an Exercise object
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
                    // Report operation was not successful and the message
                    reportOperationFailure(MobileClientData.OPERATION_CREATE_EXERCISE, t.getMessage());
                }
            });
            return true;
        }

        /**
         * Create Exercise Plan Operation. Returns false if MobileServiceClient is null (no network
         * connection). Insert into the table ExercisePlan in the Web Service
         * the ExercisePlan object. This requires to then insert the child ExerciseSet objects
         * and the child ExerciseRecord object of each ExerciseSet
         */
        @Override
        public boolean createWorkout(final ExercisePlan exercisePlan) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            // Insert ExercisePlan object
            ListenableFuture<JsonObject> future
                    = new MobileServiceJsonTable(ExercisePlan.Entry.TABLE_NAME, mMobileServiceClient)
                    .insert(formatter.getAsJsonObject(exercisePlan));
            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject jsonObject) {
                    // Extract resulting ID, and to each child ExerciseSet
                    // set the id of the parent ExercisePlan and
                    // insert them into the ExerciseSet table.
                    String exercisePlanID = parser.parseString(jsonObject, ExercisePlan.Entry.Cols.ID);

                    for (final ExerciseSet exerciseSet : exercisePlan.getExerciseSetList()) {
                        exerciseSet.setExercisePlanID(exercisePlanID);

                        ListenableFuture<JsonObject> future
                                = new MobileServiceJsonTable(ExerciseSet.Entry.TABLE_NAME, mMobileServiceClient)
                                .insert(formatter.getAsJsonObject(exerciseSet));
                        Futures.addCallback(future, new FutureCallback<JsonObject>() {
                            @Override
                            public void onSuccess(JsonObject jsonObject) {
                                // Extract resulting ID, and to each child ExerciseRecord
                                // set the id of the parent ExerciseSet and
                                // insert them into the ExerciseRecord table.
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

                    // Callback that the operation was successful
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
                    // Report operation was not successful and the message
                    reportOperationFailure(MobileClientData.OPERATION_CREATE_EXERCISE_PLAN, t.getMessage());
                }
            });
            return true;
        }

        /**
         * Create Suggested Exercise Plan Operation. Returns false if MobileServiceClient is null
         * (no network connection). Insert into the table ExercisePlanSuggested in the Web Service
         * the ExercisePlanSuggested object. This requires to then insert the child ExerciseSet objects
         * and the child ExerciseRecord object of each ExerciseSet
         */
        @Override
        public boolean createSuggestedWorkout(final ExercisePlanSuggested exercisePlanSuggested) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            // Insert ExercisePlanSuggested object
            ListenableFuture<JsonObject> future
                    = new MobileServiceJsonTable(ExercisePlanSuggested.Entry.TABLE_NAME, mMobileServiceClient)
                    .insert(formatter.getAsJsonObject(exercisePlanSuggested));
            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject jsonObject) {

                    // Extract resulting ID, and to each child ExerciseSet
                    // set the id of the parent ExercisePlanSuggested and
                    // insert them into the ExerciseSet table.
                    String exercisePlanID = parser.parseString(jsonObject, ExercisePlan.Entry.Cols.ID);

                    for (final ExerciseSet exerciseSet : exercisePlanSuggested.getExerciseSetList()) {
                        exerciseSet.setExercisePlanID(exercisePlanID);

                        ListenableFuture<JsonObject> future
                                = new MobileServiceJsonTable(ExerciseSet.Entry.TABLE_NAME, mMobileServiceClient)
                                .insert(formatter.getAsJsonObject(exerciseSet));
                        Futures.addCallback(future, new FutureCallback<JsonObject>() {
                            @Override
                            public void onSuccess(JsonObject jsonObject) {

                                // Extract resulting ID, and to each child ExerciseRecord
                                // set the id of the parent ExerciseSet and
                                // insert them into the ExerciseRecord table.
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

                    // Callback that the operation was successful
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
                    // Report operation was not successful and the message
                    reportOperationFailure(MobileClientData.OPERATION_CREATE_SUGGESTED_EXERCISE_PLAN, t.getMessage());
                }
            });
            return true;
        }

        /**
         * Get Exercise Plans Operation. Returns false if MobileServiceClient is null (no network
         * connection). Query the table ExercisePlan in the Web Service in order to get
         * all exercise plans associated with the Gym Member whose id is 'userID'.
         */
        @Override
        public boolean getExercisePlanList(String userID) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            // Get all exercise plan records whose ID is associated with the Member
            // whose id is userID
            ListenableFuture<JsonElement> future
                    = new MobileServiceJsonTable(ExercisePlan.Entry.TABLE_NAME, mMobileServiceClient)
                    .where().field(ExercisePlan.Entry.Cols.MEMBER_ID).eq(userID)
                    .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {
                    // Parse the resulting JsonElement into a list of ExercisePlans
                    List<ExercisePlan> exercisePlanList
                            = parser.parseExercisePlans(result);

                    // Get all the Exercise Plans info (the gym member username, and the complete
                    // info of its child list of Exercise Sets) by calling this AsyncTask
                    GetExercisePlanInfoTask.instance(mMobileServiceClient)
                            .setOnFinishedListener(new BaseTask.OnFinishedListener<ExercisePlan>() {
                                @Override
                                public void onFinished(List<ExercisePlan> resultList) {

                                    // Sort the list in order of their respective datetimes
                                    Collections.sort(resultList, new Comparator<ExercisePlan>() {
                                        @Override
                                        public int compare(ExercisePlan o1, ExercisePlan o2) {
                                            return o1.getDatetime().before(o2.getDatetime())? 1 : -1;
                                        }
                                    });

                                    // Callback the resulting list of Exercise Plan Records
                                    MobileClientData m = new MobileClientData(
                                            MobileClientData.OPERATION_GET_HISTORY,
                                            MobileClientData.OPERATION_SUCCESS);
                                    m.setExercisePlanList(resultList);

                                    try {
                                        if (mCallback != null)
                                            mCallback.sendResults(m);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            exercisePlanList.toArray(
                                    new ExercisePlan[exercisePlanList.size()]));

                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    // Report operation was not successful and the message
                    reportOperationFailure(MobileClientData.OPERATION_GET_HISTORY, t.getMessage());
                }
            });

            return true;
        }

        /**
         * Get Suggested Exercise Plans Operation. Returns false if MobileServiceClient is null
         * (no network connection). Query the table ExercisePlanSuggested in the Web Service
         * in order to get all suggested exercise plans associated with the Gym Member
         * whose id is 'userID'.
         */
        @Override
        public boolean getExercisePlanSuggestedList(String userID) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            // Get all suggested exercise plan records whose ID is associated with the Member
            // whose id is userID
            ListenableFuture<JsonElement> future
                    = new MobileServiceJsonTable(ExercisePlanSuggested.Entry.TABLE_NAME, mMobileServiceClient)
                    .where().field(ExercisePlanSuggested.Entry.Cols.MEMBER_ID).eq(userID)
                    .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {

                    // Parse the resulting JsonElement into a list of suggested ExercisePlans
                    List<ExercisePlanSuggested> exercisePlanSuggestedList
                            = parser.parseExercisePlanSuggesteds(result);

                    // Get all the suggested Exercise Plans info (the gym member username,
                    // and the complete info of its child list of Exercise Sets)
                    // by calling this AsyncTask
                    GetExercisePlanSuggestedInfoTask.instance(mMobileServiceClient)
                            .setOnFinishedListener(new BaseTask.OnFinishedListener<ExercisePlanSuggested>() {
                                @Override
                                public void onFinished(List<ExercisePlanSuggested> resultList) {

                                    // Sort the list in order of their respective datetimes
                                    Collections.sort(resultList, new Comparator<ExercisePlanSuggested>() {
                                        @Override
                                        public int compare(ExercisePlanSuggested o1, ExercisePlanSuggested o2) {
                                            return o1.getDatetime().before(o2.getDatetime())? 1 : -1;
                                        }
                                    });

                                    // Callback the resulting list of Exercise Plan Records
                                    MobileClientData m = new MobileClientData(
                                            MobileClientData.OPERATION_GET_HISTORY_SUGGESTED,
                                            MobileClientData.OPERATION_SUCCESS);
                                    m.setExercisePlanSuggestedList(resultList);

                                    try {
                                        if (mCallback != null)
                                            mCallback.sendResults(m);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            exercisePlanSuggestedList.toArray(
                                    new ExercisePlanSuggested[exercisePlanSuggestedList.size()]));

                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    // Report operation was not successful and the message
                    reportOperationFailure(MobileClientData.OPERATION_GET_HISTORY_SUGGESTED, t.getMessage());
                }
            });

            return true;
        }

        /**
         * Dismiss Suggested Exercise Plan Operation. Returns false if MobileServiceClient is null
         * (no network connection). Delete the suggested exercise plan from the table
         * ExercisePlanSuggested in the Web Service and, if the Member set the suggested plan
         * as done, insert a copy of the suggested plan into the ExercisePlan table.
         */
        @Override
        public boolean dismissSuggestedPlan(final ExercisePlanSuggested exercisePlanSuggested,
                                            final boolean wasDone) throws RemoteException {
            if (mMobileServiceClient == null)
                return false;

            // get the suggested exercise plan as a JsonObject and make sure the ID is added
            JsonObject jsonObject = formatter.getAsJsonObject(exercisePlanSuggested);
            jsonObject.addProperty(ExercisePlanSuggested.Entry.Cols.ID, exercisePlanSuggested.getID());

            // delete it from the ExercisePlanSuggested table
            ListenableFuture<Void> future
                    = new MobileServiceJsonTable(ExercisePlanSuggested.Entry.TABLE_NAME, mMobileServiceClient)
                    .delete(jsonObject);
            Futures.addCallback(future, new FutureCallback<Void>() {
                @Override
                public void onSuccess(@Nullable Void result) {
                    // if the Member set the suggested plan as done,
                    // insert a copy of the suggested plan into the
                    // ExercisePlan table
                    if (wasDone)
                        createWorkout(exercisePlanSuggested);

                    // Callback the original suggested exercise plan
                    MobileClientData m = new MobileClientData(
                            MobileClientData.OPERATION_DISMISS_SUGGESTED_PLAN,
                            MobileClientData.OPERATION_SUCCESS);
                    m.setExercisePlanSuggested(exercisePlanSuggested);

                    try {
                        if (mCallback != null)
                            mCallback.sendResults(m);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    // Report operation was not successful and the message
                    reportOperationFailure(MobileClientData.OPERATION_DISMISS_SUGGESTED_PLAN, t.getMessage());
                }
            });

            return true;
        }

        /**
         * Get the User object of all ids provided and call back the result
         */
        private void getUsers(List<String> idList, final int operationType) {
            // initialize the appropriate AsyncTask and callback the returned Users(s)
            List<User> userList = new ArrayList<>();
            for (String id : idList)
                userList.add(new User(id));

            GetUserInfoTask.instance(mMobileServiceClient)
                    .setOnFinishedListener(new BaseTask.OnFinishedListener<User>() {
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
                            userList.toArray(new User[userList.size()]));
        }

        /**
         * Exclude from the list of MemberIDs all those that are already associated with
         * the the Gym Staff with id 'userID'.
         */
        private void getGymMembersExceptMine(List<String> allMemberIDList, String userID) {
            // initialize the appropriate AsyncTask and get the returned IDs
            GetGymMembersExceptMine.instance(userID, mMobileServiceClient)
                    .setOnFinishedListener(new GetGymMembersExceptMine.OnFinishedListener() {
                        @Override
                        public void onFinished(List<String> allMemberExceptMineList) {

                            // get MemberID(s) as User objects and callback the result
                            getUsers(allMemberExceptMineList,
                                    MobileClientData.OPERATION_GET_MEMBERS_EXCEPT_MINE);
                        }
                    })
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            allMemberIDList.toArray(new String[allMemberIDList.size()]));
        }

        /**
         * Get the credential (Admin, Staff, Member) of the Logged-in User
         */
        private void getCredentialOfLoggedInUser(final User user) {
            GetCredentialTask.instance(mMobileServiceClient)
                    .setOnFinishedListener(new BaseTask.OnFinishedListener<User>() {
                        @Override
                        public void onFinished(List<User> resultList) {
                            User user = resultList.get(0);

                            MobileClientData m;
                            if (user.getCredential() == null) {

                                m = new MobileClientData(
                                        MobileClientData.OPERATION_LOGIN,
                                        MobileClientData.OPERATION_FAILURE);
                                m.setErrorMessage("Credential not found");
                            }
                            else {
                                mMobileServiceUser = new MobileServiceUser(user.getUserID());
                                mMobileServiceUser.setAuthenticationToken(user.getToken());
                                mMobileServiceClient.setCurrentUser(mMobileServiceUser);

                                m = new MobileClientData(
                                        MobileClientData.OPERATION_LOGIN,
                                        MobileClientData.OPERATION_SUCCESS);
                                m.setUser(user);
                            }
                            try {
                                if (mCallback != null)
                                    mCallback.sendResults(m);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user);
        }

        /**
         * Insert a copy of the suggested plan into the ExercisePlan table.
         * This method splits the items in buffers of 'numberOfItemsInWhereClause'
         * in order to avoid error of where clause being to long.
         */
        private void createWorkout(ExercisePlanSuggested exercisePlanSuggested) {
            // Get the ExercisePlanSuggested object as an ExercisePlan object
            final ExercisePlan plan = exercisePlanSuggested;

            // Insert ExercisePlan object
            ListenableFuture<JsonObject> future
                    = new MobileServiceJsonTable(ExercisePlan.Entry.TABLE_NAME, mMobileServiceClient)
                    .insert(formatter.getAsJsonObject(plan));
            Futures.addCallback(future, new FutureCallback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject jsonObject) {
                    // Extract resulting ID, and to each child ExerciseSet
                    // set the id of the parent ExercisePlan and update the
                    // the ExercisePlanID column into the ExerciseSet table.
                    final String exercisePlanID = parser.parseString(jsonObject, ExercisePlan.Entry.Cols.ID);

                    List<ExerciseSet> setList = plan.getExerciseSetList();
                    List<SplitPair> splitPairList = getSplitPairList(setList.size(), numberOfItemsInWhereClause);

                    for (SplitPair s : splitPairList) {
                        List<ExerciseSet> exerciseSetList = setList.subList(s.init, s.end);

                        String[] ids = MobileClientServiceHelper.getIDsOfExerciseSets(exerciseSetList);

                        ListenableFuture<JsonElement> future =
                                MobileServiceJsonTableBuilder.instance(ExerciseSet.Entry.TABLE_NAME, mMobileServiceClient)
                                        .where(ExerciseSet.Entry.Cols.ID, ids)
                                        .execute();
                        Futures.addCallback(future, new FutureCallback<JsonElement>() {
                            @Override
                            public void onSuccess(@Nullable JsonElement result) {
                                if (result == null) {
                                    Log.e(TAG, "Error getting ExerciseSets: none found");
                                    return;
                                }
                                for (JsonElement jsonElement : result.getAsJsonArray()) {
                                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                                    jsonObject.addProperty(
                                            ExerciseSet.Entry.Cols.EXERCISE_PLAN_RECORD_ID,
                                            exercisePlanID);

                                    ListenableFuture<JsonObject> future =
                                            new MobileServiceJsonTable(ExerciseSet.Entry.TABLE_NAME, mMobileServiceClient)
                                                    .update(jsonObject);
                                    Futures.addCallback(future, new FutureCallback<JsonObject>() {
                                        @Override
                                        public void onSuccess(JsonObject jsonObject) {
                                            Log.d(TAG, "Successfully updated ExerciseSet");
                                        }

                                        @Override
                                        public void onFailure(@NonNull Throwable t) {
                                            Log.e(TAG, "Error updating ExerciseSet: " + t.getMessage());
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Throwable t) {
                                Log.e(TAG, "Error getting ExerciseSets: " + t.getMessage());
                            }
                        });

                    }
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e(TAG, "Error inserting ExercisePlan: " + t.getMessage());
                }
            });
        }

        /**
         * Client registers IMobileClientServiceCallback to receive callback events
         */
        @Override
        public void registerCallback(IMobileClientServiceCallback cb) throws RemoteException {
            mCallback = cb;
        }

        /**
         * Client unregisters IMobileClientServiceCallback as no longer requests to
         * receive callback events
         */
        @Override
        public void unregisterCallback(IMobileClientServiceCallback cb) throws RemoteException {
            if (mCallback == cb)
                mCallback = null;
        }

        /**
         * Sends a callback error message with a failure operation result flag
         * and with the given operation type flag
         */
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

    /**
     * Async Task class which fetches the ids of all the members which are associated with gym staff
     * with 'userID', and excludes them from the original String list of all exsting members' ID(s).
     */
    private static class GetGymMembersExceptMine extends AsyncTask<String, Void, Void> {

        private static final String TAG = GetGymMembersExceptMine.class.getSimpleName();

        /**
         * The id of the gym staff
         */
        private final String mUserID;

        /**
         * The MobileServiceClient instance used to fetch the data
         */
        private final MobileServiceClient mMobileServiceClient;

        /**
         * Listener which is called when information of all items was fetched
         */
        private OnFinishedListener mListener;

        /**
         * Used to parse JsonObjects.
         */
        private MobileClientDataJsonParser parser = new MobileClientDataJsonParser();

        /**
         * Factory method
         */
        public static GetGymMembersExceptMine instance(String userID,
                                                       MobileServiceClient mobileServiceClient) {
            return new GetGymMembersExceptMine(userID, mobileServiceClient);
        }
        /**
         * Constructor
         */
        GetGymMembersExceptMine(String userID, MobileServiceClient mobileServiceClient) {

            mUserID = userID;
            mMobileServiceClient = mobileServiceClient;
        }

        /**
         * Set finish callback
         */
        GetGymMembersExceptMine setOnFinishedListener(OnFinishedListener listener) {
            mListener = listener;
            return this;
        }

        @Override
        protected Void doInBackground(String... allMembersIDs) {

            List<String> allMemberExceptMineList = new ArrayList<>();
            allMemberExceptMineList.addAll(Arrays.asList(allMembersIDs));

            try {
                // Get all id(s) of the Gym members which are associated with gym staff
                // of id 'userdID'
                JsonElement result =
                        new MobileServiceJsonTable(StaffMember.Entry.TABLE_NAME, mMobileServiceClient)
                                .where().field(StaffMember.Entry.Cols.STAFF_ID).eq(mUserID)
                                .execute().get();

                // Parse result to list of StaffMember
                List<StaffMember> staffMemberList = parser.parseStaffMembers(result);

                // For each StaffMember pairing, get the id of the Member and remove all equal
                // String objects from the original list
                for (StaffMember staffMember : staffMemberList)  {
                    String memberID = staffMember.getMemberID();

                    for(String id : allMembersIDs)
                        if (id.equals(memberID))
                            allMemberExceptMineList.remove(id);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Log.e(TAG, "Error: " + e.getMessage());
            }

            // return the resulting filtered list
            if (mListener != null)
                mListener.onFinished(allMemberExceptMineList);

            return null;
        }

        interface OnFinishedListener {
            void onFinished(List<String> allMemberExceptMineList);
        }
    }

    /**
     * Async Task class which fetches all the info of the list of ExercisePlanSuggested(s)
     * (the gym member username and credential, and the complete info of its child list of
     * Exercise Sets)  asynchronously.
     */
    private static class GetExercisePlanSuggestedInfoTask extends BaseTask<ExercisePlanSuggested> {

        public static GetExercisePlanSuggestedInfoTask instance(MobileServiceClient mobileServiceClient) {
            return new GetExercisePlanSuggestedInfoTask(mobileServiceClient);
        }

        GetExercisePlanSuggestedInfoTask(MobileServiceClient mobileServiceClient) {
            super(mobileServiceClient);
        }

        @Override
        protected void doProcessing(final ExercisePlanSuggested[] records) {

            // Extract the ids of the all User objects of all ExercisePlan(s)
            final List<String> ids = new ArrayList<>();
            for (ExercisePlanSuggested record : records) {
                if (!ids.contains(record.getMemberID()))
                    ids.add(record.getMemberID());
                if (!ids.contains(record.getStaffID()))
                    ids.add(record.getStaffID());
            }

            // init simple User objects (with id only)
            final List<User> users = new ArrayList<>();
            for (String id : ids)
                users.add(new User(id));

            // Get their full info (username + credential)
            GetUserInfoTask.instance(mMobileServiceClient)
                    .setOnFinishedListener(new OnFinishedListener<User>() {
                        @Override
                        public void onFinished(List<User> resultList) {

                            // Set the resulting User object into the ExercisePlan(s)
                            // accordingly
                            for (ExercisePlanSuggested record : records) {
                                for (User user : resultList) {
                                    if (user.getID().equals(record.getMemberID()))
                                        record.setMember(user);
                                    if (user.getID().equals(record.getStaffID()))
                                        record.setStaff(user);
                                }
                            }

                            // Get all the complete info of their child list of Exercise Sets
                            getExercisePlanSuggestedInfo(records);
                        }
                    }).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    users.toArray(new User[users.size()]));
        }

        /**
         * Get the complete info of the list of Exercise Sets of all ExercisePlanSuggested(s).
         * This method splits the items in buffers of 'numberOfItemsInWhereClause'
         * in order to avoid error of where clause being to long.
         */
        private void getExercisePlanSuggestedInfo(ExercisePlanSuggested[] plans) {

            List<ExercisePlanSuggested> planList = Arrays.asList(plans);
            List<SplitPair> splitPairList = getSplitPairList(planList.size(), numberOfItemsInWhereClause);

            for (SplitPair s : splitPairList) {

                final List<ExercisePlanSuggested> exercisePlanList = planList.subList(s.init, s.end);
                String[] ids = MobileClientServiceHelper.getIDsOfExercisePlans(exercisePlanList);

                // Get all ExerciseSet(s) which are associated with the extracted ids
                ListenableFuture<JsonElement> future =
                        MobileServiceJsonTableBuilder.instance(ExerciseSet.Entry.TABLE_NAME, mMobileServiceClient)
                                .where(ExerciseSet.Entry.Cols.EXERCISE_PLAN_RECORD_ID, ids)
                                .execute();
                Futures.addCallback(future, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {

                        // Get list of ExerciseSet(s) by parsing the resulting JsonElement
                        List<ExerciseSet> exerciseSetList
                                = parser.parseExerciseSets(result);

                        // Get all the Exercise Sets info (the info of its child list of
                        // ExerciseRecords) by calling this AsyncTask
                        GetExerciseSetInfoTask.instance(mMobileServiceClient)
                                .setOnFinishedListener(new OnFinishedListener<ExerciseSet>() {
                                    @Override
                                    public void onFinished(List<ExerciseSet> resultList) {

                                        // Set the resulting list of ExerciseSet(s) into
                                        // the ExercisePlan(s) accordingly
                                        for (ExercisePlanSuggested plan : exercisePlanList) {
                                            for (ExerciseSet set : resultList) {
                                                if (set.getExercisePlanID().equals(plan.getID()))
                                                    plan.addExerciseSet(set);
                                            }

                                            onFetched(plan);
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

                        // Error fetching. Callback as they are
                        for (ExercisePlanSuggested plan : exercisePlanList) {
                            onFetched(plan);
                        }
                    }
                });
            }
        }
    }

    /**
     * Async Task class which fetches all the info of the list of ExercisePlans (the gym
     * member username and credential, and the complete info of its child list of Exercise Sets)
     * asynchronously.
     */
    private static class GetExercisePlanInfoTask extends BaseTask<ExercisePlan> {

        public static GetExercisePlanInfoTask instance(MobileServiceClient mobileServiceClient) {
            return new GetExercisePlanInfoTask(mobileServiceClient);
        }

        GetExercisePlanInfoTask(MobileServiceClient mobileServiceClient) {
            super(mobileServiceClient);
        }

        @Override
        protected void doProcessing(final ExercisePlan[] records) {

            // Extract the ids of the all User objects of all ExercisePlan(s)
            final List<String> ids = new ArrayList<>();
            for (ExercisePlan record : records)
                if (!ids.contains(record.getMemberID()))
                    ids.add(record.getMemberID());

            // init simple User objects (with id only)
            final List<User> users = new ArrayList<>();
            for (String id : ids)
                users.add(new User(id));

            // Get their full info (username + credential)
            GetUserInfoTask.instance(mMobileServiceClient)
                    .setOnFinishedListener(new OnFinishedListener<User>() {
                        @Override
                        public void onFinished(List<User> resultList) {
                            // Set the resulting User object into the ExercisePlan(s)
                            // accordingly
                            for (ExercisePlan record : records) {
                                for (User user : resultList) {
                                    if (user.getID().equals(record.getMemberID()))
                                        record.setMember(user);
                                }
                            }

                            // Get all the complete info of their child list of Exercise Sets
                            getExercisePlanInfo(records);
                        }
                    }).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    users.toArray(new User[users.size()]));
        }

        /**
         * Get the complete info of the list of Exercise Sets of all ExercisePlan(s).
         * This method splits the items in buffers of 'numberOfItemsInWhereClause'
         * in order to avoid error of where clause being to long.
         */
        private void getExercisePlanInfo(ExercisePlan[] plans) {

            List<ExercisePlan> planList = Arrays.asList(plans);
            List<SplitPair> splitPairList = getSplitPairList(planList.size(), numberOfItemsInWhereClause);

            for (SplitPair s : splitPairList) {

                final List<ExercisePlan> exercisePlanList = planList.subList(s.init, s.end);
                String[] ids = MobileClientServiceHelper.getIDsOfExercisePlans(exercisePlanList);

                // Get all ExerciseSet(s) which are associated with the extracted ids
                ListenableFuture<JsonElement> future =
                        MobileServiceJsonTableBuilder.instance(ExerciseSet.Entry.TABLE_NAME, mMobileServiceClient)
                                .where(ExerciseSet.Entry.Cols.EXERCISE_PLAN_RECORD_ID, ids)
                                .execute();
                Futures.addCallback(future, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {
                        // Get list of ExerciseSet(s) by parsing the resulting JsonElement
                        List<ExerciseSet> exerciseSetList
                                = parser.parseExerciseSets(result);

                        // Get all the Exercise Sets info (the info of its child list of
                        // ExerciseRecords) by calling this AsyncTask
                        GetExerciseSetInfoTask.instance(mMobileServiceClient)
                                .setOnFinishedListener(new OnFinishedListener<ExerciseSet>() {
                                    @Override
                                    public void onFinished(List<ExerciseSet> resultList) {

                                        // Set the resulting list of ExerciseSet(s) into
                                        // the ExercisePlan(s) accordingly
                                        for (ExercisePlan plan : exercisePlanList) {
                                            for (ExerciseSet set : resultList) {
                                                if (set.getExercisePlanID().equals(plan.getID()))
                                                    plan.addExerciseSet(set);
                                            }
                                            onFetched(plan);
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
                        // Error fetching. Callback as they are
                        for (ExercisePlan plan : exercisePlanList) {
                            onFetched(plan);
                        }
                    }
                });
            }
        }
    }

    /**
     * Async Task class which fetches all the info of the list of ExerciseSet (the complete
     * info of its child list of Exercise Records + Exercise(s)) asynchronously.
     */
    private static class GetExerciseSetInfoTask extends BaseTask<ExerciseSet> {

        public static GetExerciseSetInfoTask instance(MobileServiceClient mobileServiceClient) {
            return new GetExerciseSetInfoTask(mobileServiceClient);
        }

        GetExerciseSetInfoTask(MobileServiceClient mobileServiceClient) {
            super(mobileServiceClient);
        }

        @Override
        protected void doProcessing(final ExerciseSet[] sets) {

            // Extract the ids of the Exercise objects of all ExerciseSet(s)
            final List<String> ids = new ArrayList<>();
            for (ExerciseSet set : sets)
                if (!ids.contains(set.getExerciseID()))
                    ids.add(set.getExerciseID());

            // init simple Exercise objects (with id only)
            final List<Exercise> exercises = new ArrayList<>();
            for (String id : ids)
                exercises.add(new Exercise(id));

            // Get their full info (name)
            GetExerciseInfoTask.instance(mMobileServiceClient)
                    .setOnFinishedListener(new OnFinishedListener<Exercise>() {
                        @Override
                        public void onFinished(List<Exercise> resultList) {

                            // Set the resulting list of Exercise(s) into
                            // the ExerciseSets(s) accordingly
                            for (ExerciseSet set : sets) {
                                for (Exercise exercise : resultList) {
                                    if (set.getExerciseID().equals(exercise.getID()))
                                        set.setExercise(exercise);
                                }
                            }

                            // Get all the complete info of their child list of Exercise Records
                            getExerciseSetInfo(sets);
                        }
                    }).executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            exercises.toArray(new Exercise[exercises.size()]));
        }

        /**
         * Get all the complete info of their child list of Exercise Records.
         * This method splits the number in buffers of 'numberOfItemsInWhereClause'
         * in order to avoid error of where clause being to long.
         */
        private void getExerciseSetInfo(ExerciseSet[] sets) {

            // Extract the ids of all ExerciseSet(s)
            List<ExerciseSet> setList = Arrays.asList(sets);
            List<SplitPair> splitPairList = getSplitPairList(setList.size(), numberOfItemsInWhereClause);

            for (SplitPair s : splitPairList) {
                final List<ExerciseSet> exerciseSetList = setList.subList(s.init, s.end);
                String[] ids = MobileClientServiceHelper.getIDsOfExerciseSets(exerciseSetList);

                // Get all ExerciseRecords(s) which are associated with the extracted ids
                ListenableFuture<JsonElement> future =
                        MobileServiceJsonTableBuilder.instance(ExerciseRecord.Entry.TABLE_NAME, mMobileServiceClient)
                                .where(ExerciseRecord.Entry.Cols.EXERCISE_SET_ID, ids)
                                .execute();
                Futures.addCallback(future, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {
                        // Get list of ExerciseRecord(s) by parsing the resulting JsonElement
                        List<ExerciseRecord> exerciseRecords
                                = parser.parseExerciseRecords(result);

                        // Set the resulting list of ExerciseRecord(s) into
                        // the ExerciseSet(s) accordingly
                        for (ExerciseSet set : exerciseSetList) {
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

                        // Error fetching. Callback as they are
                        for (ExerciseSet set : exerciseSetList) {
                            onFetched(set);
                        }
                    }
                });
            }
        }
    }

    /**
     * Async Task class which fetches all the info of the list of User(s) (the username
     * and credential, and the complete info of its child list of Exercise Sets)
     * asynchronously.
     */
    private static class GetUserInfoTask extends BaseTask<User> {

        public static GetUserInfoTask instance(MobileServiceClient mobileServiceClient) {
            return new GetUserInfoTask(mobileServiceClient);
        }

        GetUserInfoTask(MobileServiceClient mobileServiceClient) {
            super(mobileServiceClient);
        }

        @Override
        protected void doProcessing(final User[] users) {

            GetCredentialTask.instance(mMobileServiceClient)
                    .setOnFinishedListener(new OnFinishedListener<User>() {
                        @Override
                        public void onFinished(List<User> userList) {
                            getUserInfo(userList);
                        }

                    }).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR, users);


        }

        /**
         * This method splits the items in buffers of 'numberOfItemsInWhereClause'
         * in order to avoid error of where clause being to long.
         */
        private void getUserInfo(List<User> userList) {

            // Extract the ids of the all User(s)
            List<SplitPair> splitPairList = getSplitPairList(userList.size(), numberOfItemsInWhereClause);

            for (SplitPair s : splitPairList) {
                final List<User> users = userList.subList(s.init, s.end);
                String[] ids = MobileClientServiceHelper.getIDsOfUsers(users);


                // Get all User(s), particularly the username, which are associated with the extracted ids
                ListenableFuture<JsonElement> future =
                        MobileServiceJsonTableBuilder.instance(User.Entry.TABLE_NAME, mMobileServiceClient)
                                .where(User.Entry.Cols.ID, ids)
                                .select(User.Entry.Cols.ID, User.Entry.Cols.USERNAME)
                                .execute();
                Futures.addCallback(future, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {
                        // Parse resulting JsonElement into list of User(s)
                        List<User> resultList = parser.parseUsers(result);

                        // Set the resulting usernames into the original list of User(s) accordingly
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

                        // Error fetching. Callback as they are
                        for (User user : users) {
                            onFetched(user);
                        }
                    }
                });

            }

        }
    }

    /**
     * Async Task class which fetches the Credential (Admin, Gym Member, or Gym Staff)
     * of the list of User(s) asynchronously.
     */
    private static class GetCredentialTask extends BaseTask<User> {


        public static GetCredentialTask instance(MobileServiceClient mobileServiceClient) {
            return new GetCredentialTask(mobileServiceClient);
        }

        GetCredentialTask(MobileServiceClient mobileServiceClient) {
            super(mobileServiceClient);
        }


        @Override
        protected void doProcessing(User[] users) {

            // This method splits the items in buffers of 'numberOfItemsInWhereClause'
            // in order to avoid error of where clause being to long.
            List<User> userList = Arrays.asList(users);
            List<SplitPair> splitPairList = getSplitPairList(userList.size(), numberOfItemsInWhereClause);

            for (SplitPair s : splitPairList) {
                getAdminCredential(userList.subList(s.init, s.end));
            }

        }

        private void getAdminCredential(List<User> users) {

            // Extract the ids of the all User(s) that do not have a credential
            final List<User> userList = MobileClientServiceHelper.getUsersWithoutCredential(users);
            String[] ids = MobileClientServiceHelper.getIDsOfUsers(userList);

            ListenableFuture<JsonElement> future =
                    MobileServiceJsonTableBuilder.instance(User.Credential.ADMIN, mMobileServiceClient)
                            .where(User.Credential.Cols.USER_ID, ids)
                            .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {
                    // Parse resulting JsonElement into list of String(s), representing the users' ids
                    List<String> resultList = parser.parseStrings(result, User.Credential.Cols.USER_ID);

                    // Set the admin credential the original list of User(s) accordingly
                    boolean doAllUsersHaveCredential = true;
                    for (User user : userList) {
                        for (String r : resultList) {
                            if (user.getID().equals(r))
                                user.setCredential(User.Credential.ADMIN);
                        }
                        if (user.getCredential() != null)
                            onFetched(user);
                        else
                            doAllUsersHaveCredential = false;

                        if (doAllUsersHaveCredential)
                            return;
                    }
                    getStaffCredential(userList);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e(TAG, "Error Fetching Credential: " + t.getMessage());

                    getStaffCredential(userList);
                }
            });
        }

        private void getStaffCredential(List<User>users) {

            // Extract the ids of the all User(s) that do not have a credential
            final List<User> userList = MobileClientServiceHelper.getUsersWithoutCredential(users);
            String[] ids = MobileClientServiceHelper.getIDsOfUsers(userList);

            ListenableFuture<JsonElement> future =
                    MobileServiceJsonTableBuilder.instance(User.Credential.STAFF, mMobileServiceClient)
                            .where(User.Credential.Cols.USER_ID, ids)
                            .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {
                    // Parse resulting JsonElement into list of String(s), representing the users' ids
                    List<String> resultList = parser.parseStrings(result, User.Credential.Cols.USER_ID);

                    // Set the admin credential the original list of User(s) accordingly
                    boolean doAllUsersHaveCredential = true;
                    for (User user : userList) {
                        for (String r : resultList) {
                            if (user.getID().equals(r))
                                user.setCredential(User.Credential.STAFF);
                        }

                        if (user.getCredential() != null)
                            onFetched(user);
                        else
                            doAllUsersHaveCredential = false;

                        if (doAllUsersHaveCredential)
                            return;
                    }
                    getMemberCredential(userList);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e(TAG, "Error Fetching User: " + t.getMessage());

                    getMemberCredential(userList);
                }
            });
        }

        private void getMemberCredential(List<User> users) {

            // Extract the ids of the all User(s) that do not have a credential
            final List<User> userList = MobileClientServiceHelper.getUsersWithoutCredential(users);
            String[] ids = MobileClientServiceHelper.getIDsOfUsers(userList);

            ListenableFuture<JsonElement> future =
                    MobileServiceJsonTableBuilder.instance(User.Credential.MEMBER, mMobileServiceClient)
                            .where(User.Credential.Cols.USER_ID, ids)
                            .execute();
            Futures.addCallback(future, new FutureCallback<JsonElement>() {
                @Override
                public void onSuccess(JsonElement result) {
                    // Parse resulting JsonElement into list of String(s), representing the users' ids
                    List<String> resultList = parser.parseStrings(result, User.Credential.Cols.USER_ID);

                    // Set the admin credential the original list of User(s) accordingly
                    boolean doAllUsersHaveCredential = true;
                    for (User user : userList) {
                        for (String r : resultList) {
                            if (user.getID().equals(r))
                                user.setCredential(User.Credential.MEMBER);
                        }

                        if (user.getCredential() != null)
                            onFetched(user);
                        else
                            doAllUsersHaveCredential = false;

                        if (doAllUsersHaveCredential)
                            return;
                    }
                    getDefaultCredential(userList);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e(TAG, "Error Fetching User: " + t.getMessage());

                    getDefaultCredential(userList);
                }
            });
        }

        private void getDefaultCredential(final List<User> users) {

            for (User user : users)
                if (user.getCredential() == null)
                    onFetched(user);
        }
    }

    /**
     * Async Task class which fetches all the info of the list of Exercise (the name)
     * asynchronously.
     */
    private static class GetExerciseInfoTask extends BaseTask<Exercise> {

        public static GetExerciseInfoTask instance(MobileServiceClient mobileServiceClient) {
            return new GetExerciseInfoTask(mobileServiceClient);
        }

        GetExerciseInfoTask(MobileServiceClient mobileServiceClient) {
            super(mobileServiceClient);
        }

        @Override
        protected void doProcessing(Exercise[] exercises) {

            // Extract the ids of the all Exercise(s)
            List<Exercise> eList = Arrays.asList(exercises);

            // This method splits the items in buffers of 'numberOfItemsInWhereClause'
            // in order to avoid error of where clause being to long.
            List<SplitPair> splitPairList = getSplitPairList(eList.size(), numberOfItemsInWhereClause);

            for (SplitPair s : splitPairList) {
                final List<Exercise> exerciseList = eList.subList(s.init, s.end);
                String[] ids = MobileClientServiceHelper.getIDsOfExercises(exerciseList);

                // Get all Exercise(s) which are associated with the extracted ids
                ListenableFuture<JsonElement> future =
                        MobileServiceJsonTableBuilder.instance(Exercise.Entry.TABLE_NAME, mMobileServiceClient)
                                .where(Exercise.Entry.Cols.ID, ids)
                                .execute();
                Futures.addCallback(future, new FutureCallback<JsonElement>() {
                    @Override
                    public void onSuccess(JsonElement result) {

                        // Get list of Exercise(s) by parsing the resulting JsonElement
                        List<Exercise> resultList = parser.parseExercises(result);

                        // Set the resulting Exercise(s) into the original list of
                        // Exercise(s) accordingly
                        for (Exercise exercise : exerciseList) {
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
                        // Error fetching. Callback as they are
                        for (Exercise exercise : exerciseList) {
                            onFetched(exercise);
                        }
                    }
                });
            }
        }
    }

    /**
     * Abstract AsyncTask class used through this instance to fetch data from the Web Service.
     * Particularly useful in providing a structured way of fetching the data of a list of
     * objects of type 'T' and sending a callback of a list (with the same size) of objects
     * of type 'T' with the fetched information.
     * a
     */
    private static abstract class BaseTask<T> extends AsyncTask<T, Void, Void> {

        /**
         * Used for logging
         */
        protected final String TAG = getClass().getSimpleName();

        /**
         * The MobileServiceClient instance used to fetch the data
         */
        final MobileServiceClient mMobileServiceClient;

        /**
         * The number of items retrieved of the original collection
         */
        private int numberOfItems = 0;

        /**
         * The list that stores the processed objects.
         */
        private List<T> resultList = new ArrayList<>();

        /**
         * Listener which is called when information of all items was fetched
         */
        OnFinishedListener<T> mListener;

        /**
         * Used to parse JsonObjects.
         */
        MobileClientDataJsonParser parser = new MobileClientDataJsonParser();

        BaseTask(MobileServiceClient mobileServiceClient) {
            mMobileServiceClient = mobileServiceClient;
        }

        /**
         * Set OnFinishedListener listener to receive the callback
         */
        BaseTask<T> setOnFinishedListener(OnFinishedListener<T> listener) {
            mListener = listener;
            return this;
        }

        @Override
        protected Void doInBackground(T[] ts) {
            // set number of items of original collection
            numberOfItems = ts.length;

            if (numberOfItems == 0)
                // nothing to process
                finish();
            else
                // do processing of the original list
                doProcessing(ts);
            return null;
        }

        /**
         * Abstract method that subclasses need to implement. Where the fetching is done.
         */
        protected abstract void doProcessing(T[] ts);

        /**
         * The object 'obj' is set. Add it to the result list and check if all original
         * objects were set
         */
        void onFetched(T obj) {
            resultList.add(obj);
            // if all objects are set, call callback
            if (resultList.size() == numberOfItems)
                finish();
        }

        /**
         * All objects are set. Call callback
         */
        private void finish() {
            if (mListener != null)
                mListener.onFinished(resultList);
        }

        /**
         * Interface used to return a list of objects of type 'T' once all are set.
         */
        interface OnFinishedListener<T> {
            void onFinished(List<T> resultList);
        }
    }

    /**
     * Class used in parallel with the 'getSplitPairList' static method in order to
     * split the the data into buffers of certain size in order to avoid the enetwork error of
     * the query having a too long where clause
     */
    private static class SplitPair {
        final int init;
        final int end;

        SplitPair(int init, int end) {
            this.init = init;
            this.end = end;
        }

        @Override
        public String toString() {
            return "SplitPair{" +
                    "init=" + init +
                    ", end=" + end +
                    '}';
        }
    }

    /**
     * Method that returns a list of SplitPair objects used to give instructions
     * (init index and end index) of how to split a list of size 'size' into buffers of
     * 'numberOfItemsInWhereClause' elements.
     */
    private static List<SplitPair> getSplitPairList(int size, int numberOfItemsInWhereClause) {

        int numberOfSplits = size / numberOfItemsInWhereClause;
        List<SplitPair> sList = new ArrayList<>();
        for (int i = 0 ; i <= numberOfSplits; i++) {
            if (i == numberOfSplits)
                sList.add(new SplitPair(
                        i * numberOfItemsInWhereClause,
                        size));
            else
                sList.add(new SplitPair(
                        i * numberOfItemsInWhereClause,
                        (i + 1 ) * numberOfItemsInWhereClause));
        }
        return sList;
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
