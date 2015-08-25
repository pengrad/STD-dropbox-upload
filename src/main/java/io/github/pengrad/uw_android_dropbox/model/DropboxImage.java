package io.github.pengrad.uw_android_dropbox.model;

import java.io.Serializable;

/**
 * stas
 * 8/24/15
 */
public class DropboxImage implements Serializable {

    public final String imagePath;
    private String dropboxPath;

    public DropboxImage(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDropboxPath() {
        return dropboxPath;
    }

    public void setDropboxPath(String dropboxPath) {
        this.dropboxPath = dropboxPath;
    }
}
