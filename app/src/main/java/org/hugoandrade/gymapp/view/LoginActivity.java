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

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.presenter.LoginPresenter;
import org.hugoandrade.gymapp.utils.LoginUtils;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.admin.AdminMainActivity;
import org.hugoandrade.gymapp.view.member.MemberMainActivity;
import org.hugoandrade.gymapp.view.staff.StaffMainActivity;


public class LoginActivity extends ActivityBase<MVP.RequiredLoginViewOps,
                                                MVP.ProvidedLoginPresenterOps,
                                                LoginPresenter>
        implements MVP.RequiredLoginViewOps {


    private static final int SIGN_UP_REQUEST_CODE = 100;

    // Views for createStaff input
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvLoginButton;
    private View tvSignUp;
    private View mLoginButton;
    private ProgressBar mLoginProgressBar;

    public static Intent makeIntent(Context context) {
        return new Intent(context, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalData.resetUser();

        initializeUI();

        setupUI();

        super.onCreate(LoginPresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_login);

        tvSignUp = findViewById(R.id.tv_sign_up);

        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword        = (EditText) findViewById(R.id.et_password);
        mLoginButton      = findViewById(R.id.view_group_login);
        tvLoginButton     = (TextView) findViewById(R.id.tv_login);
        mLoginProgressBar = (ProgressBar) findViewById(R.id.progressBar_login_button);

        etUsername.addTextChangedListener(mTextWatcher);
        etPassword.addTextChangedListener(mTextWatcher);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        tvLoginButton.setOnClickListener(mOnClickListener);
        tvSignUp.setOnClickListener(mOnClickListener);
    }

    private void setupUI() {
        etUsername.setText("Admin");
        etUsername.setSelection(etUsername.getText().toString().length());
        etPassword.setText("password");
        checkLoginInputFieldsValidity();
        setLoggingInStatus(false);
    }

    @Override
    public void setLoggingInStatus(boolean isLoggingIn) {
        etUsername.setEnabled(!isLoggingIn);
        etPassword.setEnabled(!isLoggingIn);
        mLoginButton.setBackgroundColor(isLoggingIn ?
                Color.parseColor("#3d000000"):
                Color.parseColor("#3dffffff"));
        mLoginProgressBar.setVisibility(isLoggingIn ?
                View.VISIBLE :
                View.INVISIBLE);
    }

    private void checkLoginInputFieldsValidity() {

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (!LoginUtils.isPasswordAtLeast4CharactersLong(password)
                || !LoginUtils.isPasswordNotAllSpaces(password)
                || !LoginUtils.isUsernameAtLeast4CharactersLong(username)
                || !LoginUtils.isUsernameNotAllSpaces(username)) {

            tvLoginButton.setClickable(false);
            tvLoginButton.setTextColor(Color.parseColor("#3dffffff"));
            return;
        }

        tvLoginButton.setClickable(true);
        tvLoginButton.setTextColor(Color.WHITE);
    }

    private void attemptLogin() {

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (!LoginUtils.isPasswordAtLeast4CharactersLong(password)
                || !LoginUtils.isPasswordNotAllSpaces(password)
                || !LoginUtils.isUsernameAtLeast4CharactersLong(username)
                || !LoginUtils.isUsernameNotAllSpaces(username))  {

            return;
        }

        getPresenter().login(username, password);
    }

    @Override
    public void successfulLogin(String credential) {
        switch (credential) {
            case User.Credential.ADMIN:
                startActivity(AdminMainActivity.makeIntent(this));
                break;
            case User.Credential.STAFF:
                startActivity(StaffMainActivity.makeIntent(this));
                break;
            case User.Credential.MEMBER:
                startActivity(MemberMainActivity.makeIntent(this));
                break;
        }
        finish();
    }

    @Override
    public void reportMessage(String message) {
        UIUtils.showSnackBar(findViewById(android.R.id.content), message);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == tvLoginButton) {
                attemptLogin();
            }
            else if (v == tvSignUp) {
                startActivityForResult(SignUpActivity.makeIntent(LoginActivity.this), SIGN_UP_REQUEST_CODE);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_UP_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                User user = SignUpActivity.extractUserFromIntent(data);
                etUsername.setText(user.getUsername());
                etPassword.setText(user.getPassword());
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void afterTextChanged(Editable s) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkLoginInputFieldsValidity();
        }
    };
}
