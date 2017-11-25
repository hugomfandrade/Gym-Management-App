package org.hugoandrade.gymapp;

import org.hugoandrade.gymapp.common.ContextView;
import org.hugoandrade.gymapp.common.ModelOps;
import org.hugoandrade.gymapp.common.PresenterOps;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlan;
import org.hugoandrade.gymapp.data.ExercisePlanSuggested;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.model.IMobileClientService;
import org.hugoandrade.gymapp.model.aidl.MobileClientData;

import java.util.List;


public interface MVP {

    /**
     * Presenter Ops that the MobileClientPresenterBase in the "Presenter" layer,
     * which interacts with the Remote Web Service, implements
     */
    interface RequiredMobileServicePresenterOps extends RequiredMobileClientPresenterBaseOps {

        /**
         * "Model" reports to the "Presenter" the data that results from the request to the Remote
         * Web Service.
         */
        void sendResults(MobileClientData mobileClientData);
    }

    /**
     * Model Ops that the MobileClientModel in the "Model" layer, which interacts with the
     * Remote Web Service, implements
     */
    interface ProvidedMobileServiceModelOps extends ProvidedMobileClientModelBaseOps<RequiredMobileServicePresenterOps> {
        IMobileClientService getService();
    }

    /** These interfaces define the minimum public API provided and required by the
     * LoginActivity class in the View layer, the LoginPresenter in the
     * Presenter layer, and LoginModel in the Model layer to interact between each other.
     */
    interface RequiredLoginViewOps extends RequiredMobileClientViewBaseOps {
        /*
         * Displays in the login form the last logged in username-password
         */
        void displayLastLogin(User user);

        /*
         * User successfully logged in. Go to next activity depending on the credential type
         */
        void successfulLogin(String credential);
    }
    interface ProvidedLoginPresenterOps extends PresenterOps<RequiredLoginViewOps> {
        /*
         * User wants to login with the username-password inputed
         */
        void login(String username, String password);
    }


    /** These interfaces define the minimum public API provided and required by the
     * SignUpActivity class in the View layer, the SignUpPresenter in the
     * Presenter layer, and SignUpModel in the Model layer to interact between each other.
     */
    interface RequiredSignUpViewOps extends ContextView {
        /*
         * Disable UI while waiting for the validate operation
         */
        void disableValidateUI();

        /*
         * Enable UI for validation operation
         */
        void enableValidateUI();

        /*
         * Disable UI while waiting for the signing up operation
         */
        void disableSignUpUI();

        /*
         * Enable UI for signing up operation
         */
        void enableSignUpUI();

        /*
         * Validation operation was successful. Set up sign up step
         */
        void successfulWaitingUser(WaitingUser waitingUser);

        /*
         * User successfully signed up. Finish activity and return to Login activity
         */
        void successfulSignUp(User user);

        /*
         * Show a message as a SnackBar.
         */
        void reportMessage(String message);
    }
    interface ProvidedSignUpPresenterOps extends PresenterOps<RequiredSignUpViewOps> {

        /*
         * User wants to validate the username-code combo that the user inputted
         */
        void validateWaitingUser(String username, String code);

        /*
         * User wants to sign up with the username-password combo.
         */
        void signUp(String username, String password);
    }

    /* ************************************************* */
    /* ********************* Admin ********************* */
    /* ************************************************* */

    /** These interfaces define the minimum public API provided and required by the
     * CreateGymUserActivity class in the View layer, the CreateGymUserPresenter in the
     * Presenter layer, and CreateGymUserModel in the Model layer to interact between each other.
     */
    interface RequiredCreateGymUserViewOps extends RequiredMobileClientViewBaseOps {

        /*
         * Notify that the new gym user was successfully created and display generated code
         * to be used when the new user tries to sign up.
         */
        void successfulCreateGymUser(WaitingUser waitingUser);
    }
    interface ProvidedCreateGymUserPresenterOps extends PresenterOps<RequiredCreateGymUserViewOps> {

        /*
         * Staff wants to create a gym member or staff depending on the credential value.
         */
        void createGymUser(String username, String credential);
    }


