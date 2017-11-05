package org.hugoandrade.gymapp.view.member;

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
import org.hugoandrade.gymapp.presenter.MyGymStaffPresenter;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.ActivityBase;
import org.hugoandrade.gymapp.view.adapter.UserListAdapter;
import org.hugoandrade.gymapp.view.admin.CreateGymUserActivity;

import java.util.List;

public class MyGymStaffActivity extends ActivityBase<MVP.RequiredMyGymStaffViewOps,
                                                     MVP.ProvidedMyGymStaffPresenterOps,
                                                     MyGymStaffPresenter>

        implements MVP.RequiredMyGymStaffViewOps {

    /**
     * View showing "Waiting" and disables UI to be displayed when waiting
     * for the response of a Web request
     */
    private View vProgressBar;

    /**
     * Adapter used to display the staff of the 'My Staff'
     */
    private UserListAdapter mMyGymStaffListAdapter;

    /**
     * Message to be displayed when no staff of the member's 'My Staff' was retrieved
     */
    private TextView tvNoMyGymStaffMessage;

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context, MyGymStaffActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();

        super.onCreate(MyGymStaffPresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_member_my_staff);

        // set up toolbar and title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.action_check_my_gym_staff);

        vProgressBar = findViewById(R.id.progressBar_waiting);

        // set up recycler view and adapter
        mMyGymStaffListAdapter = new UserListAdapter();
        RecyclerView rvGymUsers = (RecyclerView) findViewById(R.id.rv_my_staff);
        rvGymUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvGymUsers.setAdapter(mMyGymStaffListAdapter);

        // set up no gym my staff message appropriately
        tvNoMyGymStaffMessage = (TextView) findViewById(R.id.tv_no_my_staff);
        tvNoMyGymStaffMessage.setText(R.string.no_my_staff);
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
    public void displayGymStaffList(List<User> staffList) {
        mMyGymStaffListAdapter.setAll(staffList);

        // if list is empty, show "no my gym staff" message
        tvNoMyGymStaffMessage.setVisibility(staffList.size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void reportMessage(String message) {
        UIUtils.showSnackBar(findViewById(android.R.id.content), message);
    }
}
