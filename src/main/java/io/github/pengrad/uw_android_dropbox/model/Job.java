package io.github.pengrad.uw_android_dropbox.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import co.uk.rushorm.core.RushObject;
import co.uk.rushorm.core.annotations.RushList;

/**
 * stas
 * 8/24/15
 */
public class Job extends RushObject implements Serializable {

    private String jobNumber;
    private String client;
    private Date date;

    @RushList(classType = DropboxImage.class)
    private List<DropboxImage> images;

    public Job() {
    }

    public Job(String jobNumber, String client, List<DropboxImage> images) {
        this.jobNumber = jobNumber;
        this.client = client;
        this.images = images;
        this.date = new Date();
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public List<DropboxImage> getImages() {
        return images;
    }

    public void setImages(List<DropboxImage> images) {
        this.images = images;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "job: " + jobNumber + " client: " + client + " date: " + date + " images: " + Arrays.toString(images.toArray());
    }
}
