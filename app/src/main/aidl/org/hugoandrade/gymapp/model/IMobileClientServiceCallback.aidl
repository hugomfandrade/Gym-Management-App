// IMobileClientServiceCallback.aidl
package org.hugoandrade.gymapp.model;

import org.hugoandrade.gymapp.model.aidl.MobileClientData;

/**
 * Interface defining the method that receives callbacks from the
 * MobileClientService.
 */
interface IMobileClientServiceCallback {

    /**
     * This one-way (non-blocking) method allows MobileClientService
     * to return the MobileClientData results associated with the one-way
     * IMobileClientService calls.
     */
    oneway void sendResults(in MobileClientData mobileClientData);

}
