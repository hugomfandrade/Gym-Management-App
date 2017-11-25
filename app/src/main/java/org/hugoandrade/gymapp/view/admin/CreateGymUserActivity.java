package org.hugoandrade.gymapp.view.admin;

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
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.data.WaitingUser;
import org.hugoandrade.gymapp.presenter.admin.CreateGymUserPresenter;
import org.hugoandrade.gymapp.utils.LoginUtils;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.ActivityBase;


public class CreateGymUserActivity extends ActivityBase<MVP.RequiredCreateGymUserViewOps,
                                                        MVP.ProvidedCreateGymUserPresenterOps,
                                                        CreateGymUserPresenter>
        implements MVP.RequiredCreateGymUserViewOps {

    /**
     * Constant that represents the name of the intent extra that is paired with a User object.
     */
    private static final String INTENT_EXTRA_USER = "intent_extra_user";

    /**
     * Constant that represents the name of the intent extra that is paired with a String object.
     */
    private static final String INTENT_EXTRA_CREDENTIAL = "intent_extra_credential";

    /**
     * The type of gym user that is desired to create
     */
    private String mCredential;

    /**
     * View showing "Waiting" and disables UI to be displayed when waiting
     * for the response of a Web request
     */
    private View vProgressBar;

    /*
      * Views for User input
      */
    private EditText etUsername;
    private TextView tvCode;
    private TextView tvCreateGymUserButton;
    private View mCreateGymUserButton;

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context, String credential) {
        return new Intent(context, CreateGymUserActivity.class)
                .putExtra(INTENT_EXTRA_CREDENTIAL, credential);
    }

    /**
     * Method used to extract a String object from an Intent
     */
    private static String extractCredentialFromIntent(Intent intent) {
        return intent.getStringExtra(INTENT_EXTRA_CREDENTIAL);
    }

    /**
     * Method used to extract an User object from an Intent
     */
    public static User extractUserFromIntent(Intent data) {
        return data.getParcelableExtra(INTENT_EXTRA_USER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // extract desired gym user type
        mCredential = extractCredentialFromIntent(getIntent());

        initializeUI();

        enableUI();

        super.onCreate(CreateGymUserPresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_admin_create_user);

        // set up tool bar and title appropriately
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        switch (mCredential) {
            case User.Credential.MEMBER:
                getSupportActionBar().setTitle(R.string.create_gym_member);
                break;
            case User.Credential.STAFF:
                getSupportActionBar().setTitle(R.string.create_gym_staff);
                break;
        }


        vProgressBar = findViewById(R.id.progressBar_waiting);

        etUsername = (EditText) findViewById(R.id.et_username);
        tvCode = (TextView) findViewById(R.id.tv_code);
        mCreateGymUserButton = findViewById(R.id.view_group_create);
        tvCreateGymUserButton = (TextView) findViewById(R.id.tv_create);

        // add text changed listener to enable/disable the button according to what
        // is written
        etUsername.addTextChangedListener(mTextWatcher);
        etUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.create || id == EditorInfo.IME_ACTION_DONE) {
                    attemptCreateUser();
                    return true;
                }
                return false;
            }
        });

        tvCreateGymUserButton.setOnClickListener(mOnClickListener);

        // Hide code form for 1st part of this activity
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

    /**
     * Method used to verify the Username validity and set up button accordingly
     */
    private void checkUsernameValidity() {

        String username = etUsername.getText().toString();

        // Check if username is valid
        if (!LoginUtils.isUsernameAtLeast4CharactersLong(username)
                || !LoginUtils.isUsernameNotAllSpaces(username)) {

            tvCreateGymUserButton.setClickable(false);
            tvCreateGymUserButton.setTextColor(Color.parseColor("#3dffffff"));
            return;
        }

        tvCreateGymUserButton.setClickable(true);
        tvCreateGymUserButton.setTextColor(Color.WHITE);
    }

    /**
     * Method used create User after verifying the username validity.
     */
    private void attemptCreateUser() {

        String username = etUsername.getText().toString();

        // If username is not valid, do not attempt to create
        if (!LoginUtils.isUsernameAtLeast4CharactersLong(username)
                || !LoginUtils.isUsernameNotAllSpaces(username))  {

            return;
        }

        UIUtils.hideSoftKeyboardAndClearFocus(etUsername);

        getPresenter().createGymUser(username, mCredential);
    }

    @Override
    public void successfulCreateGymUser(WaitingUser waitingUser) {
        // display code
        etUsername.setEnabled(false);
        mCreateGymUserButton.setVisibility(View.INVISIBLE);
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
            if (v == tvCreateGymUserButton) {
                attemptCreateUser();
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
