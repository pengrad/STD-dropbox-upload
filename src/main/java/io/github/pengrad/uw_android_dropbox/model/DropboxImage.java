package io.github.pengrad.uw_android_dropbox.model;

import java.io.Serializable;

import co.uk.rushorm.core.RushObject;

/**
 * stas
 * 8/24/15
 */
public class DropboxImage extends RushObject implements Serializable {

    private String imagePath;
    private String dropboxPath;

    public DropboxImage() {
    }

    public DropboxImage(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDropboxPath() {
        return dropboxPath;
    }

    public void setDropboxPath(String dropboxPath) {
        this.dropboxPath = dropboxPath;
    }

    @Override
    public String toString() {
        return "imagePath:" + imagePath + " dropboxPath:" + dropboxPath;
    }
}
