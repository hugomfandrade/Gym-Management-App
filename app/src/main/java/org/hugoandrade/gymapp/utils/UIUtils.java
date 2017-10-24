package org.hugoandrade.gymapp.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

    /**
     * Hides the soft keyboard for the provided view.
     *
     * @param view The target view for soft keyboard input.
     */
    public static void hideSoftKeyboard(View view) {
        InputMethodManager imm =
                (InputMethodManager) view.getContext().getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Hides the soft keyboard for the provided view and clear focus.
     *
     * @param view The target view for soft keyboard input.
     */
    public static void hideSoftKeyboardAndClearFocus(View view) {
        view.clearFocus();
        hideSoftKeyboard(view);
    }
}
