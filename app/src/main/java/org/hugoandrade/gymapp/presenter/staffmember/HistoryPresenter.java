package org.hugoandrade.gymapp.presenter.staffmember;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.ExercisePlan;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;
import org.hugoandrade.gymapp.presenter.MobileClientPresenterBase;

import java.util.List;

public class HistoryPresenter extends MobileClientPresenterBase<MVP.RequiredHistoryViewOps>

        implements MVP.ProvidedHistoryPresenterOps {

    @Override
    public void onCreate(MVP.RequiredHistoryViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the HistoryModel class to instantiate/manage and
        // "this" to provide MobileClientModel with this MVP.RequiredMobileServicePresenterOps
        // instance.
        super.onCreate(view);
    }

    @Override
    public void notifyServiceIsBound() {
        // Once service is bound, get all exercise plans of the logged in gym member
        getHistory(getView().getUserID());
    }

    private void getHistory(String userID) {
        // disable UI while waiting for web service response
        getView().disableUI();

        // get all exercise plans recorded by the gym member
        doGetHistory(userID);
    }

    /**
     * Try getting all exercise plan records of the member with ID userID via the Service.
     */
    private void doGetHistory(String userID) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingHistoryOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isGetting = getMobileClientService().getExercisePlanList(userID);
            if (!isGetting) {
                gettingHistoryOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingHistoryOperationResult(false, "Error sending message", null);
        }
    }

    @Override
    public void sendResults(MobileClientData data) {

        int operationType = data.getOperationType();
        boolean isOperationSuccessful
                = data.getOperationResult() == MobileClientData.OPERATION_SUCCESS;

        if (operationType == MobileClientData.OPERATION_GET_HISTORY) {
            gettingHistoryOperationResult(isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getExercisePlanList());
        }
    }

    /**
     * Handle the operation result of getting all exercise plan records of the member
     */
    private void gettingHistoryOperationResult(boolean wasOperationSuccessful,
                                               String errorMessage,
                                               List<ExercisePlan> exercisePlanList) {

        if (wasOperationSuccessful) {

            // operation was successful, display exercise plan records
            getView().displayExercisePlanList(exercisePlanList);
        }
        else {
            // operation failed, show error message
            if (errorMessage != null)
                getView().reportMessage(errorMessage);
        }

        // enable UI
        getView().enableUI();

    }
}
