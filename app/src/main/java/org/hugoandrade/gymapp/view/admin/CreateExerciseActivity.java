package org.hugoandrade.gymapp.view.admin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.presenter.CreateExercisePresenter;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.ActivityBase;


public class CreateExerciseActivity extends ActivityBase<MVP.RequiredCreateExerciseViewOps,
                                                         MVP.ProvidedCreateExercisePresenterOps,
                                                         CreateExercisePresenter>
        implements MVP.RequiredCreateExerciseViewOps {

    /**
     * Constant that represents the name of the intent extra that is paired with a Exercise object.
     */
    private static final String INTENT_EXTRA_EXERCISE = "intent_extra_exercise";

    /**
     * View showing "Waiting" and disables UI to be displayed when waiting
     * for the response of a Web request
     */
    private View vProgressBar;

    /*
     * Views for Exercise input
     */
    private EditText etExerciseName; // insert name of the new exercise
    private TextView tvCreateExerciseButton;

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context, CreateExerciseActivity.class);
    }

    /**
     * Method used to extract an Exercise object from an Intent
     */
    public static Exercise extractExerciseFromIntent(Intent data) {
        return data.getParcelableExtra(INTENT_EXTRA_EXERCISE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();

        enableUI();

        super.onCreate(CreateExercisePresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_admin_create_exercise);

        //set up toolbar and title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.create_exercise);

        vProgressBar = findViewById(R.id.progressBar_waiting);

        etExerciseName = (EditText) findViewById(R.id.et_exercise_name);
        tvCreateExerciseButton = (TextView) findViewById(R.id.tv_create);

        // add text changed listener to enable/disable the button according to what
        // is written
        etExerciseName.addTextChangedListener(mTextWatcher);
        etExerciseName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.create || id == EditorInfo.IME_ACTION_DONE) {
                    attemptCreateExercise();
                    return true;
                }
                return false;
            }
        });

        tvCreateExerciseButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void disableUI() {
        vProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void enableUI() {
        vProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Method used to verify the Exercise validity and set up button accordingly
     */
    private void checkNameValidity() {

        String name = etExerciseName.getText().toString();

        // Check if exercise name is empty or has all spaces
        if (name.trim().isEmpty()) {

            tvCreateExerciseButton.setClickable(false);
            tvCreateExerciseButton.setTextColor(Color.parseColor("#3dffffff"));
            return;
        }

        tvCreateExerciseButton.setClickable(true);
        tvCreateExerciseButton.setTextColor(Color.WHITE);
    }

    /**
     * Method used create Exercise after verifying the Exercise validity.
     */
    private void attemptCreateExercise() {

        String name = etExerciseName.getText().toString();

        // If name is empty or has all spaces, do not attempt to create
        if (name.trim().isEmpty())  {

            return;
        }

        UIUtils.hideSoftKeyboardAndClearFocus(etExerciseName);

        getPresenter().createExercise(name);
    }

    @Override
    public void successfulCreateExercise(Exercise exercise) {

        // Put created exercise in the intent result and finish
        setResult(Activity.RESULT_OK, new Intent().putExtra(INTENT_EXTRA_EXERCISE, exercise));
        finish();
    }

    @Override
    public void reportMessage(String message) {
        UIUtils.showSnackBar(findViewById(android.R.id.content), message);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == tvCreateExerciseButton) {
                attemptCreateExercise();
            }
        }
    };

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void afterTextChanged(Editable s) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkNameValidity();
        }
    };
}
