package org.hugoandrade.gymapp.view.staff;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.common.CustomRecyclerScroll;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.presenter.StaffMainPresenter;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.ActivityBase;
import org.hugoandrade.gymapp.view.adapter.UserListAdapter;
import org.hugoandrade.gymapp.view.staffmember.HistoryActivity;

import java.util.List;

public class StaffMainActivity extends ActivityBase<MVP.RequiredStaffMainViewOps,
                                                    MVP.ProvidedStaffMainPresenterOps,
                                                    StaffMainPresenter>

        implements MVP.RequiredStaffMainViewOps {

    /**
     * Constant that represents the request code used to start the "add gym member to
     * My Members' list" activity.
     */
    private static final int ADD_GYM_MEMBER_REQUEST_CODE = 400;

    /**
     * View showing "Waiting" and disables UI to be displayed when waiting
     * for the response of a Web request
     */
    private View vProgressBar;

    /**
     * FAB to add a new Gym Member to 'My Member' List.
     */
    private FloatingActionButton fabAddGymMember;

    /**
     * Adapter used to display all gym members that are in the My Members' list
     */
    private UserListAdapter mMyGymMemberListAdapter;

    /**
     * Message to be displayed when no gym member was retrieved
     */
    private TextView tvNoMyGymMemberMessage;

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context, StaffMainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize UI
        initializeUI();

        super.onCreate(StaffMainPresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_staff_main);

        // Set toolbar and title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.action_check_my_gym_members);

        vProgressBar = findViewById(R.id.progressBar_waiting);

        // set up FAB
        fabAddGymMember = (FloatingActionButton) findViewById(R.id.fab_add_gym_member);
        fabAddGymMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // FAB was clicked; gym staff wants to add another gym member to their list
                startActivityForResult(
                        AddGymMemberActivity.makeIntent(StaffMainActivity.this),
                        ADD_GYM_MEMBER_REQUEST_CODE);
            }
        });

        // set up recycler view and adapter
        mMyGymMemberListAdapter = new UserListAdapter();
        mMyGymMemberListAdapter.setOnItemClickedListener(new UserListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(User user) {

                // Member wants to check the history of saved exercise plans
                startActivity(HistoryActivity.makeIntent(StaffMainActivity.this, user));
            }
        });
        RecyclerView rvGymUsers = (RecyclerView) findViewById(R.id.rv_my_members);
        rvGymUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvGymUsers.setAdapter(mMyGymMemberListAdapter);
        rvGymUsers.addOnScrollListener(new CustomRecyclerScroll() {
            @Override
            public void show() {
                // recycler view is scrolling up. show FAB
                showFab();
            }

            @Override
            public void hide() {
                // recycler view is scrolling down. show FAB
                hideFab();
            }
        });

        // set up no gym members message
        tvNoMyGymMemberMessage = (TextView) findViewById(R.id.tv_no_my_members);
        tvNoMyGymMemberMessage.setText(R.string.no_my_members);
    }

    /**
     * Show FloatingActionButton when the the RecyclerView is scrolling upwards.
     */
    public void showFab() {
        fabAddGymMember.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator(2)).start();
    }

    /**
     * Hide FloatingActionButton when the RecyclerView is scrolling downwards.
     */
    public void hideFab() {
        fabAddGymMember.animate().translationY(fabAddGymMember.getHeight() +
                ((ViewGroup.MarginLayoutParams) fabAddGymMember.getLayoutParams()).bottomMargin)
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
    public void displayMyMembersList(List<User> myMemberList) {
        mMyGymMemberListAdapter.setAll(myMemberList);

        // if list is empty, show "no gym members" message
        tvNoMyGymMemberMessage.setVisibility(myMemberList.size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_GYM_MEMBER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Successfully added a gym member to staff's list.
                // Extract the new member and add to the list
                User newUser = AddGymMemberActivity.extractUserFromIntent(data);
                mMyGymMemberListAdapter.add(newUser);

                tvNoMyGymMemberMessage.setVisibility(mMyGymMemberListAdapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE);
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
