package io.github.pengrad.uw_android_dropbox.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

/**
 * stas
 * 8/25/15
 */
public class ChooseImageManager {

    public static final int REQUEST_CHOOSE_IMAGE = 1;

    public void startChooseImage(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
    }

    public String getChoosedImagePath(Context context, Intent data) {
        if (data == null) {
            return null;
        }
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imagePath = cursor.getString(columnIndex);
        cursor.close();
        Log.d("LoadImage", imagePath == null ? "null" : imagePath);
        return imagePath;
    }

}
