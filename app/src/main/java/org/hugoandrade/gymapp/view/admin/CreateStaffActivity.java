package org.hugoandrade.gymapp.view.admin;

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
import android.widget.TextView;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.presenter.CreateStaffPresenter;
import org.hugoandrade.gymapp.utils.LoginUtils;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.ActivityBase;


public class CreateStaffActivity extends ActivityBase<MVP.RequiredCreateStaffViewOps,
                                                      MVP.ProvidedCreateStaffPresenterOps,
                                                      CreateStaffPresenter>
        implements MVP.RequiredCreateStaffViewOps {

    private static final String INTENT_EXTRA_USER = "intent_extra_user";

    // Views for createStaff input
    private View vProgressBar;

    private EditText etUsername;
    private TextView tvCode;
    private TextView tvCreateStaffButton;
    private View mCreateStaffButton;

    public static Intent makeIntent(Context context) {
        return new Intent(context, CreateStaffActivity.class);
    }

    public static User extractUserFromIntent(Intent data) {
        return data.getParcelableExtra(INTENT_EXTRA_USER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();

        enableUI();

        super.onCreate(CreateStaffPresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_create_staff);

        vProgressBar = findViewById(R.id.progressBar_waiting);

        etUsername = (EditText) findViewById(R.id.et_username);
        tvCode = (TextView) findViewById(R.id.tv_code);
        mCreateStaffButton = findViewById(R.id.view_group_create);
        tvCreateStaffButton = (TextView) findViewById(R.id.tv_create);

        etUsername.addTextChangedListener(mTextWatcher);
        etUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_ACTION_DONE) {
                    attemptCreateStaff();
                    return true;
                }
                return false;
            }
        });

        tvCreateStaffButton.setOnClickListener(mOnClickListener);

        tvCode.setVisibility(View.INVISIBLE);
    }

    @Override
    public void disableUI() {
        vProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void enableUI() {
        vProgressBar.setVisibility(View.INVISIBLE);
    }

    private void checkUsernameValidity() {

        String username = etUsername.getText().toString();

        if (!LoginUtils.isUsernameAtLeast4CharactersLong(username)
                || !LoginUtils.isUsernameNotAllSpaces(username)) {

            tvCreateStaffButton.setClickable(false);
            tvCreateStaffButton.setTextColor(Color.parseColor("#3dffffff"));
            return;
        }

        tvCreateStaffButton.setClickable(true);
        tvCreateStaffButton.setTextColor(Color.WHITE);
    }

    private void attemptCreateStaff() {

        String username = etUsername.getText().toString();

        if (!LoginUtils.isUsernameAtLeast4CharactersLong(username)
                || !LoginUtils.isUsernameNotAllSpaces(username))  {

            return;
        }

        UIUtils.hideSoftKeyboardAndClearFocus(etUsername);

        getPresenter().createStaff(username);
    }

    @Override
    public void successfulCreateStaff(WaitingUser waitingUser) {
        mCreateStaffButton.setVisibility(View.INVISIBLE);

        etUsername.setEnabled(false);
        tvCode.setVisibility(View.VISIBLE);
        tvCode.setText(waitingUser.getCode());
    }

    @Override
    public void reportMessage(String message) {
        UIUtils.showSnackBar(findViewById(android.R.id.content), message);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == tvCreateStaffButton) {
                attemptCreateStaff();
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
            checkUsernameValidity();
        }
    };
}
