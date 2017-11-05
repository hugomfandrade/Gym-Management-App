// IMobileClientService.aidl
package org.hugoandrade.gymapp.model;

// Declare any non-default types here with import statements
import org.hugoandrade.gymapp.model.IMobileClientServiceCallback;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlanRecord;

interface IMobileClientService {
    void registerCallback(IMobileClientServiceCallback cb);
    void unregisterCallback(IMobileClientServiceCallback cb);

    boolean login(String username, String password);
    boolean signUp(String username, String password);
    boolean getAllGymUsers(String credential);
    boolean createGymUser(in WaitingUser waitingUser);
    boolean validateGymUser(in WaitingUser waitingUser);
    boolean getMyGymMembers(String userID);
    boolean getGymMembersExceptMine(String userID);
    boolean addMemberToMyMembers(in User member, String userID);
    boolean getMyGymStaff(String userID);
    boolean getAllExercises();
    boolean createExercise(in Exercise exercise);
    boolean createWorkout(in ExercisePlanRecord exercisePlanRecord);
    boolean getExercisePlanRecordList(String userID);
}
