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

        void displayLastLogin(User user);

        void setLoggingInStatus(boolean isLoggingIn);

        void successfulLogin(String credential);

        void reportMessage(String message);
    }
    interface ProvidedLoginPresenterOps extends PresenterOps<RequiredLoginViewOps> {
        void login(String username, String password);
    }
    interface RequiredLoginPresenterOps extends RequiredMobileClientPresenterBaseOps {
        void loginOperationResult(boolean wasOperationSuccessful, String message, User user);

        void displayLastLogin(User user);
    }
    interface ProvidedLoginModelOps extends ProvidedMobileClientModelBaseOps<RequiredLoginPresenterOps> {
        void login(String username, String password);

        void getLastLogin();

        void insertLastLogin(User user);
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
    interface ProvidedSignUpModelOps extends ProvidedMobileClientModelBaseOps<RequiredSignUpPresenterOps> {
        void validateWaitingUser(WaitingUser waitingUser);
        void signUp(String username, String password);
    }

    /** For Create Staff **/
    interface RequiredCreateGymUserViewOps extends ContextView {
        void disableUI();
        void enableUI();
        void successfulCreateGymUser(WaitingUser waitingUser);
        void reportMessage(String message);
    }
    interface ProvidedCreateGymUserPresenterOps extends PresenterOps<RequiredCreateGymUserViewOps> {
        void createGymUser(String username, String credential);
    }
    interface RequiredCreateGymUserPresenterOps extends RequiredMobileClientPresenterBaseOps {
        void creatingGymUserOperationResult(boolean wasOperationSuccessful, String message, WaitingUser waitingUser);
    }
    interface ProvidedCreateGymUserModelOps extends ProvidedMobileClientModelBaseOps<RequiredCreateGymUserPresenterOps>  {
        void createGymUser(WaitingUser waitingUser);
    }

    /** For Gym Staff **/
    interface RequiredGymUserListViewOps extends ContextView {
        void disableUI();

        void enableUI();

        String getCredential();

        void displayGymUserList(List<User> userList);

        void reportMessage(String message);
    }
    interface ProvidedGymUserListPresenterOps extends PresenterOps<RequiredGymUserListViewOps> {
    }
    interface RequiredGymUserListPresenterOps extends RequiredMobileClientPresenterBaseOps {
        void gettingAllGymUsersOperationResult(boolean wasOperationSuccessful, String errorMessage, List<User> userList);
    }
    interface ProvidedGymUserListModelOps extends ProvidedMobileClientModelBaseOps<RequiredGymUserListPresenterOps> {
        void getAllGymUsers(String credential);
    }

    interface RequiredMobileClientPresenterBaseOps extends ContextView {
        void notifyServiceIsBound();
    }
    interface ProvidedMobileClientModelBaseOps<RequiredPresenterOps> extends ModelOps<RequiredPresenterOps> {
        void registerCallback();
    }
}
