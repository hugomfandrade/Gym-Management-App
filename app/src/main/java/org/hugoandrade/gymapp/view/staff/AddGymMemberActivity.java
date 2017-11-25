package org.hugoandrade.gymapp.view.staff;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.hugoandrade.gymapp.GlobalData;
import org.hugoandrade.gymapp.MVP;
import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.User;
import org.hugoandrade.gymapp.presenter.staff.AddGymMemberPresenter;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.ActivityBase;
import org.hugoandrade.gymapp.view.adapter.UserListAdapter;
import org.hugoandrade.gymapp.view.dialog.SimpleBuilderDialog;

import java.util.List;

public class AddGymMemberActivity extends ActivityBase<MVP.RequiredAddGymMemberViewOps,
                                                       MVP.ProvidedAddGymMemberPresenterOps,
                                                       AddGymMemberPresenter>

        implements MVP.RequiredAddGymMemberViewOps {

    /**
     * Constant that represents the name of the intent extra that is paired with a User object.
     */
    private static final String INTENT_EXTRA_USER = "intent_extra_user";

    /**
     * View showing "Waiting" and disables UI to be displayed when waiting
     * for the response of a Web request
     */
    private View vProgressBar;

    /**
     * Adapter used to display all gym members that are not in the My Members' list
     */
    private UserListAdapter mGymMemberListAdapter;

    /**
     * Message to be displayed when no gym member was retrieved
     */
    private TextView tvNoGymMemberMessage;

    /**
     * Factory method that makes an Intent used to start this Activity
     * when passed to startActivity().
     *
     * @param context The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context, AddGymMemberActivity.class);
    }

    /**
     * Method used to extract a User object from an Intent
     */
    public static User extractUserFromIntent(Intent data) {
        return data.getParcelableExtra(INTENT_EXTRA_USER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(Activity.RESULT_CANCELED);

        initializeUI();

        super.onCreate(AddGymMemberPresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_staff_add_gym_member);

        // set up toolbar and title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.action_add_gym_members);

        vProgressBar = findViewById(R.id.progressBar_waiting);

        // set up recycler view and adapter
        mGymMemberListAdapter = new UserListAdapter();
        mGymMemberListAdapter.setOnItemClickedListener(new UserListAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(final User user) {
                // Gym staff selected a user. Show a "confirm dialog" to
                // check if the gym staff wants to add the selected member
                // to the My Members' list
                String title = "Add to 'My Members'";
                String message = "Are you sure you add "
                        + user.getUsername()
                        + " to your 'My Members' Lost?";
                SimpleBuilderDialog builderDialog =
                        new SimpleBuilderDialog(getActivityContext(), title, message);
                builderDialog.setOnDialogResultListener(new SimpleBuilderDialog.OnDialogResultListener() {
                    @Override
                    public void onResult(DialogInterface dialog, @SimpleBuilderDialog.Result int result) {
                        if (result == SimpleBuilderDialog.YES) {
                            // YES clicked. add member to My Members' list
                            getPresenter().addMemberToMyMembers(user, GlobalData.getUser().getID());
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
        RecyclerView rvGymUsers = (RecyclerView) findViewById(R.id.rv_members);
        rvGymUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvGymUsers.setAdapter(mGymMemberListAdapter);

        // set up no gym members message
        tvNoGymMemberMessage = (TextView) findViewById(R.id.tv_no_members);
        tvNoGymMemberMessage.setText(R.string.no_members);
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
    public void displayGymMemberList(List<User> gymMemberList) {
        mGymMemberListAdapter.setAll(gymMemberList);

        // if list is empty, show "no gym members" message
        tvNoGymMemberMessage.setVisibility(gymMemberList.size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void memberAdded(User member) {
        // Put added member in the intent result and finish
        setResult(Activity.RESULT_OK, new Intent().putExtra(INTENT_EXTRA_USER, member));
        finish();
    }

    @Override
    public void reportMessage(String message) {
        UIUtils.showSnackBar(findViewById(android.R.id.content), message);
    }
}
