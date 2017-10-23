package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.GymStaffListModel;

import java.util.List;

public class GymStaffListPresenter extends PresenterBase<MVP.RequiredGymStaffListViewOps,
                                                         MVP.RequiredGymStaffListPresenterOps,
                                                         MVP.ProvidedGymStaffListModelOps,
                                                         GymStaffListModel>
        implements MVP.ProvidedGymStaffListPresenterOps,
                   MVP.RequiredGymStaffListPresenterOps {

    @Override
    public void onCreate(MVP.RequiredGymStaffListViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the ImageModel class to instantiate/manage and
        // "this" to provide ImageModel with this OldMVP.RequiredModelOps
        // instance.
        super.onCreate(view, GymStaffListModel.class, this);
    }

    @Override
    public void onResume() { }

    @Override
    public void onConfigurationChange(MVP.RequiredGymStaffListViewOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void notifyServiceIsBound() {
        getAllStaff();
    }

    public void getAllStaff() {
        getView().disableUI();

        getModel().getAllStaff();
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
    public void gettingAllStaffOperationResult(boolean wasOperationSuccessful,
                                               String errorMessage,
                                               List<User> userList) {

        if (wasOperationSuccessful) {

            getView().displayStaffList(userList);
        }
        else {
            if (errorMessage != null)
                getView().reportMessage(errorMessage);
        }

        getView().enableUI();

    }
}
