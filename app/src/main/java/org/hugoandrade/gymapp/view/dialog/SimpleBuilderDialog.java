package org.hugoandrade.gymapp.view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IntDef;
import android.view.KeyEvent;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SimpleBuilderDialog {

    @SuppressWarnings("unused")
    private static final String TAG = SimpleBuilderDialog.class.getSimpleName();

    @SuppressWarnings("WeakerAccess") public static final int YES = 1;
    @SuppressWarnings("WeakerAccess") public static final int NO = 2;
    @SuppressWarnings("WeakerAccess") public static final int BACK = 3;
    @SuppressWarnings("WeakerAccess") public static final int CANCEL = 4;

    @Retention(RetentionPolicy.SOURCE) @IntDef({YES, NO, BACK, CANCEL})
    public @interface Result {}

    private OnDialogResultListener mOnDialogResultListener;
    private Context mContext;
    private String mTitle;
    private String mMessage;
    private View mView;

    public SimpleBuilderDialog(Context context, String title, String message) {
        mContext = context;
        mTitle = title;
        mMessage = message;

        buildPlan();
    }

    @SuppressWarnings("unused")
    public SimpleBuilderDialog(Context context, View view) {
        mContext = context;
        mView = view;

        buildPlanWithView();
    }

    private void buildPlan() {
        // Initialize and build the AlertBuilderDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle(mTitle)
                .setMessage(mMessage);

        setupListeners(builder);

        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildPlanWithView() {
        // Initialize and build the AlertBuilderDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setView(mView);

        setupListeners(builder);

        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void setupListeners(AlertDialog.Builder builder) {
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mOnDialogResultListener != null)
                            mOnDialogResultListener.onResult(dialog, YES);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mOnDialogResultListener != null)
                            mOnDialogResultListener.onResult(dialog, NO);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (mOnDialogResultListener != null)
                            mOnDialogResultListener.onResult(dialog, CANCEL);
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                            if (mOnDialogResultListener != null)
                                mOnDialogResultListener.onResult(dialog, BACK);
                        }
                        return false;
                    }
                });
    }

    public void setOnDialogResultListener(OnDialogResultListener onDialogResultListener) {
        mOnDialogResultListener = onDialogResultListener;
    }

    public interface OnDialogResultListener {
        void onResult(DialogInterface dialog, @SimpleBuilderDialog.Result int result);
    }
}
