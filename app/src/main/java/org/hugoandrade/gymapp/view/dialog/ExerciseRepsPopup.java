package org.hugoandrade.gymapp.view.dialog;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.ExerciseRecord;

public class ExerciseRepsPopup extends PopupWindow {

    private final View mParentView;
    private final int mPosition;
    private final ExerciseRecord mExercise;

    private EditText etRepetitions;
    private SeekBar seekBarRepetitions;

    private OnExerciseRepsPopupListener mListener;
    private boolean wasExerciseRemoved = false;
    private boolean isTrackingTouch = false;

    public ExerciseRepsPopup(View view, int position, ExerciseRecord exercise) {
        super(View.inflate(view.getContext(), R.layout.layout_popup, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        mParentView = view;
        mPosition = position;
        mExercise = exercise;

        initUI();
    }
    private void initUI() {

        etRepetitions = (EditText) getContentView().findViewById(R.id.et_exercise_repetitions);
        seekBarRepetitions = (SeekBar) getContentView().findViewById(R.id.seekbar_exercise_repetitions);

        etRepetitions.setText(String.valueOf(mExercise.getNumberOfRepetitions()));
        etRepetitions.setEnabled(false);
        etRepetitions.setTextColor(Color.DKGRAY);
        seekBarRepetitions.setProgress(mExercise.getNumberOfRepetitions());
        seekBarRepetitions.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isTrackingTouch)
                    etRepetitions.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTrackingTouch = true;

                etRepetitions.clearFocus();
                etRepetitions.setFocusable(false);
                etRepetitions.setFocusableInTouchMode(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTrackingTouch = false;
            }
        });
        showAsDropDown(mParentView, 0,0);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!wasExerciseRemoved)
                    if (mListener != null)
                        mListener.onDismiss(mPosition, mExercise, seekBarRepetitions.getProgress());
            }
        });
        etRepetitions.clearFocus();
        etRepetitions.setFocusable(false);
        etRepetitions.setFocusableInTouchMode(true);

        seekBarRepetitions.setFocusable(true);
        seekBarRepetitions.setFocusableInTouchMode(true);

    }

    public void setOnExerciseRepsPopupListener(OnExerciseRepsPopupListener listener) {
        mListener = listener;
    }

    public interface OnExerciseRepsPopupListener {
        void onDismiss(int position, ExerciseRecord exercise, int numberOfRepetitions);
    }
}
