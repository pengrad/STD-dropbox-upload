package io.github.pengrad.uw_android_dropbox.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.GridView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.pengrad.uw_android_dropbox.JobDeleteIntentService;
import io.github.pengrad.uw_android_dropbox.R;
import io.github.pengrad.uw_android_dropbox.model.Job;

/**
 * stas
 * 8/26/15
 */
public class JobLookActivity extends AppCompatActivity {

    public static final String EXTRA_JOB = "JOB";
    public static final String EXTRA_JOB_ID = "JOB_ID";
    private String mJobId;

    public static Intent newIntent(Context context, Job job) {
        return new Intent().putExtra(EXTRA_JOB, job).putExtra(EXTRA_JOB_ID, job.getId()).setClass(context, JobLookActivity.class);
    }

    @Bind(R.id.jobNumber) EditText mEditJobNumber;
    @Bind(R.id.tankId) EditText mEditTankId;
    @Bind(R.id.clientName) EditText mEditClientName;
    @Bind(R.id.gridview) GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_job);
        ButterKnife.bind(this);
        initActionBar();

        mJobId = getIntent().getStringExtra(EXTRA_JOB_ID);
        Job job = (Job) getIntent().getSerializableExtra(EXTRA_JOB);
        mEditJobNumber.setText(job.getJobNumber());
        mEditTankId.setText(job.getTankId());
        mEditClientName.setText(job.getClient());
        mGridView.setAdapter(new ImageGridAdapter(this, job.getImages()));
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

    @OnClick(R.id.buttonDelete)
    void onDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Achtung")
                .setMessage("Sind Sie sicher, Sie wollen Bilder von Dropbox entfernen?")
                .setNegativeButton("Abbrechen", null)
                .setPositiveButton("Entfernen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        JobDeleteIntentService.startActionDeleteDropbox(getApplicationContext(), mJobId);
                        finish();
                    }
                }).show();
    }
}
