package org.hugoandrade.gymapp.presenter.member;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.ExercisePlanSuggested;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;
import org.hugoandrade.gymapp.presenter.MobileClientPresenterBase;

import java.util.List;

public class SuggestedPlansPresenter extends MobileClientPresenterBase<MVP.RequiredSuggestedPlansViewOps>

        implements MVP.ProvidedSuggestedPlansPresenterOps {

    @Override
    public void onCreate(MVP.RequiredSuggestedPlansViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the MyGymStaffModel class to instantiate/manage and
        // "this" to provide MobileClientModel with this MVP.RequiredMobileServicePresenterOps
        // instance.
        super.onCreate(view);
    }

    @Override
    public void notifyServiceIsBound() {
        // Once service is bound, get gym staff of the logged in user's 'My Gym Staff'
        getSuggestedPlans();
    }

    private void getSuggestedPlans() {
        // disable UI while waiting for web service response
        getView().disableUI();

        // get gym staff of the logged in gym member
        doGetSuggestedPlans(GlobalData.getUser().getID());
    }

    /**
     * Try getting the suggested plans of the member with ID userID via the Service.
     */
    private void doGetSuggestedPlans(String userID) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            gettingSuggestedPlansOperationResult(false,
                                                 "Not bound to the service",
                                                 null);
            return;
        }

        try {
            boolean isGetting = getMobileClientService().getExercisePlanSuggestedList(userID);
            if (!isGetting) {
                gettingSuggestedPlansOperationResult(false,
                                                     "No Network Connection",
                                                     null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            gettingSuggestedPlansOperationResult(false,
                                                 "Error sending message",
                                                 null);
        }
    }

    @Override
    public void sendResults(MobileClientData data) {

        int operationType = data.getOperationType();
        boolean isOperationSuccessful
                = data.getOperationResult() == MobileClientData.OPERATION_SUCCESS;

        if (operationType == MobileClientData.OPERATION_GET_HISTORY_SUGGESTED) {
            gettingSuggestedPlansOperationResult(isOperationSuccessful,
                                                 data.getErrorMessage(),
                                                 data.getExercisePlanSuggestedList());
        }
    }

    /**
     * Handle the operation result of getting the suggested plans
     */
    private void gettingSuggestedPlansOperationResult(boolean wasOperationSuccessful,
                                                      String errorMessage,
                                                      List<ExercisePlanSuggested> suggestedPlanList) {

        if (wasOperationSuccessful) {

            // operation was successful, display list of gym staff
            getView().displaySuggestedPlansList(suggestedPlanList);
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