    /** These interfaces define the minimum public API provided and required by the
     * CreateExerciseActivity class in the View layer, the CreateExercisePresenter in the
     * Presenter layer, and CreateExerciseModel in the Model layer to interact between each other.
     */
    interface RequiredCreateExerciseViewOps extends RequiredMobileClientViewBaseOps {

        /*
         * Notify that the exercise was successfully created and finish activity.
         */
        void successfulCreateExercise(Exercise exercise);
    }
    interface ProvidedCreateExercisePresenterOps extends PresenterOps<RequiredCreateExerciseViewOps> {

        /*
         * Admin wants to create a new exercise.
         */
        void createExercise(String name);
    }

    /** These interfaces define the minimum public API provided and required by the
     * ExerciseListActivity class in the View layer, the ExerciseListPresenter in the
     * Presenter layer, and ExerciseListModel in the Model layer to interact between each other.
     */
    interface RequiredExerciseListViewOps extends RequiredMobileClientViewBaseOps {
        /*
         * Display the list of exercises
         */
        void displayExerciseList(List<Exercise> exerciseList);
    }
    interface ProvidedExerciseListPresenterOps extends PresenterOps<RequiredExerciseListViewOps> {
    }


    /** These interfaces define the minimum public API provided and required by the
     * GymUserListActivity class in the View layer, the GymUserListPresenter in the
     * Presenter layer, and GymUserListModel in the Model layer to interact between each other.
     */
    interface RequiredGymUserListViewOps extends RequiredMobileClientViewBaseOps {
        /*
         * Returns the Credential type of this activity.
         */
        String getCredential();

        /*
         * Display the list of gym users
         */
        void displayGymUserList(List<User> userList);
    }
    interface ProvidedGymUserListPresenterOps extends PresenterOps<RequiredGymUserListViewOps> {
    }

    /* ***************************************************** */
    /* ********************* Gym Staff ********************* */
    /* ***************************************************** */

    /** These interfaces define the minimum public API provided and required by the
     * AddGymMemberActivity class in the View layer, the AddGymMemberPresenter in the
     * Presenter layer, and AddGymMemberModel in the Model layer to interact between each other.
     */
    interface RequiredAddGymMemberViewOps extends RequiredMobileClientViewBaseOps {

        /*
         * Display the list of gym members that are not in the "My Members" of the staff
         */
        void displayGymMemberList(List<User> gymMemberList);


        /*
         * Notify that the member was successfully added to the "My Member" list of the staff
         * and finish activity.
         */
        void memberAdded(User member);
    }
    interface ProvidedAddGymMemberPresenterOps extends PresenterOps<RequiredAddGymMemberViewOps> {
        /*
         * Staff wants to add member to his/her "My Members" list.
         */
        void addMemberToMyMembers(User member, String userID);
    }

    /** These interfaces define the minimum public API provided and required by the
     * StaffMainActivity class in the View layer, the StaffMainPresenter in the
     * Presenter layer, and StaffMainModel in the Model layer to interact between each other.
     */
    interface RequiredStaffMainViewOps extends RequiredMobileClientViewBaseOps {
        /*
         * Display the members of the "My Members" list.
         */
        void displayMyMembersList(List<User> myMemberList);
    }
    interface ProvidedStaffMainPresenterOps extends PresenterOps<RequiredStaffMainViewOps> {
    }

    /* ****************************************************** */
    /* ********************* Gym Member ********************* */
    /* ****************************************************** */

    /** These interfaces define the minimum public API provided and required by the
     * SuggestedPlanDetailsActivity class in the View layer, the SuggestedPlanDetailsPresenter in the
     * Presenter layer, and SuggestedPlanDetailsModel in the Model layer to interact between each other.
     */
    interface RequiredSuggestedPlanDetailsViewOps extends RequiredMobileClientViewBaseOps {
        /*
         * Suggested plan was dismissed. Return to previous activity
         */
        void suggestedPlanDismissed(ExercisePlanSuggested suggestedPlan);
    }
    interface ProvidedSuggestedPlanDetailsPresenterOps extends PresenterOps<RequiredSuggestedPlanDetailsViewOps> {
        /*
         * Member dismisses the suggested plan
         */
        void dismissSuggestedPlan(ExercisePlanSuggested suggestedPlan, boolean wasDone);
    }


