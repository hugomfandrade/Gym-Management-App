package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.StaffMainModel;

import java.util.List;

public class StaffMainPresenter extends PresenterBase<MVP.RequiredStaffMainViewOps,
                                                      MVP.RequiredStaffMainPresenterOps,
                                                      MVP.ProvidedStaffMainModelOps,
                                                      StaffMainModel>
        implements MVP.ProvidedStaffMainPresenterOps,
                   MVP.RequiredStaffMainPresenterOps {

    @Override
    public void onCreate(MVP.RequiredStaffMainViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the StaffMainModel class to instantiate/manage and
        // "this" to provide StaffMainModel with this MVP.RequiredMainModelOps
        // instance.
        super.onCreate(view, StaffMainModel.class, this);
    }

    @Override
    public void onResume() {
        // this activity is focused, so register callback from service
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(MVP.RequiredStaffMainViewOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void notifyServiceIsBound() {
        // Once service is bound, get gym users
        getAllGymUsers();
    }

    private void getAllGymUsers() {
        // disable UI while waiting for web service response
        getView().disableUI();

        // get all gym users
        getModel().getMyMembers(GlobalData.getUser().getID());
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
    public void gettingMyMembersOperationResult(boolean wasOperationSuccessful,
                                                String errorMessage,
                                                List<User> myMemberList) {

        if (wasOperationSuccessful) {
            // operation was successful, display the retrieved data
            getView().displayMyMembersList(myMemberList);
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
