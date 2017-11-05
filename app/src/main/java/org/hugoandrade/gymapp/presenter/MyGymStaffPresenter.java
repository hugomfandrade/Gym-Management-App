package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.MyGymStaffModel;

import java.util.List;

public class MyGymStaffPresenter extends PresenterBase<MVP.RequiredMyGymStaffViewOps,
                                                       MVP.RequiredMyGymStaffPresenterOps,
                                                       MVP.ProvidedMyGymStaffModelOps,
                                                       MyGymStaffModel>
        implements MVP.ProvidedMyGymStaffPresenterOps,
                   MVP.RequiredMyGymStaffPresenterOps {

    @Override
    public void onCreate(MVP.RequiredMyGymStaffViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the MyGymStaffModel class to instantiate/manage and
        // "this" to provide MyGymStaffModel with this MVP.RequiredMyGymStaffModelModelOps
        // instance.
        super.onCreate(view, MyGymStaffModel.class, this);
    }

    @Override
    public void onResume() {
        // this activity is focused, so register callback from service
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(MVP.RequiredMyGymStaffViewOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void notifyServiceIsBound() {
        // Once service is bound, get gym staff of the logged in user's 'My Gym Staff'
        getMyGymStaff();
    }

    private void getMyGymStaff() {
        // disable UI while waiting for web service response
        getView().disableUI();

        // get gym staff of the logged in gym member
        getModel().getMyGymStaff(GlobalData.getUser().getID());
    }

    @Override
    public Context getActivityContext() {
        return getView().getActivityContext();
    }

    @Override
    public Context getApplicationContext() {
        return getView().getApplicationContext();
    }

    @Override
    public void gettingMyGymStaffOperationResult(boolean wasOperationSuccessful,
                                                 String errorMessage,
                                                 List<User> staffList) {

        if (wasOperationSuccessful) {

            // operation was successful, display list of gym staff
            getView().displayGymStaffList(staffList);
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