    /** These interfaces define the minimum public API provided and required by the
     * SuggestedPlansActivity class in the View layer, the SuggestedPlansPresenter in the
     * Presenter layer, and SuggestedPlansModel in the Model layer to interact between each other.
     */
    interface RequiredSuggestedPlansViewOps extends RequiredMobileClientViewBaseOps {
        /*
         * Display suggested plans in list
         */
        void displaySuggestedPlansList(List<ExercisePlanSuggested> suggestedPlanList);
    }
    interface ProvidedSuggestedPlansPresenterOps extends PresenterOps<RequiredSuggestedPlansViewOps> {
    }

    /** These interfaces define the minimum public API provided and required by the
     * MyGymStaffActivity class in the View layer, the MyGymStaffPresenter in the
     * Presenter layer, and MyGymStaffModel in the Model layer to interact between each other.
     */
    interface RequiredMyGymStaffViewOps extends RequiredMobileClientViewBaseOps {
        /*
         * Display staffs in list
         */
        void displayGymStaffList(List<User> staffList);
    }
    interface ProvidedMyGymStaffPresenterOps extends PresenterOps<RequiredMyGymStaffViewOps> {
    }


    /* ****************************************************** */
    /* ***************** Gym Member & Staff ***************** */
    /* ****************************************************** */

    /** These interfaces define the minimum public API provided and required by the
     * HistoryActivity class in the View layer and the HistoryPresenter in the
     * Presenter layer.
     */
    interface RequiredHistoryViewOps extends RequiredMobileClientViewBaseOps {
        /*
         * Display all exercise plan records in list
         */
        void displayExercisePlanList(List<ExercisePlan> exercisePlanList);

        /*
         * Returns the ID of the selected user of this activity.
         */
        String getUserID();
    }
    interface ProvidedHistoryPresenterOps extends PresenterOps<RequiredHistoryViewOps> {
    }


    /** These interfaces define the minimum public API provided and required by the
     * BuildWorkoutActivity class in the View layer and the BuildWorkoutPresenter in the
     * Presenter layer.
     */
    interface RequiredBuildWorkoutViewOps extends RequiredMobileClientViewBaseOps {
        /*
         * All possible exercises were retrieved. This list is used when creating
         * a new exercise plan record
         */
        void setExerciseList(List<Exercise> exerciseList);
        /*
         * Notify exercise plan record was successfully created/saved and leave activity
         */
        void exercisePlanCreated(ExercisePlan exercisePlan);
    }
    interface ProvidedBuildWorkoutPresenterOps extends PresenterOps<RequiredBuildWorkoutViewOps> {
        /*
         * Member wants to create by saving the built exercise plan record in the Web service.
         */
        void createWorkout(ExercisePlan exercisePlan);
        /*
         * Staff wants to create by saving the suggested exercise plan record in the Web service.
         */
        void createSuggestedWorkout(ExercisePlanSuggested exercisePlanSuggested);
    }

    /* ****************************************************** */
    /* ****************** Base Interfaces ******************* */
    /* ****************************************************** */

    /**
     * Base View Ops that all views in the "View" layer which interact with the
     * Remote Web Service must implement
     */
    interface RequiredMobileClientViewBaseOps extends ContextView {
        /**
         * Disable UI by displaying over all layout a "Loading" progress bar
         */
        void disableUI();

        /**
         * Enable UI by dismissing the "Loading" progress bar
         */
        void enableUI();

        /**
         * Show a message, usually as a SnackBar.
         */
        void reportMessage(String message);
    }

    /**
     * Base Presenter Ops that all presenters in the "Presenter" layer which interact with the
     * Remote Web Service must implement
     */
    interface RequiredMobileClientPresenterBaseOps extends ContextView {

        /**
         * "Model" notifies the "Presenter" that the Service is Bound
         */
        void notifyServiceIsBound();
    }
    /**
     * Base Model Ops that all models in the "Model" layer which interact with the
     * Remote Web Service must implement
     */
    interface ProvidedMobileClientModelBaseOps<RequiredPresenterOps> extends ModelOps<RequiredPresenterOps> {

        /**
         * Tells "Model" to listen to callbacks from the Service
         */
        void registerCallback();
    }
}
