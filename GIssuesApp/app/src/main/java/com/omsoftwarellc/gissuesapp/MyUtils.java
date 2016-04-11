package com.omsoftwarellc.gissuesapp;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


// MyUtils class for connection testing.
// Developed for use with DisplayListActivity class
// Activity using must implement ConnectionTestResponse
public class MyUtils {

    private static final String TAG = "MyUtils";

    public static void isConnected(Context ctx, int requestCode) {
        ConnectivityManager connMgr = (ConnectivityManager) ctx.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new CheckConnection(ctx, requestCode).execute();
        } else {
            if (ctx instanceof ConnectionTestResponse) {
                ((ConnectionTestResponse) ctx).connectionResponse(false, requestCode);
            }
        }
    }

    static class CheckConnection extends AsyncTask<String, String, String> {

        private Context mContext;
        private int mURLResponseCode;
        private int mActivityRequestCode;

        public CheckConnection(Context ctx, int requestCode) {
            this.mContext = ctx;
            mURLResponseCode = 0;
            mActivityRequestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... args) {

            int responseCode = 0;
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL("http://clients3.google.com/generate_204")
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(4000);
                urlc.connect();
                responseCode = urlc.getResponseCode();
            } catch (IOException e) {
                //Log.e(TAG, e.toString());
            }

            mURLResponseCode = responseCode;

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (mContext instanceof ConnectionTestResponse) {
                ((ConnectionTestResponse) mContext).connectionResponse((mURLResponseCode == 204), mActivityRequestCode);
            }
            destroyVars();
        }

        private void destroyVars() {
            mContext = null;
            mURLResponseCode = 0;
            mActivityRequestCode = 0;
        }
    }
}
