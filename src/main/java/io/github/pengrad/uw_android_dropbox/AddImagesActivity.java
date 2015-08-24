package io.github.pengrad.uw_android_dropbox;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddImagesActivity extends AppCompatActivity {

    public static final int REQUEST_LOAD_IMAGE = 123;

    @Bind(R.id.jobNumber) EditText mEditJobNumber;
    @Bind(R.id.clientName) EditText mEditClientName;
    @Bind(R.id.listview) ListView mListView;

    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_images);

        ButterKnife.bind(this);

        initActionBar();

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
            processNewImage(picturePath);
        }
    }

    private void processNewImage(String picturePath) {
        mAdapter.addImage(new ImageTimestamp(picturePath));
    }

    @OnClick(R.id.buttonAddImage)
    void addImage() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_LOAD_IMAGE);
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
        Toast.makeText(getApplicationContext(),"Auftragsnummer " + jobNumber + " begann upload", Toast.LENGTH_SHORT).show();
        finish();
    }

    class ListAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;
        private List<ImageTimestamp> mImages;

        public ListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mImages = new ArrayList<>();
        }

        public void addImage(ImageTimestamp image) {
            mImages.add(image);
            notifyDataSetChanged();
        }

        public void removeImage(int pos) {
            mImages.remove(pos);
            notifyDataSetChanged();
        }

        public List<ImageTimestamp> getImages() {
            return mImages;
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
            imageView.setImageBitmap(BitmapFactory.decodeFile(mImages.get(position).imagePath));

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