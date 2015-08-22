package io.github.pengrad.uw_android_dropbox;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddImagesActivity extends AppCompatActivity {

    public static final int REQUEST_LOAD_IMAGE = 123;


    final static private String APP_KEY = BuildConfig.API_KEY;
    final static private String APP_SECRET = BuildConfig.API_SECRET;

    private DropboxAPI<AndroidAuthSession> mDBApi;

    private ImageView mImageView;
    private View mLayoutImage;
    private Button mBAddImage;
    private String mImagePath;
    private EditText mEditJobNumber;
    private EditText mEditClientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_images);

        mLayoutImage = findViewById(R.id.layoutImage);
        mBAddImage = (Button) findViewById(R.id.buttonAddImage);
        mImageView = (ImageView) findViewById(R.id.image);

        mEditJobNumber = (EditText) findViewById(R.id.jobNumber);
        mEditClientName = (EditText) findViewById(R.id.clientName);

        mBAddImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST_LOAD_IMAGE);
            }
        });

        findViewById(R.id.buttonImageDelete).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mImageView.setImageBitmap(null);
                mLayoutImage.setVisibility(View.GONE);
                mBAddImage.setVisibility(View.VISIBLE);
            }
        });


        findViewById(R.id.buttonUpload).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!mDBApi.getSession().authenticationSuccessful()) {
                    mDBApi.getSession().startOAuth2Authentication(AddImagesActivity.this);
                    return;
                }


                new AsyncTask<Void, Object, Object>() {
                    @Override
                    protected void onPreExecute() {
                        Toast.makeText(getApplicationContext(), "Starting upload to dropbox", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected Object doInBackground(Void... params) {
                        try {
                            File file = new File(mImagePath);
                            FileInputStream inputStream = new FileInputStream(file);
                            long millis = System.currentTimeMillis();
                            DropboxAPI.Entry response = mDBApi.putFile(getFileName(mImagePath), inputStream, file.length(), null, null);
                            Log.d("DbExampleLog", "The uploaded file's rev is: " + response.rev);
                        } catch (Exception e) {
                            Log.d("++++", "Upload error", e);
                            Toast.makeText(getApplicationContext(), "Upload error, do you have internet?", Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object object) {
                        Toast.makeText(getApplicationContext(), "Uploaded, check you dropbox!", Toast.LENGTH_SHORT).show();
                    }
                }.execute();

            }
        });

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                Log.d("DbAuthLog", "Success " + accessToken);
                Toast.makeText(this, "Ok, you can upload image now", Toast.LENGTH_SHORT).show();
            } catch (IllegalStateException e) {
                Log.d("++++", "Error authenticating", e);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            mImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            mImagePath = picturePath;

            mBAddImage.setVisibility(View.GONE);
            mLayoutImage.setVisibility(View.VISIBLE);
        }
    }

    private String getFileName(String path) {
        String job = mEditJobNumber.getText().toString();
        if (TextUtils.isEmpty(job)) {
            job = "000";
        }

        String client = mEditClientName.getText().toString();
        if (TextUtils.isEmpty(client)) {
            client = "unknown";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        String timestamp = dateFormat.format(new Date());

        String extension = path.substring(path.lastIndexOf("."));

        return job + "_" + timestamp + "_" + client + extension;
    }
}
