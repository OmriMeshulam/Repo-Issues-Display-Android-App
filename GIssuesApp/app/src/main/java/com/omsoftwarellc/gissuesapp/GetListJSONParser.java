package com.omsoftwarellc.gissuesapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetListJSONParser {

    Context mContext;

    public GetListJSONParser(Context ctx) {
        mContext = ctx;
    }

    /**
     * Receives a JSONObject and returns a list
     */
    public List<HashMap<String, String>> parse(JSONArray jArray) {

        JSONArray jEntries = jArray;
        /** Invoking getEntries with the array of json object
         * where each json object represent a place
         */
        return getEntries(jEntries);
    }

    private List<HashMap<String, String>> getEntries(JSONArray jEntries) {
        int entriesCount = jEntries.length();
        List<HashMap<String, String>> entriesList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> entry = null;

        /** Taking each place, parses and adds to list object */
        for (int i = 0; i < entriesCount; i++) {
            try {
                /** Call getEntry with place JSON object to parse the place */
                entry = getEntry((JSONObject) jEntries.get(i), i);
                entriesList.add(entry);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return entriesList;
    }

    /**
     * Parsing the Place JSON object
     */
    private HashMap<String, String> getEntry(JSONObject jEntry, int pos) {

        HashMap<String, String> entry = new HashMap<String, String>();
        String title = "";
        String description = "";
        String uploadDate = "";
        String commentsURL = "";
        String numOfComments;

        try {
            title = jEntry.getString((mContext.getResources().getString(R.string.json_title)));
            description = jEntry.getString((mContext.getResources().getString(R.string.json_description)));
            uploadDate = jEntry.getString((mContext.getResources().getString(R.string.json_updated_at)));
            commentsURL = jEntry.getString((mContext.getResources().getString(R.string.json_comments_url)));
            numOfComments = jEntry.getString((mContext.getResources().getString(R.string.json_comments_num)));

            entry.put(mContext.getResources().getString(R.string.key_title), title);
            entry.put(mContext.getResources().getString(R.string.key_description), description);
            entry.put(mContext.getResources().getString(R.string.key_updated_at), uploadDate);
            entry.put(mContext.getResources().getString(R.string.key_comments_url), commentsURL);
            entry.put(mContext.getResources().getString(R.string.key_comments_num), numOfComments);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entry;
    }

}