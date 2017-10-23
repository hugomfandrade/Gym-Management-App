package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.model.CreateStaffModel;

public class CreateStaffPresenter extends PresenterBase<MVP.RequiredCreateStaffViewOps,
                                                        MVP.RequiredCreateStaffPresenterOps,
                                                        MVP.ProvidedCreateStaffModelOps,
                                                        CreateStaffModel>
        implements MVP.ProvidedCreateStaffPresenterOps,
                   MVP.RequiredCreateStaffPresenterOps {

    @Override
    public void onCreate(MVP.RequiredCreateStaffViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the ImageModel class to instantiate/manage and
        // "this" to provide ImageModel with this OldMVP.RequiredModelOps
        // instance.
        super.onCreate(view, CreateStaffModel.class, this);
    }

    @Override
    public void onResume() { }

    @Override
    public void onConfigurationChange(MVP.RequiredCreateStaffViewOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void notifyServiceIsBound() {
        //No-ops
    }

    @Override
    public void createStaff(String username) {
        getView().disableUI();

        getModel().createStaff(username);
    }

    @Override
    public void creatingStaffOperationResult(boolean wasOperationSuccessful, String message, String username, String code) {
        if (wasOperationSuccessful) {

            getView().successfulCreateStaff(username, code);
        }
        else {
            if (message != null)
                getView().reportMessage(message);
        }

        getView().enableUI();
    }

    @Override
    public Context getActivityContext() {
        return getView().getActivityContext();
    }

    @Override
    public Context getApplicationContext() {
        return getView().getApplicationContext();
    }
}
