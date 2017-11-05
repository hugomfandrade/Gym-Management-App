package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.GymUserListModel;

import java.util.List;

public class GymUserListPresenter extends PresenterBase<MVP.RequiredGymUserListViewOps,
                                                        MVP.RequiredGymUserListPresenterOps,
                                                        MVP.ProvidedGymUserListModelOps,
                                                        GymUserListModel>
        implements MVP.ProvidedGymUserListPresenterOps,
                   MVP.RequiredGymUserListPresenterOps {

    @Override
    public void onCreate(MVP.RequiredGymUserListViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the GymUserListModel class to instantiate/manage and
        // "this" to provide GymUserListModel with this MVP.RequiredGymUserListModelOps
        // instance.
        super.onCreate(view, GymUserListModel.class, this);
    }

    @Override
    public void onResume() {
        // this activity is focused, so register callback from service
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(MVP.RequiredGymUserListViewOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void notifyServiceIsBound() {
        // Once service is bound, get all gym users that have the selected 'credential'
        getAllGymUsers();
    }

    private void getAllGymUsers() {
        // disable UI while waiting for web service response
        getView().disableUI();

        // get all gym users with the selected credential
        getModel().getAllGymUsers(getView().getCredential());
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
    public void gettingAllGymUsersOperationResult(boolean wasOperationSuccessful,
                                                  String errorMessage,
                                                  List<User> userList) {

        if (wasOperationSuccessful) {

            // operation was successful, display gym users
            getView().displayGymUserList(userList);
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
