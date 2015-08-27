package io.github.pengrad.uw_android_dropbox.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.uk.rushorm.core.RushSearch;
import co.uk.rushorm.core.RushSearchCallback;
import io.github.pengrad.uw_android_dropbox.MyApp;
import io.github.pengrad.uw_android_dropbox.R;
import io.github.pengrad.uw_android_dropbox.model.Job;

public class MainActivity extends AppCompatActivity {

    private MyApp app;

    @Bind(R.id.linkDropbox) TextView mTextView;
    @Bind(R.id.buttonStatus) Button mButtonStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        app = MyApp.get(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_unlink_dropbox) {
            unlinkDropbox();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        completeDropboxAuth();
        updateDropboxStatus();
        updateErrorJobs();
    }

    private void completeDropboxAuth() {
        DropboxAPI<AndroidAuthSession> dropboxAPI = app.getDropboxApi();
        if (dropboxAPI.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                dropboxAPI.getSession().finishAuthentication();
                String accessToken = dropboxAPI.getSession().getOAuth2AccessToken();
                app.saveDropboxAuth(accessToken);
                Log.d("DbAuthLog", "Success " + accessToken);
            } catch (IllegalStateException e) {
                Log.d("++++", "Error authenticating", e);
            }
        }
    }

    private void updateDropboxStatus() {
        if (isDropboxLinked()) {
            mTextView.setText("Dropbox ist OK");
            mTextView.setTextColor(getResources().getColor(R.color.green));
        } else {
            mTextView.setText("Link Dropbox");
            mTextView.setTextColor(getResources().getColor(R.color.blue));
        }
    }

    private void updateErrorJobs() {
        new RushSearch().whereEqual("status", Job.STATUS_ERROR).find(Job.class, new RushSearchCallback<Job>() {
            public void complete(final List<Job> list) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        updateButtonStatus(list.size() > 0);
                    }
                });
            }
        });
    }

    private void updateButtonStatus(boolean hasErrors) {
        Drawable ok = getResources().getDrawable(R.drawable.ic_warning_green_24dp);
        Drawable error = getResources().getDrawable(R.drawable.ic_warning_red_900_24dp);
        mButtonStatus.setCompoundDrawablesWithIntrinsicBounds(hasErrors ? error : ok, null, null, null);
    }

    @OnClick(R.id.linkDropbox)
    void linkDropbox() {
        if (!isDropboxLinked()) {
            app.getDropboxApi().getSession().startOAuth2Authentication(this);
        }
    }

    @OnClick(R.id.addImageSet)
    void openAddImagesActivity() {
        if (isDropboxLinked()) {
            startActivity(new Intent(this, JobPostActivity.class));
        } else {
            Toast.makeText(this, "Link Dropbox Zuerst", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.buttonStatus)
    void openStatusActivity() {
        startActivity(new Intent(this, StatusActivity.class));
    }

    boolean isDropboxLinked() {
        return app.isDropboxLinked();
    }

    private void unlinkDropbox() {
        if (isDropboxLinked()) {
            app.unlinkDropbox();
            updateDropboxStatus();
        }
    }
}
