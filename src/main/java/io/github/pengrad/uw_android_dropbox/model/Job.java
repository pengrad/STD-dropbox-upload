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

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_OK = "ok";
    public static final String STATUS_ERROR = "error";

    private String jobNumber;
    private String client;
    private Date date;
    private String status;

    @RushList(classType = DropboxImage.class)
    private List<DropboxImage> images;

    public Job() {
    }

    public Job(String jobNumber, String client, List<DropboxImage> images) {
        this.jobNumber = jobNumber;
        this.client = client;
        this.images = images;
        this.date = new Date();
        this.status = STATUS_PENDING;
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

    public boolean isPending() {
        return status.equals(STATUS_PENDING);
    }

    public boolean isOk() {
        return status.equals(STATUS_OK);
    }

    public boolean isError() {
        return status.equals(STATUS_ERROR);
    }

    public void setPending() {
        status = STATUS_PENDING;
    }

    public void setOk() {
        status = STATUS_OK;
    }

    public void setError() {
        status = STATUS_ERROR;
    }

    @Override
    public String toString() {
        return "job: " + jobNumber + " client: " + client + " status: " + status + " date: " + date + " images: " + Arrays.toString(images.toArray());
    }
}
