package org.hugoandrade.gymapp.view.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.ExercisePlanRecord;
import org.hugoandrade.gymapp.data.ExerciseSet;
import org.hugoandrade.gymapp.view.helper.ItemTouchHelperAdapter;
import org.hugoandrade.gymapp.view.helper.SimpleItemTouchHelperCallback;

import java.util.Collections;

public class ExerciseSetListAdapter extends RecyclerView.Adapter<ExerciseSetListAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    @SuppressWarnings("unused")
    private final String TAG = getClass().getSimpleName();

    public final static int MODE_EDIT = 1;
    public final static int MODE_DISPLAY = 2;

    private ItemTouchHelper mItemTouchHelper;

    private final int mMode;

    private ExercisePlanRecord mExercisePlan;

    public ExerciseSetListAdapter(int mode, ExercisePlanRecord exercisePlan) {
        mMode = mode;
        mExercisePlan = exercisePlan;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        return new ViewHolder(vi.inflate(R.layout.list_item_exercise_set, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ExerciseSet exerciseSet = mExercisePlan.getExerciseSetList().get(holder.getAdapterPosition());
        exerciseSet.setExercisePlanRecordOrder(holder.getAdapterPosition() + 1);

        holder.tvExerciseName.setText(TextUtils.concat(
                String.valueOf(exerciseSet.getExercisePlanRecordOrder()),
                ". ",
                exerciseSet.getExercise().getName()));

        ExerciseRecordListAdapter mExerciseRecordListAdapter
                = new ExerciseRecordListAdapter(mMode, exerciseSet.getExerciseRecordList());
        mExerciseRecordListAdapter.setOnAllItemsRemovedListener(new ExerciseRecordListAdapter.OnAllItemsRemovedListener() {
            @Override
            public void onItemsRemoved() {
                for (int i = holder.getAdapterPosition() + 1; i < mExercisePlan.getExerciseSetList().size(); i++) {
                    notifyItemChanged(i);
                }

                mExercisePlan.getExerciseSetList().remove(exerciseSet);
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
        holder.rvExerciseRecord.setHasFixedSize(true);
        holder.rvExerciseRecord.setAdapter(mExerciseRecordListAdapter);
        holder.rvExerciseRecord.setLayoutManager(
                new LinearLayoutManager(holder.rvExerciseRecord.getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false));


        holder.ivReorder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemTouchHelper != null) {
                    mItemTouchHelper.startDrag(holder);
                }
                return false;
            }
        });

        if (mMode == MODE_EDIT)
            holder.ivReorder.setVisibility(View.VISIBLE);
        else
            holder.ivReorder.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if (mExercisePlan == null)
            return 0;
        return mExercisePlan.getExerciseSetList().size();
    }

    public void set(ExercisePlanRecord exercisePlan) {
        mExercisePlan = exercisePlan;
        notifyDataSetChanged();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

        for (int i = 0 ; i < Math.abs(fromPosition - toPosition) ; i++) {
            int multiplier = (fromPosition < toPosition) ? 1 : -1;
            Collections.swap(
                    mExercisePlan.getExerciseSetList(),
                    fromPosition + i * multiplier,
                    fromPosition + i * multiplier + multiplier);
            notifyItemMoved(
                    fromPosition + i * multiplier,
                    fromPosition + i * multiplier + multiplier);

        }
    }

    @Override
    public void onItemDropped(int position) {
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivReorder;
        TextView tvExerciseName;
        RecyclerView rvExerciseRecord;

        ViewHolder(View itemView) {
            super(itemView);

            ivReorder = (ImageView) itemView.findViewById(R.id.iv_reorder);
            tvExerciseName = (TextView) itemView.findViewById(R.id.tv_exercise_name);
            rvExerciseRecord = (RecyclerView) itemView.findViewById(R.id.rv_exercise_record);
        }
    }
}
