package com.akbari.myapplication.jobapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akbari.myapplication.jobapp.R;
import com.akbari.myapplication.jobapp.adapter.TimeDetailRecyclerAdapter;
import com.akbari.myapplication.jobapp.dao.TimeDao;
import com.akbari.myapplication.jobapp.decoration.CustomDividerItemDecoration;
import com.akbari.myapplication.jobapp.model.Time;

import java.util.ArrayList;
import java.util.List;

public class JobDetailHourFragment extends Fragment {

    private List<Time> times = new ArrayList<>();
    private TimeDetailRecyclerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_detail_hour, container, false);
        TimeDao timeDao = new TimeDao();
        String payDay = getArguments().getString("payDay");
        String jobTitle = getArguments().getString("title");
        times.addAll(timeDao.getMonthTimes(getContext(), payDay, jobTitle));
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.detailRecycler);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TimeDetailRecyclerAdapter(times, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new CustomDividerItemDecoration(getContext()));
        return view;
    }

}
