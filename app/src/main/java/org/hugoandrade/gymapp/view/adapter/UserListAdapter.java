package org.hugoandrade.gymapp.view.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.User;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private final List<User> mUserList;

    private OnItemClickListener mListener;

    public UserListAdapter() {
        mUserList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        View v = vi.inflate(R.layout.list_item_user, viewGroup, false);
        return new UserListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        final User user = mUserList.get(holder.getAdapterPosition());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onItemClicked(user);
            }
        });
        holder.tvUsername.setText(user.getUsername());
        holder.tvCredential.setText(user.getCredential());
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void setAll(List<User> userList) {
        mUserList.clear();
        mUserList.addAll(userList);
        notifyDataSetChanged();
    }

    public void add(User user) {
        mUserList.add(user);
        notifyItemInserted(mUserList.size() - 1);
    }

    public void setOnItemClickedListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(User user);
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
