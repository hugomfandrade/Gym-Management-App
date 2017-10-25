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
        // passing in the ImageModel class to instantiate/manage and
        // "this" to provide ImageModel with this OldMVP.RequiredModelOps
        // instance.
        super.onCreate(view, GymUserListModel.class, this);
    }

    @Override
    public void onResume() {
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
        getAllGymUsers();
    }

    private void getAllGymUsers() {
        getView().disableUI();

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

            getView().displayGymUserList(userList);
        }
        else {
            if (errorMessage != null)
                getView().reportMessage(errorMessage);
        }

        getView().enableUI();

    }
}
