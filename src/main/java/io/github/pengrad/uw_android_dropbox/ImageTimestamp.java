package io.github.pengrad.uw_android_dropbox;

import java.io.Serializable;
import java.util.Date;

/**
 * stas
 * 8/24/15
 */
public class ImageTimestamp implements Serializable {

    public final String imagePath;
    public final long millis;

    public ImageTimestamp(String imagePath) {
        this.imagePath = imagePath;
        this.millis = new Date().getTime();
    }
}
