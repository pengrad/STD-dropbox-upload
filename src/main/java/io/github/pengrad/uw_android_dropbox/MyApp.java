package io.github.pengrad.uw_android_dropbox;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.squareup.leakcanary.LeakCanary;

/**
 * stas
 * 8/23/15
 */
public class MyApp extends Application {

    public static final String DROPBOX_TOKEN = "DROPBOX_TOKEN";

    public static MyApp get(Context context) {
        return (MyApp) context.getApplicationContext();
    }

    private DropboxAPI<AndroidAuthSession> mDBApi;

    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);

        AppKeyPair appKeys = new AppKeyPair(BuildConfig.API_KEY, BuildConfig.API_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);

        String accessToken = loadDropboxToken();
        if (!TextUtils.isEmpty(accessToken)) {
            session.setOAuth2AccessToken(accessToken);
        }

        mDBApi = new DropboxAPI<>(session);
    }

    public DropboxAPI<AndroidAuthSession> getDropboxApi() {
        return mDBApi;
    }

    public boolean isDropboxLinked() {
        return mDBApi.getSession().isLinked();
    }

    private String loadDropboxToken() {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(DROPBOX_TOKEN, null);
    }

    public void saveDropboxAuth(String accessToken) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(DROPBOX_TOKEN, accessToken)
                .apply();
    }
}
