package io.github.pengrad.uw_android_dropbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * stas
 * 8/25/15
 */
public class TakePhotoManager {

    public static final int REQUEST_TAKE_PHOTO = 2;

    private String mTakedPhotoPath;

    public void startTakePhoto(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                Log.d("Take Photo file", photoFile.getAbsolutePath());
                mTakedPhotoPath = photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Fehler beim öffnen der Kamera", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public String getLastTakedPhoto() {
        return mTakedPhotoPath;
    }

    private File createImageFile() throws IOException {
        String timeStamp = Utils.getFileNameByDate();
        String imageFileName = "SDT_PHOTO_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    public void postPhotoToGallery(Context context, String imagePath) {
        if (imagePath == null) return;
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(new File(imagePath));
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            // why not?
        }
    }
}
