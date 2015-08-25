package io.github.pengrad.uw_android_dropbox.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.pengrad.uw_android_dropbox.DropboxIntentService;
import io.github.pengrad.uw_android_dropbox.R;
import io.github.pengrad.uw_android_dropbox.image.ChooseImageManager;
import io.github.pengrad.uw_android_dropbox.image.ImageResizer;
import io.github.pengrad.uw_android_dropbox.image.TakePhotoManager;
import io.github.pengrad.uw_android_dropbox.model.DropboxImage;
import io.github.pengrad.uw_android_dropbox.model.Job;

public class AddImagesActivity extends AppCompatActivity implements ImageResizer.OnProcessedImageListener {

    @Bind(R.id.jobNumber) EditText mEditJobNumber;
    @Bind(R.id.clientName) EditText mEditClientName;
    @Bind(R.id.listview) ListView mListView;

    private ImageListAdapter mAdapter;
    private TakePhotoManager mTakePhotoManager;
    private ChooseImageManager mChooseImageManager;
    private ImageResizer mImageResizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_images);

        ButterKnife.bind(this);

        initActionBar();

        mTakePhotoManager = new TakePhotoManager();
        mChooseImageManager = new ChooseImageManager();
        mImageResizer = new ImageResizer(this);

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

        String imagePath = null;
        if (requestCode == ChooseImageManager.REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK) {
            imagePath = mChooseImageManager.getChoosedImagePath(this, data);
        } else if (requestCode == TakePhotoManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            imagePath = mTakePhotoManager.getLastTakedPhoto();
            mTakePhotoManager.postPhotoToGallery(this, imagePath);
        }

        if (imagePath != null) {
            mImageResizer.resizeImage(this, imagePath);
        }
    }

    @Override
    public void onProcessedImage(String imagePath) {
        mAdapter.addImage(new DropboxImage(imagePath));
    }

    @OnClick(R.id.buttonAddImage)
    void addImage() {
        final Activity activity = this;
        String[] options = new String[]{"Von der Kamera", "Aus der Galerie"};
        new AlertDialog.Builder(this)
                .setNeutralButton("Abbrechen", null)
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) mTakePhotoManager.startTakePhoto(activity);
                        else if (i == 1) mChooseImageManager.startChooseImage(activity);
                    }
                }).show();
    }

    @OnClick(R.id.buttonUpload)
    void uploadImages() {
        String jobNumber = mEditJobNumber.getText().toString();
        String client = mEditClientName.getText().toString();
        if (TextUtils.isEmpty(jobNumber) || TextUtils.isEmpty(client)) {
            Toast.makeText(this, "Füllen Sie Auftragsnummer und Name des Kunden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAdapter.getImages().size() <= 0) {
            Toast.makeText(this, "Fügen Sie Bild", Toast.LENGTH_SHORT).show();
            return;
        }

        Job job = new Job(jobNumber, client, mAdapter.getImages());
        DropboxIntentService.startUploadJob(this, job);
        Toast.makeText(getApplicationContext(), "Auftragsnummer " + jobNumber + " begann upload", Toast.LENGTH_SHORT).show();
        finish();
    }
}