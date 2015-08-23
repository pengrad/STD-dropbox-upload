package io.github.pengrad.uw_android_dropbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.addImageSet)
    void openAddImagesActivity() {
        startActivity(new Intent(this, AddImagesActivity.class));
    }

    @OnClick(R.id.buttonStatus)
    void openStatusActivity() {

    }
}
