package io.github.pengrad.uw_android_dropbox.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.uk.rushorm.core.RushSearch;
import co.uk.rushorm.core.RushSearchCallback;
import io.github.pengrad.uw_android_dropbox.R;
import io.github.pengrad.uw_android_dropbox.model.Job;

public class StatusActivity extends AppCompatActivity {

    @Bind(R.id.listview) ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        ButterKnife.bind(this);

        initActionBar();

        new RushSearch().orderDesc("date").find(Job.class, new RushSearchCallback<Job>() {
            @Override
            public void complete(List<Job> list) {
                mListView.setAdapter(new JobListAdapter(StatusActivity.this, list));
            }
        });
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
}
