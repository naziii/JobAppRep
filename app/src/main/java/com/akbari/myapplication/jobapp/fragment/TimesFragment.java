package com.akbari.myapplication.jobapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.akbari.myapplication.jobapp.R;
import com.akbari.myapplication.jobapp.activity.JobActivity;
import com.akbari.myapplication.jobapp.adapter.TimeRecyclerAdapter;
import com.akbari.myapplication.jobapp.dao.JobDao;
import com.akbari.myapplication.jobapp.dao.TimeDao;
import com.akbari.myapplication.jobapp.decoration.CustomDividerItemDecoration;
import com.akbari.myapplication.jobapp.dialogFragment.DeleteTimeAlertDialogFragment;
import com.akbari.myapplication.jobapp.interfaces.TimeClickListener;
import com.akbari.myapplication.jobapp.model.Job;
import com.akbari.myapplication.jobapp.model.JobTime;
import com.akbari.myapplication.jobapp.model.Time;
import com.akbari.myapplication.jobapp.utils.DateUtil;
import com.akbari.myapplication.jobapp.utils.Month;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.PersianCalendar;

import java.util.ArrayList;
import java.util.List;

public class TimesFragment extends Fragment implements TimeClickListener {

    private List<Time> times = new ArrayList<>();
    private TimeRecyclerAdapter mAdapter;
    private Spinner monthSpinner;
    private EditText year;
    private View view;
    private TextView sumHour;
    private String jobId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_job_month_hour, container, false);
        setHasOptionsMenu(true);
        Button viewDetail = (Button) view.findViewById(R.id.view_detail);
        monthSpinner = (Spinner) view.findViewById(R.id.month_choose);
        year = (EditText) view.findViewById(R.id.year_choose);
        sumHour = (TextView) view.findViewById(R.id.sum_hour);
        PersianCalendar calendar = new PersianCalendar();
        year.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        monthSpinner.setSelection(calendar.get(Calendar.MONTH));
        System.out.println(calendar.get(Calendar.MONTH));
        final TimesFragment timesFragment = this;
        jobId = getArguments().getString("jobId");
        viewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JobDao jobDao = new JobDao();
                TimeDao timeDao = new TimeDao();
                Job job = jobDao.findJobById(getContext(), jobId);
                JobTime jobTime = getJobTime(job);
                setSumText(timeDao, jobTime);
                times.addAll(timeDao.getTimesInDateRange(getContext(), jobTime));
                RecyclerView mRecyclerView = (RecyclerView) view.
                        findViewById(R.id.detailRecycler);
                mRecyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager mLayoutManager = new
                        LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mAdapter = new TimeRecyclerAdapter(times, timesFragment);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.addItemDecoration(new
                        CustomDividerItemDecoration(getContext()));
                monthSpinner.setSelection(0);
                year.getText().clear();
            }
        });
        return view;
    }

    private void setSumText(TimeDao timeDao, JobTime jobTime) {
        StringBuilder sumText = new StringBuilder();
        sumText.append("  ");
        int sumMinute = timeDao.getHourInDateRange(getContext(), jobTime);
        int sum = sumMinute / 60;
        sumText.append(sum);
        sumText.append(" ساعت و ");
        int minute = sumMinute % 60;
        sumText.append(minute);
        sumText.append(" دقیقه ");
        sumHour.setText(sumText);
    }

    private JobTime getJobTime(Job job) {
        int month = (int) monthSpinner.getSelectedItemId();
        int yearEnter = Integer.valueOf(year.getText().toString());
        JobTime jobTime = new JobTime();
        jobTime.setPayDay(job.getPayDay());
        jobTime.setJobId(job.getId());
        PersianCalendar calendar = new PersianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, jobTime.getPayDay());
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, yearEnter);
        jobTime.setDateTo(DateUtil.computeDateString(calendar));
        calendar.add(Calendar.MONTH, -1);
        jobTime.setDateFrom(DateUtil.computeDateString(calendar));
        return jobTime;
    }

    @Override
    public void OnSelectRemoveButton(String timeId, int position) {
        DeleteTimeAlertDialogFragment fragment = new DeleteTimeAlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("timeId", timeId);
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        fragment.setTargetFragment(this, 0);
        fragment.show(getFragmentManager(), "Alert");
    }

    @Override
    public void OnRemoveItem(String timeId, int position) {
        TimeDao timeDao = new TimeDao();
        timeDao.removeTime(getContext(), timeId);
        mAdapter.deleteItem(position);
    }

    @Override
    public void OnSelectEditButton(String timeId) {
        Bundle bundle = new Bundle();
        bundle.putString("timeId", timeId);
        bundle.putString("jobId", "");
        AddTimeFragment fragment =
                new AddTimeFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction tr = fragmentManager.beginTransaction();
        tr.replace(R.id.job_holder, fragment);
        tr.commit();
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_back, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                Intent intent = new Intent(getActivity(), JobActivity.class);
                intent.putExtra("jobId", jobId);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
