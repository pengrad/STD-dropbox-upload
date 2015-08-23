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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DropboxIntentService extends IntentService {

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
                handleActionFoo(job);
            }
        }
    }

    private void handleActionFoo(Job job) {
        DropboxAPI<AndroidAuthSession> dropboxAPI = MyApp.get(this).getDropboxApi();
        for (ImageTimestamp image : job.images) {
            try {
                File file = new File(image.imagePath);
                FileInputStream inputStream = new FileInputStream(file);
                String fileName = getFileName(job.jobNumber, job.client, image);
                DropboxAPI.Entry response = dropboxAPI.putFileOverwrite(fileName, inputStream, file.length(), null);
                Log.d("DropboxIntentService", "The uploaded file's rev is: " + response.rev);
            } catch (FileNotFoundException e) {
                Log.d("DropboxIntentService", "FileNotFound " + image.imagePath);
            } catch (DropboxException e) {
                Log.d("DropboxIntentService", "Dropbox exception", e);
            }
        }
    }

    private String getFileName(String jobNumber, String clientName, ImageTimestamp image) {
        if (TextUtils.isEmpty(jobNumber)) {
            jobNumber = "000";
        }
        if (TextUtils.isEmpty(clientName)) {
            clientName = "unknown";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        String timestamp = dateFormat.format(new Date(image.millis));

        String extension = image.imagePath.substring(image.imagePath.lastIndexOf("."));

        return jobNumber + "_" + timestamp + "_" + clientName + extension;
    }
}
