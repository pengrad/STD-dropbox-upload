package io.github.pengrad.uw_android_dropbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private MyApp app;

    @Bind(R.id.linkDropbox) TextView mTextView;

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
        if(item.getItemId() == R.id.menu_unlink_dropbox) {
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
        if(isDropboxLinked()) {
            mTextView.setText("Dropbox ist OK");
            mTextView.setTextColor(getResources().getColor(R.color.green));
        } else {
            mTextView.setText("Link Dropbox Zuerst");
            mTextView.setTextColor(getResources().getColor(R.color.blue));
        }
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
            startActivity(new Intent(this, AddImagesActivity.class));
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
        if(isDropboxLinked()) {
            app.unlinkDropbox();
            updateDropboxStatus();
        }
    }
}
