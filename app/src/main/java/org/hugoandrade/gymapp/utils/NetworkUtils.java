package org.hugoandrade.gymapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

public final class NetworkUtils {

    /**
     * Ensure this class is only used as a utility.
     */
    private NetworkUtils() {
        throw new AssertionError();
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final Network[] networks = connMgr.getAllNetworks();

        boolean wifiAvailability = false;
        boolean mobileAvailability = false;

        for (Network network : networks) {
            if (connMgr.getNetworkInfo(network).getType() == ConnectivityManager.TYPE_WIFI &&
                    connMgr.getNetworkInfo(network).isAvailable()) {
                wifiAvailability = true;
                //Log.e("Options", "Wifi True" );
            }
            else if (connMgr.getNetworkInfo(network).getType() == ConnectivityManager.TYPE_MOBILE &&
                    connMgr.getNetworkInfo(network).isAvailable()) {
                mobileAvailability = true;
                //Log.e("Options", "Mobile True" );
            }
        }

        return wifiAvailability || mobileAvailability;
    }
}
