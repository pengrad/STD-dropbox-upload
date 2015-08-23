package io.github.pengrad.uw_android_dropbox;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddImagesActivity extends AppCompatActivity {

    public static final int REQUEST_LOAD_IMAGE = 123;

    //    @Bind(R.id.image) ImageView mImageView;
//    @Bind(R.id.layoutImage) View mLayoutImage;
    @Bind(R.id.jobNumber) EditText mEditJobNumber;
    @Bind(R.id.clientName) EditText mEditClientName;
    @Bind(R.id.listview) ListView mListView;

    private DropboxAPI<AndroidAuthSession> mDBApi;
    private String mImagePath;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_images);

        ButterKnife.bind(this);

        initActionBar();

        AppKeyPair appKeys = new AppKeyPair(BuildConfig.API_KEY, BuildConfig.API_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);

        mAdapter = new ListAdapter(this);
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

        if (requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


//            mImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//            mImagePath = picturePath;

            mAdapter.addImage(picturePath);
//            mLayoutImage.setVisibility(View.VISIBLE);
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

    @OnClick(R.id.buttonUpload)
    void uploadImages() {

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

    @OnClick(R.id.buttonAddImage)
    void addImage() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_LOAD_IMAGE);
    }

    //    @OnClick(R.id.buttonImageDelete)
    void deleteImage() {
//        mImageView.setImageBitmap(null);
//        mLayoutImage.setVisibility(View.GONE);
    }

    class ListAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;

        private List<String> mImages;

        public ListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mImages = new ArrayList<>();
        }

        public void addImage(String path) {
            mImages.add(path);
            notifyDataSetChanged();
        }

        public void removeImage(int pos) {
            mImages.remove(pos);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public Object getItem(int position) {
            return mImages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_image, parent, false);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            imageView.setImageBitmap(BitmapFactory.decodeFile(mImages.get(position)));

            convertView.findViewById(R.id.buttonImageDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeImage(position);
                }
            });

            return convertView;
        }
    }

}
