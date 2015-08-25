package io.github.pengrad.uw_android_dropbox.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.github.pengrad.uw_android_dropbox.model.DropboxImage;

/**
 * stas
 * 8/26/15
 */
public class ImageGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<DropboxImage> images;


    public ImageGridAdapter(Context context, List<DropboxImage> images) {
        this.mContext = context;
        this.images = images;
    }

    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return images.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new SquareImageView(mContext);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        Glide.with(mContext).load(images.get(position).getImagePath()).centerCrop().into(imageView);

        return imageView;
    }
}
