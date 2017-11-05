package org.hugoandrade.gymapp.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hugoandrade.gymapp.R;
import org.hugoandrade.gymapp.data.ExercisePlanRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExercisePlanReportListAdapter extends RecyclerView.Adapter<ExercisePlanReportListAdapter.ViewHolder> {

    /**
     * Date format used throughout this instance
     */
    private final static SimpleDateFormat dateFormat
            = new SimpleDateFormat("dd MMM YYYY", Locale.getDefault());

    private final List<ExercisePlanRecord> mExercisePlanRecordList;

    private OnItemClickListener mListener;

    public ExercisePlanReportListAdapter() {
        mExercisePlanRecordList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater vi = LayoutInflater.from(viewGroup.getContext());
        View v = vi.inflate(R.layout.list_item_exercise_plan_report, viewGroup, false);
        return new ExercisePlanReportListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        final ExercisePlanRecord exercisePlanRecord = mExercisePlanRecordList.get(holder.getAdapterPosition());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onItemClicked(exercisePlanRecord);
            }
        });
        holder.tvName.setText("Exercise Plan");
        holder.tvDatetime.setText(dateFormat.format(exercisePlanRecord.getDatetime().getTime()));
    }

    @Override
    public int getItemCount() {
        return mExercisePlanRecordList.size();
    }

    public void setAll(List<ExercisePlanRecord> userList) {
        mExercisePlanRecordList.clear();
        mExercisePlanRecordList.addAll(userList);
        notifyDataSetChanged();
    }

    public void add(ExercisePlanRecord exercisePlanRecord) {
        mExercisePlanRecordList.add(exercisePlanRecord);
        notifyItemInserted(mExercisePlanRecordList.size() - 1);
    }

    public void setOnItemClickedListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(ExercisePlanRecord exercisePlanRecord);
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
