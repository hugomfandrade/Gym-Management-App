package org.hugoandrade.gymapp;

import org.hugoandrade.gymapp.data.User;

public class GlobalData {

    private static User user;

    public static void initializeUser(User user) {
        GlobalData.user = user;
    }

    public static void resetUser(){
        user = null;
    }

    public static User getUser() {
        return user;
    }
}
