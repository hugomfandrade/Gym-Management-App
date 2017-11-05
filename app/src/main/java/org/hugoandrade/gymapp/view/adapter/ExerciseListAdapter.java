package org.hugoandrade.gymapp.view.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.Exercise;
import org.hugoandrade.gymapp.data.User;

import java.util.ArrayList;
import java.util.List;

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.ViewHolder> {

    private final List<Exercise> mExerciseList;

    private OnItemClickListener mListener;

    public ExerciseListAdapter() {
        mExerciseList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        View v = vi.inflate(R.layout.list_item_exercise, viewGroup, false);
        return new ExerciseListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        final Exercise exercise = mExerciseList.get(holder.getAdapterPosition());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onItemClicked(exercise);
            }
        });
        holder.tvExerciseName.setText(exercise.getName());
    }

    @Override
    public int getItemCount() {
        return mExerciseList.size();
    }

    public void setAll(List<Exercise> exerciseList) {
        mExerciseList.clear();
        mExerciseList.addAll(exerciseList);
        notifyDataSetChanged();
    }

    public void add(Exercise exercise) {
        mExerciseList.add(exercise);
        notifyItemInserted(mExerciseList.size() - 1);
    }

    public void setOnItemClickedListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(Exercise exercise);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName;

        ViewHolder(View view) {
            super(view);
            tvExerciseName = (TextView) view.findViewById(R.id.tv_exercise_name);
        }
    }
}
