package org.hugoandrade.gymapp.model.service;

import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.ExercisePlan;
import org.hugoandrade.gymapp.data.ExerciseSet;
import org.hugoandrade.gymapp.data.User;

import java.util.ArrayList;
import java.util.List;

final class MobileClientServiceHelper {

    /**
     * Returns a list of all User objects that do not have a credential yet
     */
    static List<User> getUsersWithoutCredential(List<User> userList) {

        List<User> users = new ArrayList<>();
        for (User user : userList)
            if (user.getCredential() == null)
                users.add(user);

        return users;
    }

    /**
     * Returns a arrays of strings that represent the ID of all User objects
     */
    static String[] getIDsOfUsers(List<User> userList) {

        String[] ids = new String[userList.size()];
        for (int i = 0 ; i < userList.size() ; i++)
            ids[i] = userList.get(i).getID();

        return ids;
    }

    /**
     * Returns a arrays of strings that represent the ID of all Exercise objects
     */
    static String[] getIDsOfExercises(List<Exercise> exerciseList) {

        String[] ids = new String[exerciseList.size()];
        for (int i = 0 ; i < exerciseList.size() ; i++)
            ids[i] = exerciseList.get(i).getID();

        return ids;
    }

    /**
     * Returns a arrays of strings that represent the ID of all ExerciseSet objects
     */
    static String[] getIDsOfExerciseSets(List<ExerciseSet> exerciseSetList) {

        String[] ids = new String[exerciseSetList.size()];
        for (int i = 0 ; i < exerciseSetList.size() ; i++)
            ids[i] = exerciseSetList.get(i).getID();

        return ids;
    }

    /**
     * Returns a arrays of strings that represent the ID of all ExercisePlan objects
     */
    static String[] getIDsOfExercisePlans(List<? extends ExercisePlan> plans) {
        final List<String> ids = new ArrayList<>();

        // Extract the ids of the all ExercisePlan(s)
        for (ExercisePlan plan : plans)
            if (!ids.contains(plan.getID()))
                ids.add(plan.getID());

        return ids.toArray(new String[ids.size()]);
    }
}
