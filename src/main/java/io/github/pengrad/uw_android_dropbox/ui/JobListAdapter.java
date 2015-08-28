package io.github.pengrad.uw_android_dropbox.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.pengrad.uw_android_dropbox.R;
import io.github.pengrad.uw_android_dropbox.UploadJobIntentService;
import io.github.pengrad.uw_android_dropbox.model.Job;

/**
 * stas
 * 8/26/15
 */
public class JobListAdapter extends BaseAdapter {

    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    private final LayoutInflater mInflater;
    private List<Job> mJobs;
    private Context mContext;

    public JobListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mJobs = new ArrayList<>();
    }

    public void setJobs(List<Job> jobs) {
        mJobs = jobs;
        notifyDataSetChanged();
    }

    public void removeJob(int position) {
        mJobs.remove(position);
        notifyDataSetChanged();
    }

    public void setPendingJob(int position) {
        mJobs.get(position).setPending();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mJobs.size();
    }

    @Override
    public Job getItem(int position) {
        return mJobs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_job, parent, false);
        }
        TextView jobNumber = (TextView) convertView.findViewById(R.id.jobNumber);
        TextView tankId = (TextView) convertView.findViewById(R.id.tankId);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
        final ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.reUpload);

        final Job job = mJobs.get(position);
        int colorResource = job.isError() ? R.color.light_red : R.color.light_green;
        convertView.setBackgroundColor(mContext.getResources().getColor(colorResource));

        jobNumber.setText(job.getJobNumber());
        tankId.setText(job.getTankId());
        date.setText(FORMAT.format(job.getDate()));
        progressBar.setVisibility(job.isPending() ? View.VISIBLE : View.GONE);
        imageButton.setVisibility(job.isError() ? View.VISIBLE : View.GONE);

        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setPendingJob(position);
                UploadJobIntentService.reUploadJob(mContext.getApplicationContext(), job);
            }
        });

        return convertView;
    }
}