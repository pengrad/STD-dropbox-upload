package io.github.pengrad.uw_android_dropbox.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import io.github.pengrad.uw_android_dropbox.R;
import io.github.pengrad.uw_android_dropbox.model.ImageTimestamp;

/**
 * stas
 * 8/25/15
 */
public class ImageListAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private List<ImageTimestamp> mImages;
    private Context mContext;

    public ImageListAdapter(Context context) {
        mContext = context;
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

        String imagePath = mImages.get(position).imagePath;
        Glide.with(mContext).load(imagePath).centerCrop().into(imageView);

        convertView.findViewById(R.id.buttonImageDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeImage(position);
            }
        });

        return convertView;
    }
}