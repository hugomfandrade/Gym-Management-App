// IMobileClientService.aidl
package org.hugoandrade.gymapp.model;

// Declare any non-default types here with import statements
import org.hugoandrade.gymapp.model.IMobileClientServiceCallback;
import org.hugoandrade.gymapp.data.WaitingUser;

interface IMobileClientService {
    void registerCallback(IMobileClientServiceCallback cb);
    void unregisterCallback(IMobileClientServiceCallback cb);

    boolean login(String username, String password);
    boolean signUp(String username, String password);
    boolean getAllGymUsers(String credential);
    boolean createGymUser(in WaitingUser waitingUser);
    boolean validateGymUser(in WaitingUser waitingUser);
}
