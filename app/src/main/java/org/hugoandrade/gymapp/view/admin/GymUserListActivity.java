package org.hugoandrade.gymapp.view.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.List;

public class GymUserListActivity extends ActivityBase<MVP.RequiredGymUserListViewOps,
                                                      MVP.ProvidedGymUserListPresenterOps,
                                                      GymUserListPresenter>

        implements MVP.RequiredGymUserListViewOps {

    private static final int CREATE_USER_REQUEST_CODE = 300;

    private static final String INTENT_EXTRA_CREDENTIAL = "intent_extra_credential";

    private String mCredential;

    private View vProgressBar;

    /**
     * FAB to create a new Gym User.
     */
    private FloatingActionButton fabCreateGymUser;

    private UserListAdapter mGymUserListAdapter;

    private TextView tvNoGymUserMessage;

    public static Intent makeIntent(Context context, String credential) {
        return new Intent(context, GymUserListActivity.class)
                .putExtra(INTENT_EXTRA_CREDENTIAL, credential);
    }

    private static String extractCredentialFromIntent(Intent intent) {
        return intent.getStringExtra(INTENT_EXTRA_CREDENTIAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCredential = extractCredentialFromIntent(getIntent());

        initializeUI();

        super.onCreate(GymUserListPresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_admin_list_of_user);

        vProgressBar = findViewById(R.id.progressBar_waiting);

        fabCreateGymUser = (FloatingActionButton) findViewById(R.id.fab_create_user);
        fabCreateGymUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        CreateGymUserActivity.makeIntent(GymUserListActivity.this, mCredential),
                        CREATE_USER_REQUEST_CODE);
            }
        });

        mGymUserListAdapter = new UserListAdapter();
        RecyclerView rvGymUsers = (RecyclerView) findViewById(R.id.rv_users);
        rvGymUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvGymUsers.setAdapter(mGymUserListAdapter);
        rvGymUsers.addOnScrollListener(new CustomRecyclerScroll() {
            @Override
            public void show() {
                showFab();
            }

            @Override
            public void hide() {
                hideFab();
            }
        });

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

        tvNoGymUserMessage.setVisibility(userList.size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_USER_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                User newUser = CreateGymUserActivity.extractUserFromIntent(data);
                mGymUserListAdapter.add(newUser);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void reportMessage(String message) {
        UIUtils.showSnackBar(findViewById(android.R.id.content), message);
    }

    static class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

        private final List<User> mUserList;

        UserListAdapter() {
            mUserList = new ArrayList<>();
        }

        void setAll(List<User> userList) {
            mUserList.clear();
            mUserList.addAll(userList);
            notifyDataSetChanged();
        }

        @Override
        public UserListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
            View v = vi.inflate(R.layout.list_item_user, viewGroup, false);
            return new UserListAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(UserListAdapter.ViewHolder holder, int i) {
            final User user = mUserList.get(holder.getAdapterPosition());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if (mListener != null) mListener.onItemClick(event);
                }
            });
            holder.tvUsername.setText(user.getUsername());
            holder.tvCredential.setText(user.getCredential());
        }

        @Override
        public int getItemCount() {
            return mUserList.size();
        }

        void add(User user) {
            mUserList.add(user);
            notifyItemInserted(mUserList.size() - 1);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvUsername;
            TextView tvCredential;

            ViewHolder(View view) {
                super(view);
                tvUsername = (TextView) view.findViewById(R.id.tv_gym_user_username);
                tvCredential = (TextView) view.findViewById(R.id.tv_gym_user_credential);
            }
        }
    }
}
