package org.hugoandrade.gymapp.presenter.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.hugoandrade.gymapp.utils.NetworkUtils;

/**
 * BroadcastReceiver that catches the changes to network connectivity changes
 * and sends a broadcast intent that signals the network connection state and is
 * caught by the Service that handles the Web Service
 */
public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {

    public final static String ACTION_NETWORK_CHANGE_RECEIVER =
            "org.hugoandrade.gymapp.ACTION_NETWORK_CHANGE_RECEIVER";
    public final static String ACTION_NETWORK_STATE =
            "org.hugoandrade.gymapp.ACTION_NETWORK_STATE";

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(ACTION_NETWORK_CHANGE_RECEIVER);
        if (NetworkUtils.isNetworkAvailable(context)) {
            i.putExtra(ACTION_NETWORK_STATE, true);
        }
        else {
            i.putExtra(ACTION_NETWORK_STATE, false);
        }

        context.getApplicationContext().sendBroadcast(i);
    }

    /**
     * Method to extract the connection state
     */
    public static boolean extractNetworkConnectionState(Intent intent) {
        return intent.getBooleanExtra(ACTION_NETWORK_STATE, false);
    }
}
