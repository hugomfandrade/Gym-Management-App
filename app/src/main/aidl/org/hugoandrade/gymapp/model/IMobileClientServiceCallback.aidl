// IMobileServiceClientServiceCallback.aidl
package org.hugoandrade.gymapp.model;

import org.hugoandrade.gymapp.model.aidl.MobileClientData;

/**
 * Interface defining the method that receives callbacks from the
 * MobileServiceClientService.
 */
interface IMobileClientServiceCallback {

    /**
     * This one-way (non-blocking) method allows MobileServiceClientService
     * to return the MobileClientData results associated with the one-way
     * IMobileServiceClientService calls.
     */
    oneway void sendResults(in MobileClientData mobileClientData);

}
