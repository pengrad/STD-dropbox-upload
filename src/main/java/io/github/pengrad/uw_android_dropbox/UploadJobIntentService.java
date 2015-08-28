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

import co.uk.rushorm.core.RushSearch;
import io.github.pengrad.uw_android_dropbox.model.DropboxImage;
import io.github.pengrad.uw_android_dropbox.model.Job;

public class UploadJobIntentService extends IntentService {

    public static final String TAG = "UploadJobIntentService";

    private static final String ACTION_UPLOAD_JOB = "io.github.penrad.uw_android_dropbox.action.UPLOAD_JOB";
    private static final String ACTION_REUPLOAD_JOB = "io.github.penrad.uw_android_dropbox.action.REUPLOAD_JOB";
    private static final String EXTRA_JOB = "io.github.pengrad.uw_android_dropbox.extra.JOB";
    private static final String EXTRA_JOB_ID = "io.github.pengrad.uw_android_dropbox.extra.JOB_ID";

    public static void startUploadJob(Context context, Job job) {
        Intent intent = new Intent(context, UploadJobIntentService.class);
        intent.setAction(ACTION_UPLOAD_JOB);
        intent.putExtra(EXTRA_JOB, job);
        context.startService(intent);
    }

    public static void reUploadJob(Context context, Job job) {
        Intent intent = new Intent(context, UploadJobIntentService.class);
        intent.setAction(ACTION_REUPLOAD_JOB);
        intent.putExtra(EXTRA_JOB_ID, job.getId());
        context.startService(intent);
    }

    public UploadJobIntentService() {
        super("UploadJobIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPLOAD_JOB.equals(action)) {
                Job job = (Job) intent.getSerializableExtra(EXTRA_JOB);
                handleJob(job);
            } else if (ACTION_REUPLOAD_JOB.equals(action)) {
                String jobId = intent.getStringExtra(EXTRA_JOB_ID);
                Job job = new RushSearch().whereId(jobId).findSingle(Job.class);
                if (job != null) {
                    handleJob(job);
                }
            }
        }
    }

    private void handleJob(Job job) {
        job.setPending();
        job.save();
        DropboxAPI<AndroidAuthSession> dropboxAPI = MyApp.get(this).getDropboxApi();
        boolean statusOk = true;
        for (DropboxImage image : job.getImages()) {
            if (!TextUtils.isEmpty(image.getDropboxPath())) {
                continue;
            }
            try {
                File file = new File(image.getImagePath());
                FileInputStream inputStream = new FileInputStream(file);
                String fileName = getFileName(job.getJobNumber(), job.getTankId(), job.getClient(), file.getName());
                Log.d(TAG, "handleJob startUpload " + fileName);
                DropboxAPI.Entry response = dropboxAPI.putFileOverwrite(fileName, inputStream, file.length(), null);
                image.setDropboxPath(response.path);
                image.save();
            } catch (FileNotFoundException e) {
                Log.d("UploadJobIntentService", "FileNotFound " + image.getImagePath());
                statusOk = false;
            } catch (DropboxException e) {
                Log.d("UploadJobIntentService", "Dropbox exception", e);
                statusOk = false;
            }
        }
        if (statusOk) job.setOk();
        else job.setError();
        job.save();
    }

    private String getFileName(String jobNumber, String tankId, String clientName, String fileName) {
        if (TextUtils.isEmpty(jobNumber)) {
            jobNumber = "000";
        }
        if (TextUtils.isEmpty(clientName)) {
            clientName = "unknown";
        }
        if (TextUtils.isEmpty(tankId)) {
            tankId = "";
        }

        int dotPosition = fileName.lastIndexOf(".");
        String name = fileName.substring(0, dotPosition);
        String extension = fileName.substring(dotPosition);

        return jobNumber + "_" + tankId + "_" + name + "_" + clientName + extension;
    }
}
