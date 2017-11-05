package org.hugoandrade.gymapp.presenter;

import android.content.Context;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.model.AddGymMemberModel;

import java.util.List;

public class AddGymMemberPresenter extends PresenterBase<MVP.RequiredAddGymMemberViewOps,
                                                      MVP.RequiredAddGymMemberPresenterOps,
                                                      MVP.ProvidedAddGymMemberModelOps,
        AddGymMemberModel>
        implements MVP.ProvidedAddGymMemberPresenterOps,
                   MVP.RequiredAddGymMemberPresenterOps {

    @Override
    public void onCreate(MVP.RequiredAddGymMemberViewOps view) {
        // Invoke the special onCreate() method in PresenterBase,
        // passing in the ImageModel class to instantiate/manage and
        // "this" to provide ImageModel with this OldMVP.RequiredModelOps
        // instance.
        super.onCreate(view, AddGymMemberModel.class, this);
    }

    @Override
    public void onResume() {
        getModel().registerCallback();
    }

    @Override
    public void onConfigurationChange(MVP.RequiredAddGymMemberViewOps view) { }

    @Override
    public void onPause() { }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        getModel().onDestroy(isChangingConfiguration);
    }

    @Override
    public void notifyServiceIsBound() {
        getGymMembersExceptMine();
    }

    private void getGymMembersExceptMine() {
        getView().disableUI();

        getModel().getGymMembersExceptMine(GlobalData.getUser().getID());
    }

    @Override
    public void addMemberToMyMembers(User member, String userID) {
        getView().disableUI();

        getModel().addMemberToMyMembers(member, userID);
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
    public void gettingAllGymMembersOperationResult(boolean wasOperationSuccessful,
                                                    String errorMessage,
                                                    List<User> myMemberList) {

        if (wasOperationSuccessful) {

            getView().displayGymMemberList(myMemberList);
        }
        else {
            if (errorMessage != null)
                getView().reportMessage(errorMessage);
        }

        getView().enableUI();

    }

    @Override
    public void addingMemberToMyMembersOperationResult(boolean wasOperationSuccessful, String errorMessage, User member) {
        getView().enableUI();

        if (wasOperationSuccessful) {

            getView().memberAdded(member);
        }
        else {
            if (errorMessage != null)
                getView().reportMessage(errorMessage);
        }

    }
}
