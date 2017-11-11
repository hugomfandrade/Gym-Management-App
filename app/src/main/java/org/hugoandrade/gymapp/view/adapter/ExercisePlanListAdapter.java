package org.hugoandrade.gymapp.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.ExercisePlan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExercisePlanListAdapter extends RecyclerView.Adapter<ExercisePlanListAdapter.ViewHolder> {

    /**
     * Date format used throughout this instance
     */
    private final static SimpleDateFormat dateFormat
            = new SimpleDateFormat("dd MMM YYYY", Locale.getDefault());

    private final List<ExercisePlan> mExercisePlanList;

    private OnItemClickListener mListener;

    public ExercisePlanListAdapter() {
        mExercisePlanList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        View v = vi.inflate(R.layout.list_item_exercise_plan, viewGroup, false);
        return new ExercisePlanListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        final ExercisePlan exercisePlan = mExercisePlanList.get(holder.getAdapterPosition());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onItemClicked(exercisePlan);
            }
        });
        holder.tvName.setText("Exercise Plan");
        holder.tvDatetime.setText(dateFormat.format(exercisePlan.getDatetime().getTime()));
    }

    @Override
    public int getItemCount() {
        return mExercisePlanList.size();
    }

    public void setAll(List<ExercisePlan> userList) {
        mExercisePlanList.clear();
        mExercisePlanList.addAll(userList);
        notifyDataSetChanged();
    }

    public void add(ExercisePlan exercisePlan) {
        mExercisePlanList.add(exercisePlan);
        notifyItemInserted(mExercisePlanList.size() - 1);
    }

    public void setOnItemClickedListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(ExercisePlan exercisePlan);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvDatetime;

        ViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvDatetime = (TextView) view.findViewById(R.id.tv_datetime);
        }
    }
}
