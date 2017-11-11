package org.hugoandrade.gymapp.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.ExercisePlan;
import org.hugoandrade.gymapp.data.ExercisePlanSuggested;

import java.util.ArrayList;
import java.util.List;

public class ExercisePlanSuggestedListAdapter extends RecyclerView.Adapter<ExercisePlanSuggestedListAdapter.ViewHolder> {

    private final List<ExercisePlanSuggested> mExercisePlanSuggestedList;

    private OnItemClickListener mListener;

    public ExercisePlanSuggestedListAdapter() {
        mExercisePlanSuggestedList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        View v = vi.inflate(R.layout.list_item_exercise_plan_suggested, viewGroup, false);
        return new ExercisePlanSuggestedListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        final ExercisePlanSuggested exercisePlanSuggested
                = mExercisePlanSuggestedList.get(holder.getAdapterPosition());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onItemClicked(exercisePlanSuggested);
            }
        });
        holder.tvName.setText("Exercise Plan");
        holder.tvStaff.setText(exercisePlanSuggested.getStaff().getUsername());
    }

    @Override
    public int getItemCount() {
        return mExercisePlanSuggestedList.size();
    }

    public void setAll(List<ExercisePlanSuggested> suggestedList) {
        mExercisePlanSuggestedList.clear();
        mExercisePlanSuggestedList.addAll(suggestedList);
        notifyDataSetChanged();
    }

    public void add(ExercisePlanSuggested exercisePlanSuggested) {
        mExercisePlanSuggestedList.add(exercisePlanSuggested);
        notifyItemInserted(mExercisePlanSuggestedList.size() - 1);
    }

    public void setOnItemClickedListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void remove(ExercisePlanSuggested exercisePlanSuggested) {
        for (int i = 0; i < mExercisePlanSuggestedList.size() ; i++) {
            if (exercisePlanSuggested.getID() != null &&
                    exercisePlanSuggested.getID().equals(mExercisePlanSuggestedList.get(i).getID())) {
                mExercisePlanSuggestedList.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(ExercisePlanSuggested exercisePlanSuggested);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvStaff;

        ViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvStaff = (TextView) view.findViewById(R.id.tv_staff);
        }
    }
}
