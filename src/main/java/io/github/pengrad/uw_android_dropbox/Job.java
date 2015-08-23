package io.github.pengrad.uw_android_dropbox;

import java.io.Serializable;
import java.util.List;

/**
 * stas
 * 8/24/15
 */
public class Job implements Serializable {

    public final String jobNumber;
    public final String client;
    public final List<ImageTimestamp> images;

    public Job(String jobNumber, String client, List<ImageTimestamp> images) {
        this.jobNumber = jobNumber;
        this.client = client;
        this.images = images;
    }
}
