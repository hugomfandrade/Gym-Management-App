package org.hugoandrade.gymapp.presenter.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.hugoandrade.gymapp.utils.NetworkUtils;

public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {

    public final static String ACTION_NETWORK_CHANGE_RECEIVER =
            "org.hugoandrade.gymapp.ACTION_NETWORK_CHANGE_RECEIVER";
    public final static String ACTION_NETWORK_STATE =
            "org.hugoandrade.gymapp.ACTION_NETWORK_STATE";

    public NetworkChangeBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent newIntent = new Intent(ACTION_NETWORK_CHANGE_RECEIVER);
        if (NetworkUtils.isNetworkAvailable(context)) {
            newIntent.putExtra(ACTION_NETWORK_STATE, true);
        }
        else {
            newIntent.putExtra(ACTION_NETWORK_STATE, false);
        }

        context.getApplicationContext().sendBroadcast(newIntent);
    }
}
