package org.hugoandrade.gymapp.presenter.member;

import android.os.RemoteException;
import android.util.Log;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.ExercisePlanSuggested;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;
import org.hugoandrade.gymapp.presenter.MobileClientPresenterBase;

import java.util.Calendar;

public class SuggestedPlanDetailsPresenter extends MobileClientPresenterBase<MVP.RequiredSuggestedPlanDetailsViewOps>

        implements MVP.ProvidedSuggestedPlanDetailsPresenterOps {

    @Override
    public void onCreate(MVP.RequiredSuggestedPlanDetailsViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the SuggestedPlanDetailsModel class to instantiate/manage and
        // "this" to provide MobileClientModel with this MVP.RequiredMobileServicePresenterOps
        // instance.
        super.onCreate(view);
    }

    @Override
    public void notifyServiceIsBound() {
        // No-ops
    }

    @Override
    public void dismissSuggestedPlan(ExercisePlanSuggested suggestedPlan, boolean wasDone) {
        // disable UI while waiting for web service response
        getView().disableUI();

        // dismiss exercise plan
        suggestedPlan.setDatetime(Calendar.getInstance());
        doDismissSuggestedPlan(suggestedPlan, wasDone);
    }

    /**
     * Try dismissing the suggested plan via the Service.
     */
    private void doDismissSuggestedPlan(ExercisePlanSuggested suggestedPlan, boolean wasDone) {
        if (getMobileClientService() == null) {
            Log.w(TAG, "Service is still not bound");
            dismissingSuggestedPlanOperationResult(false, "Not bound to the service", null);
            return;
        }

        try {
            boolean isDismissing = getMobileClientService().dismissSuggestedPlan(suggestedPlan, wasDone);
            if (!isDismissing) {
                dismissingSuggestedPlanOperationResult(false, "No Network Connection", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            dismissingSuggestedPlanOperationResult(false, "Error sending message", null);
        }
    }

    @Override
    public void sendResults(MobileClientData data) {

        int operationType = data.getOperationType();
        boolean isOperationSuccessful
                = data.getOperationResult() == MobileClientData.OPERATION_SUCCESS;

        if (operationType == MobileClientData.OPERATION_DISMISS_SUGGESTED_PLAN) {
            dismissingSuggestedPlanOperationResult(isOperationSuccessful,
                    data.getErrorMessage(),
                    data.getExercisePlanSuggested());
        }
    }

    /**
     * Handle the operation result of dismissing the suggested plan
     */
    private void dismissingSuggestedPlanOperationResult(boolean wasOperationSuccessful,
                                                        String errorMessage,
                                                        ExercisePlanSuggested suggestedPlan) {

        if (wasOperationSuccessful) {

            // operation was successful, display list of gym staff
            getView().suggestedPlanDismissed(suggestedPlan);
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
