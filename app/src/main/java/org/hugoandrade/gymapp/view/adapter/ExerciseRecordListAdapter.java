package org.hugoandrade.gymapp.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.ExerciseRecord;
import org.hugoandrade.gymapp.view.dialog.ExerciseRepsPopup;

import java.util.List;

public class ExerciseRecordListAdapter extends RecyclerView.Adapter<ExerciseRecordListAdapter.ViewHolder> {

    private final List<ExerciseRecord> mExerciseList;

    private final int mMode;

    private OnAllItemsRemovedListener mListener;

    public ExerciseRecordListAdapter(int mode, List<ExerciseRecord> exerciseList) {
        mMode = mode;
        mExerciseList = exerciseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        View v = vi.inflate(R.layout.list_item_exercise_record, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int i) {
        if (holder.getAdapterPosition() == mExerciseList.size()) {
            holder.mAddExerciseRecord.setVisibility(View.VISIBLE);
            holder.mAddExerciseRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExerciseList.add(new ExerciseRecord(10, holder.getAdapterPosition() + 1));
                    notifyItemInserted(holder.getAdapterPosition());
                }
            });
            return;
        }
        final ExerciseRecord exerciseRecord = mExerciseList.get(holder.getAdapterPosition());
        mExerciseList.get(i).setExerciseSetOrder(holder.getAdapterPosition() +1);

        holder.mAddExerciseRecord.setVisibility(View.GONE);
        holder.tvNumberOfRepetitions.setText(String.valueOf(exerciseRecord.getNumberOfRepetitions()));
        holder.tvSetNumber.setText(TextUtils.concat(
                "Set ",
                String.valueOf(mExerciseList.get(i).getExerciseSetOrder())));
        if (mMode == ExerciseSetListAdapter.MODE_EDIT) {
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = holder.getAdapterPosition() + 1; i < mExerciseList.size(); i++) {
                        notifyItemChanged(i);
                    }

                    mExerciseList.remove(exerciseRecord);
                    notifyItemRemoved(holder.getAdapterPosition());

                    if (mExerciseList.size() == 0 && mListener != null)
                        mListener.onItemsRemoved();
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExerciseRepsPopup popup = new ExerciseRepsPopup(holder.itemView, holder.getAdapterPosition(), exerciseRecord);
                    popup.setOnExerciseRepsPopupListener(new ExerciseRepsPopup.OnExerciseRepsPopupListener() {
                        @Override
                        public void onDismiss(int position, ExerciseRecord exercise, int numberOfRepetitions) {
                            exerciseRecord.setNumberOfRepetitions(numberOfRepetitions);
                            notifyItemChanged(position);
                        }
                    });
                }
            });
        }
        else {
            holder.ivDelete.setVisibility(View.GONE);
            holder.ivDelete.setOnClickListener(null);
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        if (mMode == ExerciseSetListAdapter.MODE_EDIT)
            return mExerciseList.size() + 1;

        return mExerciseList.size();
    }

    void setOnAllItemsRemovedListener(OnAllItemsRemovedListener listener) {
        mListener = listener;
    }

    public interface OnAllItemsRemovedListener {
        void onItemsRemoved();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumberOfRepetitions;
        TextView tvSetNumber;
        ImageView ivDelete;
        View mAddExerciseRecord;

        ViewHolder(View view) {
            super(view);
            tvNumberOfRepetitions = (TextView) view.findViewById(R.id.tv_exercise_repetitions);
            tvSetNumber = (TextView) view.findViewById(R.id.tv_exercise_set);
            ivDelete = (ImageView) view.findViewById(R.id.iv_delete_exercise_record);
            mAddExerciseRecord = view.findViewById(R.id.viewgroup_add_exercise_record);
        }
    }
}
