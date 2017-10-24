package org.hugoandrade.gymapp.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.presenter.SignUpPresenter;
import org.hugoandrade.gymapp.utils.LoginUtils;
import org.hugoandrade.gymapp.utils.UIUtils;


public class SignUpActivity extends ActivityBase<MVP.RequiredSignUpViewOps,
                                                 MVP.ProvidedSignUpPresenterOps,
                                                 SignUpPresenter>
        implements MVP.RequiredSignUpViewOps {

    private static final String INTENT_EXTRA_USER = "intent_extra_user";

    private TextView tvMessage;

    private EditText etUsername;
    private EditText etCode;
    private TextView tvValidateButton;
    private View mValidateButton;
    private ProgressBar mValidateProgressBar;

    private EditText etPassword;
    private EditText etPasswordConfirm;
    private TextView tvSignUpButton;
    private View mSignUpButton;
    private ProgressBar mSignUpProgressBar;

    public static Intent makeIntent(Context context) {
        return new Intent(context, SignUpActivity.class);
    }

    public static User extractUserFromIntent(Intent data) {
        return data.getParcelableExtra(INTENT_EXTRA_USER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(Activity.RESULT_CANCELED);

        initializeUI();

        setupValidateUI();

        super.onCreate(SignUpPresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_sign_up);

        tvMessage = (TextView) findViewById(R.id.tv_sign_up_message);

        etUsername = (EditText) findViewById(R.id.et_username);
        etCode        = (EditText) findViewById(R.id.et_code);
        mValidateButton      = findViewById(R.id.view_group_validate);
        tvValidateButton     = (TextView) findViewById(R.id.tv_validate);
        mValidateProgressBar = (ProgressBar) findViewById(R.id.progressBar_validate_button);

        etUsername.addTextChangedListener(mValidateTextWatcher);
        etCode.addTextChangedListener(mValidateTextWatcher);
        etCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_ACTION_DONE) {
                    attemptValidate();
                    return true;
                }
                return false;
            }
        });

        tvValidateButton.setOnClickListener(mOnClickListener);

        /* **************************** */


        etPassword        = (EditText) findViewById(R.id.et_password);
        etPasswordConfirm       = (EditText) findViewById(R.id.et_password_confirm);
        mSignUpButton      = findViewById(R.id.view_group_sign_up);
        tvSignUpButton     = (TextView) findViewById(R.id.tv_sign_up);
        mSignUpProgressBar = (ProgressBar) findViewById(R.id.progressBar_sign_up_button);

        etPassword.addTextChangedListener(mSignUpTextWatcher);
        etPasswordConfirm.addTextChangedListener(mSignUpTextWatcher);
        etPasswordConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.sign_up || id == EditorInfo.IME_ACTION_DONE) {
                    attemptSignUp();
                    return true;
                }
                return false;
            }
        });

        tvSignUpButton.setOnClickListener(mOnClickListener);
    }

    private void setupValidateUI() {
        tvMessage.setText("Insert your username and code");

        etUsername.setVisibility(View.VISIBLE);
        etCode.setVisibility(View.VISIBLE);
        mValidateButton.setVisibility(View.VISIBLE);

        etPassword.setVisibility(View.GONE);
        etPasswordConfirm.setVisibility(View.GONE);
        mSignUpButton.setVisibility(View.GONE);

        checkValidateInputFieldsValidity();
        enableValidateUI();
    }

    private void setupSignUpUI() {
        tvMessage.setText("Set new password");

        etUsername.setVisibility(View.VISIBLE);
        ((View) etCode.getParent()).setVisibility(View.GONE);
        mValidateButton.setVisibility(View.GONE);

        etPassword.setVisibility(View.VISIBLE);
        etPasswordConfirm.setVisibility(View.VISIBLE);
        mSignUpButton.setVisibility(View.VISIBLE);

        etUsername.setEnabled(false);

        checkSignUpInputFieldsValidity();
        enableSignUpUI();
    }

    @Override
    public void disableValidateUI() {
        etUsername.setEnabled(false);
        etCode.setEnabled(false);
        mValidateButton.setBackgroundColor(Color.parseColor("#3d000000"));
        mValidateProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void enableValidateUI() {
        etUsername.setEnabled(true);
        etCode.setEnabled(true);
        mValidateButton.setBackgroundColor(Color.parseColor("#3dffffff"));
        mValidateProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void disableSignUpUI() {
        etPassword.setEnabled(false);
        etPasswordConfirm.setEnabled(false);
        mSignUpButton.setBackgroundColor(Color.parseColor("#3d000000"));
        mSignUpProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void enableSignUpUI() {
        etPassword.setEnabled(true);
        etPasswordConfirm.setEnabled(true);
        mSignUpButton.setBackgroundColor(Color.parseColor("#3dffffff"));
        mSignUpProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void successfulWaitingUser(WaitingUser waitingUser) {
        setupSignUpUI();
    }

    @Override
    public void successfulSignUp(User user) {
        setResult(Activity.RESULT_OK, new Intent().putExtra(INTENT_EXTRA_USER, user));
        finish();
    }

    private void checkValidateInputFieldsValidity() {

        String username = etUsername.getText().toString();
        String code = etCode.getText().toString();

        if (!LoginUtils.isPasswordNotAllSpaces(code)
                || !LoginUtils.isUsernameAtLeast4CharactersLong(username)
                || !LoginUtils.isUsernameNotAllSpaces(username)) {

            tvValidateButton.setClickable(false);
            tvValidateButton.setTextColor(Color.parseColor("#3dffffff"));
            return;
        }

        tvValidateButton.setClickable(true);
        tvValidateButton.setTextColor(Color.WHITE);
    }

    private void checkSignUpInputFieldsValidity() {

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (!LoginUtils.isPasswordAtLeast4CharactersLong(password)
                || !LoginUtils.isPasswordNotAllSpaces(password)
                || !LoginUtils.isUsernameAtLeast4CharactersLong(username)
                || !LoginUtils.isUsernameNotAllSpaces(username)) {

            tvSignUpButton.setClickable(false);
            tvSignUpButton.setTextColor(Color.parseColor("#3dffffff"));
            return;
        }

        tvSignUpButton.setClickable(true);
        tvSignUpButton.setTextColor(Color.WHITE);
    }

    private void attemptValidate() {

        UIUtils.hideSoftKeyboardAndClearFocus(etUsername);
        UIUtils.hideSoftKeyboardAndClearFocus(etCode);

        String username = etUsername.getText().toString();
        String code = etCode.getText().toString();

        if (!LoginUtils.isPasswordNotAllSpaces(code)
                || !LoginUtils.isUsernameAtLeast4CharactersLong(username)
                || !LoginUtils.isUsernameNotAllSpaces(username))  {

            return;
        }

        getPresenter().validateWaitingUser(username, code);
    }

    private void attemptSignUp() {

        UIUtils.hideSoftKeyboardAndClearFocus(etPassword);
        UIUtils.hideSoftKeyboardAndClearFocus(etPasswordConfirm);

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String passwordConfirm = etPasswordConfirm.getText().toString();

        if (!LoginUtils.isPasswordAtLeast4CharactersLong(password)
                || !LoginUtils.isPasswordNotAllSpaces(password)
                || !LoginUtils.isUsernameAtLeast4CharactersLong(username)
                || !LoginUtils.isUsernameNotAllSpaces(username))  {

            reportMessage("Username-Password is not valid");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            reportMessage("Passwords do not match");
            return;
        }

        getPresenter().signUp(username, password);
    }


    @Override
    public void reportMessage(String message) {
        UIUtils.showSnackBar(findViewById(android.R.id.content), message);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == tvValidateButton) {
                attemptValidate();
            }
            else if (v == tvSignUpButton) {
                attemptSignUp();
            }
        }
    };

    private TextWatcher mValidateTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void afterTextChanged(Editable s) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkValidateInputFieldsValidity();
        }
    };

    private TextWatcher mSignUpTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void afterTextChanged(Editable s) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkSignUpInputFieldsValidity();
        }
    };
}
