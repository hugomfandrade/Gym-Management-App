package org.hugoandrade.gymapp.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

public final class UIUtils {

    /**
     * Ensure this class is only used as a utility.
     */
    private UIUtils() {
        throw new AssertionError();
    }

    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Show a toast message.
     */
    public static void showSnackBar(View view,
                                    String message) {
        Snackbar.make(view,
                message,
                Snackbar.LENGTH_SHORT).show();
    }
}
