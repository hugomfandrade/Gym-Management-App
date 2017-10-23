// IMobileServiceClientService.aidl
package org.hugoandrade.gymapp.model;

// Declare any non-default types here with import statements
import org.hugoandrade.gymapp.model.IMobileClientServiceCallback;

interface IMobileClientService {
    void registerCallback(IMobileClientServiceCallback cb);
    void unregisterCallback(IMobileClientServiceCallback cb);


    boolean login(String username, String password);
    boolean getAllStaff();
}
