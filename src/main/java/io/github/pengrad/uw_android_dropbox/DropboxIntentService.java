package io.github.pengrad.uw_android_dropbox;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import io.github.pengrad.uw_android_dropbox.model.DropboxImage;
import io.github.pengrad.uw_android_dropbox.model.Job;

public class DropboxIntentService extends IntentService {

    public static final String TAG = "DropboxIntentService";

    private static final String ACTION_UPLOAD_JOB = "io.github.penrad.uw_android_dropbox.action.UPLOAD_JOB";
    private static final String EXTRA_JOB = "io.github.pengrad.uw_android_dropbox.extra.JOB";

    public static void startUploadJob(Context context, Job job) {
        Intent intent = new Intent(context, DropboxIntentService.class);
        intent.setAction(ACTION_UPLOAD_JOB);
        intent.putExtra(EXTRA_JOB, job);
        context.startService(intent);
    }

    public DropboxIntentService() {
        super("DropboxIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPLOAD_JOB.equals(action)) {
                Job job = (Job) intent.getSerializableExtra(EXTRA_JOB);
                handleJob(job);
            }
        }
    }

    private void handleJob(Job job) {
        job.save();
        DropboxAPI<AndroidAuthSession> dropboxAPI = MyApp.get(this).getDropboxApi();
        boolean statusOk = true;
        for (DropboxImage image : job.getImages()) {
            try {
                File file = new File(image.getImagePath());
                FileInputStream inputStream = new FileInputStream(file);
                String fileName = getFileName(job.getJobNumber(), job.getClient(), file.getName());
                Log.d(TAG, "handleJob startUpload " + fileName);
                DropboxAPI.Entry response = dropboxAPI.putFileOverwrite(fileName, inputStream, file.length(), null);
                image.setDropboxPath(response.path);
                image.save();
            } catch (FileNotFoundException e) {
                Log.d("DropboxIntentService", "FileNotFound " + image.getImagePath());
                statusOk = false;
            } catch (DropboxException e) {
                Log.d("DropboxIntentService", "Dropbox exception", e);
                statusOk = false;
            }
        }
        if (statusOk) job.setOk();
        else job.setError();
        job.save();
    }

    private String getFileName(String jobNumber, String clientName, String fileName) {
        if (TextUtils.isEmpty(jobNumber)) {
            jobNumber = "000";
        }
        if (TextUtils.isEmpty(clientName)) {
            clientName = "unknown";
        }

        int dotPosition = fileName.lastIndexOf(".");
        String name = fileName.substring(0, dotPosition);
        String extension = fileName.substring(dotPosition);

        return jobNumber + "_" + name + "_" + clientName + extension;
    }
}
