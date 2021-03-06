package com.akbari.myapplication.jobapp.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akbari.myapplication.jobapp.activity.MainActivity;
import com.akbari.myapplication.jobapp.dialogFragment.LongClickDialogFragment;
import com.akbari.myapplication.jobapp.interfaces.ItemClickListener;
import com.akbari.myapplication.jobapp.interfaces.ItemLongClickListener;
import com.akbari.myapplication.jobapp.model.Job;
import com.akbari.myapplication.jobapp.viewHolder.RecyclerViewHolder;

import java.util.Collections;
import java.util.List;

import com.akbari.myapplication.jobapp.R;

/**
 * @author n.akbari
 * @since 06/12/2016
 * @version 1.0
 */
public class CustomRecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private static List<Job> jobs = Collections.emptyList();
    private Fragment fragment;

    public CustomRecyclerAdapter(List<Job> jobs, Fragment fragment) {
        this.jobs = jobs;
        this.fragment = fragment;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.list_item, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder viewHolder, final int position) {
        if (jobs.size() != 0) {
            viewHolder.jobTitle.setText(jobs.get(position).getJobName());
            viewHolder.payDay.setText(jobs.get(position).getPayDay().toString());
            viewHolder.setClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    intent.putExtra("payDay", jobs.get(position).getPayDay().toString());
                    intent.putExtra("selectedJob", jobs.get(position).getJobName());
                    view.getContext().startActivity(intent);
                }
            });
            viewHolder.setLongClickListener(new ItemLongClickListener() {
                @Override
                public boolean onLongClick(View view, int position, boolean isLongClick) {
                    String payDay = jobs.get(position).getPayDay().toString();
                    String jobName = jobs.get(position).getJobName();
                    showOnLongClickDialog(payDay, jobName, position);
                    return true;
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public void addItem(int position) {
        notifyItemInserted(position);
    }

    public void deleteItem(int position) {
        jobs.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void updateList(List<Job> data) {
        jobs = data;
        notifyDataSetChanged();
    }

    private void showOnLongClickDialog(String payDay, String jobName, int position) {
        LongClickDialogFragment longClickDialogFragment = new LongClickDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("selectedJob", jobName);
        bundle.putString("payDay", payDay);
        bundle.putInt("position", position);
        longClickDialogFragment.setTargetFragment(fragment, 0);
        longClickDialogFragment.setArguments(bundle);
        longClickDialogFragment.show(fragment.getFragmentManager(), "LongClickDialog");
    }

}
