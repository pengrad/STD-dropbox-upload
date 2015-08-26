package io.github.pengrad.uw_android_dropbox.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.uk.rushorm.core.RushSearch;
import co.uk.rushorm.core.RushSearchCallback;
import io.github.pengrad.uw_android_dropbox.JobDeleteIntentService;
import io.github.pengrad.uw_android_dropbox.R;
import io.github.pengrad.uw_android_dropbox.model.Job;

public class StatusActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    @Bind(R.id.refreshLayout) SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.listview) ListView mListView;
    private JobListAdapter mJobAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        ButterKnife.bind(this);

        initActionBar();

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        mJobAdapter = new JobListAdapter(StatusActivity.this);
        mListView.setAdapter(mJobAdapter);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadJobs();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                loadJobs();
            }
        });
    }

    private void loadJobs() {
        new RushSearch().orderDesc("date").find(Job.class, new RushSearchCallback<Job>() {
            @Override
            public void complete(final List<Job> list) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mJobAdapter.setJobs(list);
                    }
                });

            }
        });
        mRefreshLayout.setRefreshing(false);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(JobLookActivity.newIntent(this, mJobAdapter.getItem(position)));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final Job job = mJobAdapter.getItem(position);

        new AlertDialog.Builder(this)
                .setMessage("Entfernen aus der Liste?\nBilder werden nicht von der Dropbox löschen")
                .setNegativeButton("Abbrechen", null)
                .setPositiveButton("Entfernen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mJobAdapter.setPendingJob(position);
                        JobDeleteIntentService.startActionLocalDelete(getApplicationContext(), job.getId());
                    }
                })
                .show();

        return true;
    }
}
