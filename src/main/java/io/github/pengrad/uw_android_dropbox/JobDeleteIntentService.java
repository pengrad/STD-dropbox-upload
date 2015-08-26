package io.github.pengrad.uw_android_dropbox;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;

import co.uk.rushorm.core.RushSearch;
import io.github.pengrad.uw_android_dropbox.model.DropboxImage;
import io.github.pengrad.uw_android_dropbox.model.Job;

public class JobDeleteIntentService extends IntentService {
    private static final String ACTION_FULL_DELETE = "io.github.pengrad.uw_android_dropbox.action.FULL_DELERE";
    private static final String ACTION_LOCAL_DELETE = "io.github.pengrad.uw_android_dropbox.action.LOCAL_DELETE";

    private static final String EXTRA_JOB_ID = "io.github.pengrad.uw_android_dropbox.extra.JOB";

    public static void startActionDeleteDropbox(Context context, String jobId) {
        startAction(context, jobId, ACTION_FULL_DELETE);
    }

    public static void startActionLocalDelete(Context context, String jobId) {
        startAction(context, jobId, ACTION_LOCAL_DELETE);
    }

    private static void startAction(Context context, String jobId, String action) {
        Intent intent = new Intent(context, JobDeleteIntentService.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_JOB_ID, jobId);
        context.startService(intent);
    }

    public JobDeleteIntentService() {
        super("JobDeleteIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String jobId = intent.getStringExtra(EXTRA_JOB_ID);

            Job job = new RushSearch().whereId(jobId).findSingle(Job.class);

            if (ACTION_FULL_DELETE.equals(action)) {
                handleDelete(job, true);
            } else if (ACTION_LOCAL_DELETE.equals(action)) {
                handleDelete(job, false);
            }
        }
    }

    private void handleDelete(Job job, boolean fromDropbox) {
        job.setPending();
        job.save();
        boolean statusOK = true;
        if (fromDropbox) {
            DropboxAPI<AndroidAuthSession> dropboxAPI = MyApp.get(this).getDropboxApi();
            for (DropboxImage image : job.getImages()) {
                try {
                    if (!TextUtils.isEmpty(image.getDropboxPath())) {
                        dropboxAPI.delete(image.getDropboxPath());
                        image.setDropboxPath(null);
                        image.save();
                    }
                } catch (DropboxException e) {
                    if (e.toString().contains("404 Not Found")) {
                        // we don't have this file, ok
                        image.setDropboxPath(null);
                        image.save();
                    } else {
                        // something real wrong
                        statusOK = false;
                        break;
                    }
                }
            }
        }
        if (statusOK) {
            try {
                for (DropboxImage image : job.getImages()) {
                    File file = new File(image.getImagePath());
                    if (file.exists()) {
                        file.delete();
                    }
                }
                job.delete();
            } catch (Exception e) {
                // why not?
                job.setOk();
                job.save();
            }
        } else {
            job.setOk();
            job.save();
        }

    }
}
