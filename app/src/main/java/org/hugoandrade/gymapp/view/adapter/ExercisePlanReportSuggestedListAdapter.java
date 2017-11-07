package org.hugoandrade.gymapp.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.ExercisePlanRecordSuggested;

import java.util.ArrayList;
import java.util.List;

public class ExercisePlanReportSuggestedListAdapter extends RecyclerView.Adapter<ExercisePlanReportSuggestedListAdapter.ViewHolder> {


    private final List<ExercisePlanRecordSuggested> mExercisePlanRecordList;

    private OnItemClickListener mListener;

    public ExercisePlanReportSuggestedListAdapter() {
        mExercisePlanRecordList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        View v = vi.inflate(R.layout.list_item_exercise_plan_report_suggested, viewGroup, false);
        return new ExercisePlanReportSuggestedListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        final ExercisePlanRecordSuggested exercisePlanRecordSuggested
                = mExercisePlanRecordList.get(holder.getAdapterPosition());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onItemClicked(exercisePlanRecordSuggested);
            }
        });
        holder.tvName.setText("Exercise Plan");
        holder.tvStaff.setText(exercisePlanRecordSuggested.getStaff().getUsername());
    }

    @Override
    public int getItemCount() {
        return mExercisePlanRecordList.size();
    }

    public void setAll(List<ExercisePlanRecordSuggested> suggestedList) {
        mExercisePlanRecordList.clear();
        mExercisePlanRecordList.addAll(suggestedList);
        notifyDataSetChanged();
    }

    public void add(ExercisePlanRecordSuggested exercisePlanRecordSuggested) {
        mExercisePlanRecordList.add(exercisePlanRecordSuggested);
        notifyItemInserted(mExercisePlanRecordList.size() - 1);
    }

    public void setOnItemClickedListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void remove(ExercisePlanRecordSuggested exercisePlanRecordSuggested) {
        for (int i = 0 ; i < mExercisePlanRecordList.size() ; i++) {
            if (exercisePlanRecordSuggested.getID() != null &&
                    exercisePlanRecordSuggested.getID().equals(mExercisePlanRecordList.get(i).getID())) {
                mExercisePlanRecordList.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(ExercisePlanRecordSuggested exercisePlanRecordSuggested);
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
