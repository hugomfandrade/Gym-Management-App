package org.hugoandrade.gymapp.view.dialog;

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
    public static final int YES = 1;
    public static final int NO = 2;
    public static final int BACK = 3;
    public static final int CANCEL = 4;

    @Retention(RetentionPolicy.SOURCE) @IntDef({YES, NO, BACK, CANCEL})
    public @interface Result {}

    private OnDialogResult mOnDialogResult;
    private Context context;
    private String title;
    private String message;
    private View view;


    public SimpleBuilderDialog(Context context, String title, String message) {
        this.context = context;
        this.title = title;
        this.message = message;

        buildPlan();
    }

    public SimpleBuilderDialog(Context context, View view) {
        this.context = context;
        this.view = view;

        buildPlanWithView();
    }

    private void buildPlanWithView() {

        // Initialize and build the AlertBuilderDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setView(view)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mOnDialogResult != null)
                            mOnDialogResult.onResult(dialog, YES);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mOnDialogResult != null)
                            mOnDialogResult.onResult(dialog, NO);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (mOnDialogResult != null)
                            mOnDialogResult.onResult(dialog, CANCEL);
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                            if (mOnDialogResult != null)
                                mOnDialogResult.onResult(dialog, BACK);
                        }
                        return false;
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }


    private void buildPlan() {
        // Initialize and build the AlertBuilderDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mOnDialogResult != null)
                            mOnDialogResult.onResult(dialog, YES);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mOnDialogResult != null)
                            mOnDialogResult.onResult(dialog, NO);
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (mOnDialogResult != null)
                            mOnDialogResult.onResult(dialog, CANCEL);
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                            if (mOnDialogResult != null)
                                mOnDialogResult.onResult(dialog, BACK);
                        }
                        return false;
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }


    public void setOnDialogResultListener(OnDialogResult onDialogResultListener) {
        this.mOnDialogResult = onDialogResultListener;
    }

    public interface OnDialogResult {
        void onResult(DialogInterface dialog, @SimpleBuilderDialog.Result int result);
    }
}
