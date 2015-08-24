package io.github.pengrad.uw_android_dropbox;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddImagesActivity extends AppCompatActivity {

    @Bind(R.id.jobNumber) EditText mEditJobNumber;
    @Bind(R.id.clientName) EditText mEditClientName;
    @Bind(R.id.listview) ListView mListView;

    private ImageListAdapter mAdapter;
    private TakePhotoManager mTakePhotoManager;
    private ChooseImageManager mChooseImageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_images);

        ButterKnife.bind(this);

        initActionBar();

        mTakePhotoManager = new TakePhotoManager();
        mChooseImageManager = new ChooseImageManager();

        mAdapter = new ImageListAdapter(this);
        mListView.setAdapter(mAdapter);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ChooseImageManager.REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            String picturePath = mChooseImageManager.getChoosedImagePath(this, data);


            Glide.with(this).load(picturePath).asBitmap().override(1000, 1000).fitCenter().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
//                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                    File file = new File(dir, new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date()) + ".jpg");
                    Log.d("Glide File Created", file.getAbsolutePath());
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                        fos.flush();
                        fos.close();
                        processNewImage(file.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (requestCode == TakePhotoManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            String photoPath = mTakePhotoManager.getLastTakedPhoto();
            mTakePhotoManager.postPhotoToGallery(this, photoPath);
        }
    }

    private void processNewImage(String picturePath) {
        mAdapter.addImage(new ImageTimestamp(picturePath));
    }

    @OnClick(R.id.buttonAddImage)
    void addImage() {
//        mChooseImageManager.startChooseImage(this);
        mTakePhotoManager.startTakePhoto(this);
    }


    @OnClick(R.id.buttonUpload)
    void uploadImages() {
        String jobNumber = mEditJobNumber.getText().toString();
        String client = mEditClientName.getText().toString();
        if (TextUtils.isEmpty(jobNumber) || TextUtils.isEmpty(client)) {
            Toast.makeText(this, "Füllen Auftragsnummer und Name des Kunden", Toast.LENGTH_SHORT).show();
            return;
        }

        Job job = new Job(jobNumber, client, mAdapter.getImages());
        DropboxIntentService.startUploadJob(this, job);
        Toast.makeText(getApplicationContext(), "Auftragsnummer " + jobNumber + " begann upload", Toast.LENGTH_SHORT).show();
        finish();
    }
}