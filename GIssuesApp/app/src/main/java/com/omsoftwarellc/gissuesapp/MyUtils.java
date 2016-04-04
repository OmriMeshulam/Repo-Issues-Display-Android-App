package com.omsoftwarellc.gissuesapp;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


// MyUtils class for connection testing.
// Developed for use with DisplayListActivity class
// Activity using must implement ConnectionTestResponse
public class MyUtils {

    private static final String TAG = "MyUtils";

    public static void isConnected(Activity mThis, int responseCode) {
        ConnectivityManager connMgr = (ConnectivityManager) mThis.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new CheckConnection(mThis, responseCode).execute();
        } else {
            if (mThis instanceof ConnectionTestResponse) {
                ((ConnectionTestResponse) mThis).connectionResponse(false, responseCode);
            }
        }
    }

    static class CheckConnection extends AsyncTask<String, String, String> {

        private Activity mThis;
        private int mURLResponseCode;
        private int mActivityResponseCode;

        public CheckConnection(Activity mThis, int responseCode) {
            mURLResponseCode = 0;
            mActivityResponseCode = responseCode;
            this.mThis = mThis;
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
            if (mURLResponseCode == 204) {
                //is connected
                if (mThis instanceof ConnectionTestResponse) {
                    ((ConnectionTestResponse) mThis).connectionResponse(true, mActivityResponseCode);
                }
            } else {
                //No connection"
                if (mThis instanceof ConnectionTestResponse) {
                    ((ConnectionTestResponse) mThis).connectionResponse(false, mActivityResponseCode);
                }
            }
            mThis = null;
        }
    }

}
