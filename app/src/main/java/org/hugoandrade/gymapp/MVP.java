package org.hugoandrade.gymapp;

import org.hugoandrade.gymapp.common.ContextView;
import org.hugoandrade.gymapp.common.ModelOps;
import org.hugoandrade.gymapp.common.PresenterOps;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.data.WaitingUser;

import java.util.List;

/**
 * Created by Hugo Andrade on 22/10/2017.
 */

public interface MVP {
    /** For LOGIN **/
    interface RequiredLoginViewOps extends ContextView {
        void setLoggingInStatus(boolean isLoggingIn);
        void successfulLogin(String credential);

        void reportMessage(String message);
    }
    interface ProvidedLoginPresenterOps extends PresenterOps<RequiredLoginViewOps> {
        void login(String username, String password);
    }
    interface RequiredLoginPresenterOps extends ContextView {
        void loginOperationResult(boolean wasOperationSuccessful, String message, User user);
    }
    interface ProvidedLoginModelOps extends ModelOps<RequiredLoginPresenterOps> {
        void login(String username, String password);
    }

    /** For SignUp **/
    interface RequiredSignUpViewOps extends ContextView {
        void disableValidateUI();
        void enableValidateUI();
        void disableSignUpUI();
        void enableSignUpUI();
        void successfulWaitingUser(WaitingUser waitingUser);
        void successfulSignUp(User user);

        void reportMessage(String message);
    }
    interface ProvidedSignUpPresenterOps extends PresenterOps<RequiredSignUpViewOps> {
        void validateWaitingUser(String username, String code);
        void signUp(String username, String password);
    }
    interface RequiredSignUpPresenterOps extends RequiredMobileClientPresenterBaseOps {
        void validateWaitingUserOperationResult(boolean wasOperationSuccessful, String message, WaitingUser waitingUser);
        void signUpOperationResult(boolean wasOperationSuccessful, String message, User user);
    }
    interface ProvidedSignUpModelOps extends ModelOps<RequiredSignUpPresenterOps> {
        void validateWaitingUser(WaitingUser waitingUser);
        void signUp(String username, String password);
    }

    /** For Create Staff **/
    interface RequiredCreateStaffViewOps extends ContextView {
        void disableUI();
        void enableUI();
        void successfulCreateStaff(WaitingUser waitingUser);
        void reportMessage(String message);
    }
    interface ProvidedCreateStaffPresenterOps extends PresenterOps<RequiredCreateStaffViewOps> {
        void createStaff(String username);
    }
    interface RequiredCreateStaffPresenterOps extends RequiredMobileClientPresenterBaseOps {
        void creatingStaffOperationResult(boolean wasOperationSuccessful, String message, WaitingUser waitingUser);
    }
    interface ProvidedCreateStaffModelOps extends ModelOps<RequiredCreateStaffPresenterOps> {
        void createUser(WaitingUser waitingUser);
    }


    /** For Gym Staff **/
    interface RequiredGymStaffListViewOps extends ContextView {
        void disableUI();
        void enableUI();

        void displayStaffList(List<User> userList);

        void reportMessage(String message);
    }
    interface ProvidedGymStaffListPresenterOps extends PresenterOps<RequiredGymStaffListViewOps> {
    }
    interface RequiredGymStaffListPresenterOps extends RequiredMobileClientPresenterBaseOps {
        void gettingAllStaffOperationResult(boolean wasOperationSuccessful, String errorMessage, List<User> userList);
    }
    interface ProvidedGymStaffListModelOps extends ModelOps<RequiredGymStaffListPresenterOps> {
        void getAllStaff();
    }

    interface RequiredMobileClientPresenterBaseOps extends ContextView {
        void notifyServiceIsBound();
    }
    interface ProvidedMobileClientModelBaseOps<RequiredPresenterOps> extends ModelOps<RequiredPresenterOps> {
    }
}
