package io.github.pengrad.uw_android_dropbox.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * stas
 * 8/24/15
 */
public class Job implements Serializable {

    public final String jobNumber;
    public final String client;
    public final List<DropboxImage> images;
    public final Date date;

    public Job(String jobNumber, String client, List<DropboxImage> images) {
        this.jobNumber = jobNumber;
        this.client = client;
        this.images = images;
        this.date = new Date();
    }
}
