package io.github.pengrad.uw_android_dropbox.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import io.github.pengrad.uw_android_dropbox.Utils;

/**
 * stas
 * 8/25/15
 */
public class ImageResizer {

    public static final int IMAGE_SIZE = 1000;
    public static final int JPEG_QUALITY = 80;

    private WeakReference<OnProcessedImageListener> listener;

    public ImageResizer(OnProcessedImageListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    public void resizeImage(final Context context, String imagePath) {
        Glide.with(context).load(imagePath).asBitmap().override(IMAGE_SIZE, IMAGE_SIZE).fitCenter()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        File file = new File(dir, Utils.getFileNameByDate() + ".jpg");
                        Log.d("Glide File Created", file.getAbsolutePath());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, fos);
                            fos.close();
                            OnProcessedImageListener imageListener = listener.get();
                            if (imageListener != null) {
                                imageListener.onProcessedImage(file.getAbsolutePath());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    public interface OnProcessedImageListener {
        void onProcessedImage(String imagePath);
    }

}
