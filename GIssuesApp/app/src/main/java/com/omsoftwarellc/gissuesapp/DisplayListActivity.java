package com.omsoftwarellc.gissuesapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DisplayListActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mListView;
    private DisplayListAdapter mAdapter;

    CustomProgressDialog mProgressDialog;

    CommentsDialog mCommentsDialog;

    String[] mlistItemsTitles, mListItemsDescriptions, mListItemsCommentURLs, mListItemsCommentNums, mListItemsComments, mListItemsCommentUserNames;

    Date[] mListItemsUploadDate;

    int mIndexedOrder[];

    boolean mFirstTimeInOnResume;

    String mNextPage, mPrevPage;

    Button mBPrev, mBNext;

    boolean mPageButtonClicked;

    List<DisplayListAdapter.Item> rows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);

        init();

        createToolbar();

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onBackPressed();
            }
        });
        mProgressDialog.show();

    }

    public void onResume() {
        super.onResume();

        if (mFirstTimeInOnResume) {
            mFirstTimeInOnResume = false;

            if (!isConnected(this)) {
                mProgressDialog.dismiss();
                createAlert(true);
            } else {
                mProgressDialog.mMessage.setText(getResources().getString(R.string.text_please_wait_downloading_list));
                final GetListTask listTask = new GetListTask();
                String repoURL = getResources().getString(R.string.repo_url);
                listTask.execute(repoURL);
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        listTask.cancel(true);
                        onBackPressed();
                    }
                });
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public boolean isConnected(Activity mThis) {
        ConnectivityManager connMgr = (ConnectivityManager) mThis.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Runtime runtime = Runtime.getRuntime();
            try {
                Process ipProcess = runtime.exec(mThis.getResources().getString(R.string.ping));
                int exitValue = ipProcess.waitFor();
                return (exitValue == 0);
            } catch (IOException e) {
                return false;
            } catch (InterruptedException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * A method to download json data from url
     * Returns JSON body string. Sets previous and next page strings within.
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            GitHubHeaderResponse response = new GitHubHeaderResponse(urlConnection);
            mPrevPage = response.getPrevious();
            mNextPage = response.getNext();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            //Log.d("Excep while dling url", e.toString());
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return data;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.display_next:
                pageButtonClicked(mNextPage);
                break;
            case R.id.display_prev:
                pageButtonClicked(mPrevPage);
                break;
        }
    }

    /**
     * GetListTask class downloads the list
     */
    private class GetListTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                //Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.mMessage.setText(getResources().getString(R.string.text_please_wait_processing_list));

            final ParseListTask parserTask = new ParseListTask();

            // Start parsing the list in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);

            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //parserTask.cancel(true);
                    onBackPressed();
                }
            });
        }

    }

    /**
     * ParseListTask class parses the List in JSON format
     */
    private class ParseListTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONArray jsonArray;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> entries = null;
            GetListJSONParser entriesJsonParser = new GetListJSONParser(DisplayListActivity.this);

            try {

                jsonArray = new JSONArray(jsonData[0]);

                /** Getting the parsed data as a List construct */
                entries = entriesJsonParser.parse(jsonArray);

            } catch (Exception e) {
                //Log.d("Exception", e.toString());
            }
            return entries;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            if (list != null) {
                if (list.size() == 0) {
                    createAlert(true);
                } else {
                    mlistItemsTitles = new String[list.size()];
                    mListItemsDescriptions = new String[list.size()];
                    mListItemsCommentURLs = new String[list.size()];
                    mListItemsCommentNums = new String[list.size()];
                    mListItemsUploadDate = new Date[list.size()];

                    for (int i = 0; i < list.size(); i++) {
                        mlistItemsTitles[i] = list.get(i).get(getResources().getString(R.string.key_title));
                        mListItemsDescriptions[i] = list.get(i).get(getResources().getString(R.string.key_description));
                        mListItemsCommentURLs[i] = list.get(i).get(getResources().getString(R.string.key_comments_url));
                        mListItemsCommentNums[i] = list.get(i).get(getResources().getString(R.string.key_comments_num));
                        try {
                            mListItemsUploadDate[i] = parse(list.get(i).get(getResources().getString(R.string.key_updated_at)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    mIndexedOrder = new IndirectSorter<Date>().sort(mListItemsUploadDate);

                    // When lists scaled larger, running in different threads may be optimal
                    reorderList(mlistItemsTitles, mIndexedOrder);
                    reorderList(mListItemsDescriptions, mIndexedOrder);
                    reorderList(mListItemsCommentURLs, mIndexedOrder);
                    reorderList(mListItemsCommentNums, mIndexedOrder);
                    reorderList(mListItemsUploadDate, mIndexedOrder);

                    rows.clear();

                    for (int i = 0; i < mlistItemsTitles.length; i++) {
                        rows.add(new DisplayListAdapter.Item(mlistItemsTitles[i], mListItemsDescriptions[i]));
                    }

                    if(!mPageButtonClicked) {

                        mAdapter.setItems(rows);

                        mListView.setAdapter(mAdapter);

                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                                if (Integer.valueOf(mListItemsCommentNums[position]) == 0) {
                                    createCommentsDialog(getResources().getString(R.string.text_no_comments));
                                } else {
                                    mProgressDialog.show();
                                    mProgressDialog.mMessage.setText(getResources().getString(R.string.text_dialog_checking_connection));
                                    if (!isConnected(DisplayListActivity.this)) {
                                        createAlert(true);
                                    } else {

                                        mProgressDialog.mMessage.setText(getResources().getString(R.string.text_downloading_comments));

                                        final GetCommentsTask commentsTask = new GetCommentsTask();

                                        String commentsURL = mListItemsCommentURLs[position];

                                        commentsTask.execute(commentsURL);

                                        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                commentsTask.cancel(true);
                                                mProgressDialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }else{
                        mPageButtonClicked = false;
                        mAdapter.notifyDataSetChanged();
                        mListView.setSelection(0);
                    }

                    setPageButtons();

                    mProgressDialog.dismiss();
                }
            } else {
                createAlert(true);
            }
        }
    }

    private void setPageButtons() {
        checkPageLink(mBPrev, mPrevPage);
        checkPageLink(mBNext, mNextPage);

    }

    public void checkPageLink(Button btn, String pageLink){
        if(pageLink != null){
            if(btn.getVisibility() == View.GONE){
                btn.setVisibility(View.VISIBLE);
            }
        }else{
            if(btn.getVisibility() == View.VISIBLE){
                btn.setVisibility(View.GONE);
            }
        }
    }

    public static Date parse(String input) throws java.text.ParseException {

        //NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
        //things a bit.  Before we go on we have to repair this.
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

        //this is zero time so we need to add that TZ indicator for
        if (input.endsWith("Z")) {
            input = input.substring(0, input.length() - 1) + "GMT-00:00";
        } else {
            int inset = 6;

            String s0 = input.substring(0, input.length() - inset);
            String s1 = input.substring(input.length() - inset, input.length());

            input = s0 + "GMT" + s1;
        }

        return df.parse(input);

    }

    // Creating and returning a sorted index array to reorder the lists
    public class IndirectSorter<T extends Comparable<T>> {
        class IndirectCompareClass<T extends Comparable<T>> implements Comparator<Integer> {
            T args[];

            public IndirectCompareClass(T args[]) {
                this.args = args;
            }

            // -1 for more recent/descending
            public int compare(Integer in1, Integer in2) {
                return -1 * args[in1].compareTo(args[in2]);
            }

        }

        public int[] sort(T args[]) {
            Integer originIndex[] = new Integer[args.length];
            int returnVals[] = new int[args.length];
            int n = originIndex.length;
            for (int i = 0; i < n; i++) {
                originIndex[i] = i;
            }
            Arrays.sort(originIndex, new IndirectCompareClass<T>(args));
            for (int i = 0; i < n; i++) {
                returnVals[i] = originIndex[i];
            }
            return returnVals;
        }

    }

    void reorderList(String arr[], int index[]) {
        int n = arr.length;
        String[] temp = new String[n];

        for (int i = 0; i < n; i++) {
            temp[i] = arr[index[i]];
        }

        // faster than for loop
        System.arraycopy(temp, 0, arr, 0, n);
    }

    void reorderList(Date arr[], int index[]) {
        int n = arr.length;
        Date[] temp = new Date[n];

        for (int i = 0; i < n; i++) {
            temp[i] = arr[index[i]];
        }

        // faster than for loop
        System.arraycopy(temp, 0, arr, 0, n);
    }

    /**
     * GetCommentsTask class downloads the comments
     */
    private class GetCommentsTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                //Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.mMessage.setText(getResources().getString(R.string.text_processing_comments));

            final ParseCommentsTask parserTask = new ParseCommentsTask();
            // Start parsing the list in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    parserTask.cancel(true);
                    mProgressDialog.dismiss();
                }
            });
        }
    }

    /**
     * ParseCommentsTask class parses the comments in JSON format
     */
    private class ParseCommentsTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONArray jsonArray;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> entries = null;
            GetCommentsJSONParser commentJsonParser = new GetCommentsJSONParser(DisplayListActivity.this);

            try {
                jsonArray = new JSONArray(jsonData[0]);

                /** Getting the parsed data as a List construct */
                entries = commentJsonParser.parse(jsonArray);

            } catch (Exception e) {
                //Log.d("Exception", e.toString());
            }
            return entries;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            if (list != null) { // else task was canceled

                mListItemsComments = new String[list.size()];
                mListItemsCommentUserNames = new String[list.size()];

                if (mListItemsComments.length == 0) {
                    createAlert(false);

                } else {

                    String comments;
                    StringBuilder commentsSB = new StringBuilder();

                    int listSize = list.size();

                    for (int i = 0; i < listSize; i++) {
                        mListItemsComments[i] = list.get(i).get(getResources().getString(R.string.key_comments_comment));
                        mListItemsCommentUserNames[i] = list.get(i).get(getResources().getString(R.string.key_comments_username));

                        commentsSB.append(mListItemsCommentUserNames[i]).append(":\n").append(mListItemsComments[i]);
                        if (i < listSize - 1) {
                            commentsSB.append("\n--------------------------\n");
                        }
                    }

                    comments = commentsSB.toString();

                    createCommentsDialog(comments);

                    mProgressDialog.dismiss();
                }
            } else {
                createAlert(false);
            }
        }
    }

    private void createCommentsDialog(String msg) {
        mCommentsDialog = new CommentsDialog(DisplayListActivity.this);
        mCommentsDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mCommentsDialog.dismiss();
            }
        });
        mCommentsDialog.show();
        mCommentsDialog.commentsTV.setText(msg);
    }

    private void createAlert(final boolean goBack) {
        new AlertDialog.Builder(DisplayListActivity.this)
                .setTitle(getResources().getString(R.string.text_error))
                .setMessage(getResources().getString(R.string.text_error_contact))
                .setNegativeButton(R.string.text_go_back, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (goBack) {
                            onBackPressed();
                        } else {
                            dialog.dismiss();
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (goBack) {
                            onBackPressed();
                        } else {
                            dialog.dismiss();
                        }
                    }
                })
                .show();
        mProgressDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            case R.id.action_settings:
            case R.id.action_help:
                Toast.makeText(DisplayListActivity.this, getResources().getString(R.string.text_unimplemented), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    public void init(){
        mListView = (ListView) findViewById(R.id.display_list_view);

        mProgressDialog = new CustomProgressDialog(this);

        mFirstTimeInOnResume = true;

        mAdapter = new DisplayListAdapter();

        mBPrev = (Button)findViewById(R.id.display_prev);
        mBNext = (Button)findViewById(R.id.display_next);

        mBPrev.setOnClickListener(this);
        mBNext.setOnClickListener(this);

        mPageButtonClicked = false;

        rows = new ArrayList<>();

    }

    public void pageButtonClicked(String pageURL){
        if (!isConnected(DisplayListActivity.this)) {
            createAlert(true);
        } else {
            mPageButtonClicked = true;
            mProgressDialog.show();
            mProgressDialog.mMessage.setText(getResources().getString(R.string.text_please_wait_downloading_list));
            final GetListTask listTask = new GetListTask();
            String repoURL = pageURL;
            listTask.execute(repoURL);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    listTask.cancel(true);
                    onBackPressed();
                }
            });
        }
    }

    public void createToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon_draw);
        getSupportActionBar().setTitle(getResources().getString(R.string.text_display_title));
    }

}

