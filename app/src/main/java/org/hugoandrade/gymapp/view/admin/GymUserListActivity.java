package org.hugoandrade.gymapp.view.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.common.CustomRecyclerScroll;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.presenter.GymUserListPresenter;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.ActivityBase;
import org.hugoandrade.gymapp.view.adapter.UserListAdapter;

import java.util.List;

public class GymUserListActivity extends ActivityBase<MVP.RequiredGymUserListViewOps,
                                                      MVP.ProvidedGymUserListPresenterOps,
                                                      GymUserListPresenter>

        implements MVP.RequiredGymUserListViewOps {

    /**
     * Constant that represents the request code used to start the "create user" activity.
     */
    private static final int CREATE_USER_REQUEST_CODE = 300;

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

    /**
     * FAB to create a new Gym User.
     */
    private FloatingActionButton fabCreateGymUser;

    /**
     * Adapter used to display all gym members of type 'credential'
     */
    private UserListAdapter mGymUserListAdapter;

    /**
     * Message to be displayed when no gym user was retrieved
     */
    private TextView tvNoGymUserMessage;

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context, String credential) {
        return new Intent(context, GymUserListActivity.class)
                .putExtra(INTENT_EXTRA_CREDENTIAL, credential);
    }

    /**
     * Method used to extract a String (Credential) object from an Intent
     */
    private static String extractCredentialFromIntent(Intent intent) {
        return intent.getStringExtra(INTENT_EXTRA_CREDENTIAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // extract desired gym user type
        mCredential = extractCredentialFromIntent(getIntent());

        initializeUI();

        super.onCreate(GymUserListPresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_admin_list_of_user);

        // set up toolbar and title appropriately
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        switch (mCredential) {
            case User.Credential.MEMBER:
                getSupportActionBar().setTitle(R.string.gym_members);
                break;
            case User.Credential.STAFF:
                getSupportActionBar().setTitle(R.string.gym_staff);
                break;
        }

        vProgressBar = findViewById(R.id.progressBar_waiting);

        // set up FAB
        fabCreateGymUser = (FloatingActionButton) findViewById(R.id.fab_create_user);
        fabCreateGymUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FAB was clicked; gym admin wants to create another gym user
                startActivityForResult(
                        CreateGymUserActivity.makeIntent(GymUserListActivity.this, mCredential),
                        CREATE_USER_REQUEST_CODE);
            }
        });

        // set up recycler view and adapter
        mGymUserListAdapter = new UserListAdapter();
        RecyclerView rvGymUsers = (RecyclerView) findViewById(R.id.rv_users);
        rvGymUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvGymUsers.setAdapter(mGymUserListAdapter);
        rvGymUsers.addOnScrollListener(new CustomRecyclerScroll() {
            @Override
            public void show() {
                // recycler view is scrolling up. show FAB
                showFab();
            }

            @Override
            public void hide() {
                // recycler view is scrolling down. hide FAB
                hideFab();
            }
        });

        // set up no gym users message appropriately
        tvNoGymUserMessage = (TextView) findViewById(R.id.tv_no_users);
        switch (mCredential) {
            case User.Credential.MEMBER:
                tvNoGymUserMessage.setText(R.string.no_members);
                break;
            case User.Credential.STAFF:
                tvNoGymUserMessage.setText(R.string.no_staff);
                break;
        }
    }

    /**
     * Show FloatingActionButton when the the RecyclerView is scrolling upwards.
     */
    public void showFab() {
        fabCreateGymUser.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator(2)).start();
    }

    /**
     * Hide FloatingActionButton when the RecyclerView is scrolling downwards.
     */
    public void hideFab() {
        fabCreateGymUser.animate().translationY(fabCreateGymUser.getHeight() +
                ((ViewGroup.MarginLayoutParams) fabCreateGymUser.getLayoutParams()).bottomMargin)
                .setInterpolator(new AccelerateInterpolator(2)).start();
    }

    @Override
    public void disableUI() {
        vProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void enableUI() {
        vProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public String getCredential() {
        return mCredential;
    }

    @Override
    public void displayGymUserList(List<User> userList) {
        mGymUserListAdapter.setAll(userList);

        // if list is empty, show "no gym users" message
        tvNoGymUserMessage.setVisibility(userList.size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_USER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Successfully created a gym user.
                // Extract the new user and add to the list

                User newUser = CreateGymUserActivity.extractUserFromIntent(data);
                mGymUserListAdapter.add(newUser);

                tvNoGymUserMessage.setVisibility(View.INVISIBLE);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void reportMessage(String message) {
        UIUtils.showSnackBar(findViewById(android.R.id.content), message);
    }
}
