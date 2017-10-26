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
import org.hugoandrade.gymapp.presenter.CreateGymUserPresenter;
import org.hugoandrade.gymapp.utils.LoginUtils;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.ActivityBase;


public class CreateGymUserActivity extends ActivityBase<MVP.RequiredCreateGymUserViewOps,
                                                        MVP.ProvidedCreateGymUserPresenterOps,
                                                        CreateGymUserPresenter>
        implements MVP.RequiredCreateGymUserViewOps {

    private static final String INTENT_EXTRA_USER = "intent_extra_user";
    private static final String INTENT_EXTRA_CREDENTIAL = "intent_extra_credential";

    private String mCredential;

    // Views for CreateUser input
    private View vProgressBar;

    private EditText etUsername;
    private TextView tvCode;
    private TextView tvCreateGymUserButton;
    private View mCreateGymUserButton;

    public static Intent makeIntent(Context context, String credential) {
        return new Intent(context, CreateGymUserActivity.class)
                .putExtra(INTENT_EXTRA_CREDENTIAL, credential);
    }

    private static String extractCredentialFromIntent(Intent intent) {
        return intent.getStringExtra(INTENT_EXTRA_CREDENTIAL);
    }

    public static User extractUserFromIntent(Intent data) {
        return data.getParcelableExtra(INTENT_EXTRA_USER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCredential = extractCredentialFromIntent(getIntent());

        initializeUI();

        enableUI();

        super.onCreate(CreateGymUserPresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_admin_create_user);

        vProgressBar = findViewById(R.id.progressBar_waiting);

        etUsername = (EditText) findViewById(R.id.et_username);
        tvCode = (TextView) findViewById(R.id.tv_code);
        mCreateGymUserButton = findViewById(R.id.view_group_create);
        tvCreateGymUserButton = (TextView) findViewById(R.id.tv_create);

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

            tvCreateGymUserButton.setClickable(false);
            tvCreateGymUserButton.setTextColor(Color.parseColor("#3dffffff"));
            return;
        }

        tvCreateGymUserButton.setClickable(true);
        tvCreateGymUserButton.setTextColor(Color.WHITE);
    }

    private void attemptCreateUser() {

        String username = etUsername.getText().toString();

        if (!LoginUtils.isUsernameAtLeast4CharactersLong(username)
                || !LoginUtils.isUsernameNotAllSpaces(username))  {

            return;
        }

        UIUtils.hideSoftKeyboardAndClearFocus(etUsername);

        getPresenter().createGymUser(username, mCredential);
    }

    @Override
    public void successfulCreateGymUser(WaitingUser waitingUser) {
        mCreateGymUserButton.setVisibility(View.INVISIBLE);

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