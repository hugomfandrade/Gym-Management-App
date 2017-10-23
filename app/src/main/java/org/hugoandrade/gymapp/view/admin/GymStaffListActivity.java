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
import org.hugoandrade.gymapp.presenter.GymStaffListPresenter;
import org.hugoandrade.gymapp.utils.UIUtils;
import org.hugoandrade.gymapp.view.ActivityBase;

import java.util.ArrayList;
import java.util.List;

public class GymStaffListActivity extends ActivityBase<MVP.RequiredGymStaffListViewOps,
                                                       MVP.ProvidedGymStaffListPresenterOps,
                                                       GymStaffListPresenter>

        implements MVP.RequiredGymStaffListViewOps {

    private static final int CREATE_STAFF_REQUEST_CODE = 300;

    private View vProgressBar;
    /**
     * FAB to create a new Staff.
     */
    private FloatingActionButton fabCreateStaff;

    private UserListAdapter mStaffListAdapter;

    private View tvNoStaffMessage;

    public static Intent makeIntent(Context context) {
        return new Intent(context, GymStaffListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeUI();

        super.onCreate(GymStaffListPresenter.class, this);
    }

    private void initializeUI() {
        setContentView(R.layout.activity_admin_list_of_staff);

        vProgressBar = findViewById(R.id.progressBar_waiting);

        fabCreateStaff = (FloatingActionButton) findViewById(R.id.fab_create_staff);
        fabCreateStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        CreateStaffActivity.makeIntent(GymStaffListActivity.this),
                        CREATE_STAFF_REQUEST_CODE);
            }
        });

        mStaffListAdapter = new UserListAdapter();
        RecyclerView rvStaff = (RecyclerView) findViewById(R.id.rv_staff);
        rvStaff.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvStaff.setAdapter(mStaffListAdapter);
        rvStaff.addOnScrollListener(new CustomRecyclerScroll() {
            @Override
            public void show() {
                showFab();
            }

            @Override
            public void hide() {
                hideFab();
            }
        });

        tvNoStaffMessage = findViewById(R.id.tv_no_staffs);
    }

    /**
     * Show FloatingActionButton when the the RecyclerView is scrolling upwards.
     */
    public void showFab() {
        fabCreateStaff.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator(2)).start();
    }

    /**
     * Hide FloatingActionButton when the RecyclerView is scrolling downwards.
     */
    public void hideFab() {
        fabCreateStaff.animate().translationY(fabCreateStaff.getHeight() +
                ((ViewGroup.MarginLayoutParams) fabCreateStaff.getLayoutParams()).bottomMargin)
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
    public void displayStaffList(List<User> userList) {
        mStaffListAdapter.setAll(userList);

        tvNoStaffMessage.setVisibility(userList.size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_STAFF_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                User newUser = CreateStaffActivity.extractUserFromIntent(data);
                mStaffListAdapter.add(newUser);
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
            View v = vi.inflate(R.layout.list_item_staff, viewGroup, false);
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

            ViewHolder(View view) {
                super(view);
                tvUsername = (TextView) view.findViewById(R.id.tv_staff_username);
            }
        }
    }
}
