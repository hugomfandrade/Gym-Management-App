package org.hugoandrade.gymapp.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExercisePlanBuilderDialog {

    @SuppressWarnings("unused")
    private static final String TAG = ExercisePlanBuilderDialog.class.getSimpleName();

    private Context mContext;
    private final List<Exercise> mExerciseList;

    private View tvSet;
    private ExerciseListAdapter mAdapter;
    private android.app.AlertDialog alert;
    private OnSaveClickedListener mOnSaveClickedListener;

    public ExercisePlanBuilderDialog(Context context, List<Exercise> exerciseList) {
        mContext = context;

        mExerciseList = exerciseList;

        buildPlan();
    }

    private void buildPlan() {

        View dialogView = View.inflate(mContext, R.layout.dialog_exercise_plan_builder, null);

        RecyclerView rvExercises = (RecyclerView) dialogView.findViewById(R.id.rv_exercise);
        rvExercises.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        mAdapter = new ExerciseListAdapter(mExerciseList);
        mAdapter.setOnItemChangedListener(new ExerciseListAdapter.OnItemChangedListener() {
            @Override
            public void onItemChanged(Exercise exercise) {
                updateSaveButton();
            }
        });
        rvExercises.setAdapter(mAdapter);
        View tvCancel = dialogView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        tvSet = dialogView.findViewById(R.id.tv_set);
        tvSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                alert.dismiss();
            }
        });

        // Initialize and build the AlertBuilderDialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext)
                .setView(dialogView);
        alert = builder.create();

        updateSaveButton();
    }

    private void save() {
        if (mOnSaveClickedListener != null)
            mOnSaveClickedListener.onSaveClicked(mAdapter.getSelectedExercise());
    }

    private void updateSaveButton() {
        if (mAdapter.getSelectedExercise().size() == 0)
            tvSet.setEnabled(false);
        else
            tvSet.setEnabled(true);
    }

    public void show() {
        alert.show();
    }

    public void setOnSaveClickedListener(OnSaveClickedListener onSaveClickedListener) {
        mOnSaveClickedListener = onSaveClickedListener;
    }

    public interface OnSaveClickedListener {
        void onSaveClicked(List<Exercise> exerciseList);
    }

    private static class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.ViewHolder> {

        private final List<MExercise> mExerciseList;
        private OnItemChangedListener mOnItemChangedListener;

        ExerciseListAdapter(List<Exercise> exerciseList) {
            mExerciseList = new ArrayList<>();
            for (Exercise exercise : exerciseList)
                mExerciseList.add(new MExercise(exercise));
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater vi = LayoutInflater.from(parent.getContext());
            return new ViewHolder(vi.inflate(R.layout.list_item_add_exercise, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final MExercise exercise = mExerciseList.get(holder.getAdapterPosition());

            holder.tvExerciseName.setText(exercise.getName());
            holder.tvAdd.setBackgroundColor(exercise.isSelected?
                    ContextCompat.getColor(holder.tvAdd.getContext(), R.color.colorPrimary):
                    Color.TRANSPARENT
            );
            holder.tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exercise.isSelected = !exercise.isSelected;
                    notifyItemChanged(holder.getAdapterPosition());

                    if (mOnItemChangedListener != null)
                        mOnItemChangedListener.onItemChanged(exercise);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mExerciseList.size();
        }

        void setOnItemChangedListener(@Nullable OnItemChangedListener listener) {
            mOnItemChangedListener = listener;
        }

        List<Exercise> getSelectedExercise() {
            List<Exercise> selectedExerciseList = new ArrayList<>();
            for (MExercise exercise : mExerciseList)
                if (exercise.isSelected)
                    selectedExerciseList.add(exercise);
            return selectedExerciseList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvExerciseName;
            TextView tvAdd;

            ViewHolder(View itemView) {
                super(itemView);
                tvExerciseName = (TextView) itemView.findViewById(R.id.tv_exercise_name);
                tvAdd = (TextView) itemView.findViewById(R.id.tv_add);
            }
        }

        private class MExercise extends Exercise {

            private boolean isSelected;

            MExercise(Exercise exercise) {
                super(exercise.getID(), exercise.getName());

                isSelected = false;
            }
        }

        interface OnItemChangedListener {
            void onItemChanged(Exercise exercise);
        }
    }
}
