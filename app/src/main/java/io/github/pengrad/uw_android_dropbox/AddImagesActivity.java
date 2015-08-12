package io.github.pengrad.uw_android_dropbox;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AddImagesActivity extends AppCompatActivity {

    public static final int REQUEST_LOAD_IMAGE = 123;

    private ImageView mImageView;
    private View mLayoutImage;
    private Button mBAddImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_images);

        mLayoutImage = findViewById(R.id.layoutImage);
        mBAddImage = (Button) findViewById(R.id.buttonAddImage);
        mImageView = (ImageView) findViewById(R.id.image);

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

            }
        });
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

            mBAddImage.setVisibility(View.GONE);
            mLayoutImage.setVisibility(View.VISIBLE);
        }
    }
}
