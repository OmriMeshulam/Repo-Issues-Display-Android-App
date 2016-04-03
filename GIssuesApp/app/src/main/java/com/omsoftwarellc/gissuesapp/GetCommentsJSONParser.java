package com.omsoftwarellc.gissuesapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetCommentsJSONParser {

    Context mContext;

    public GetCommentsJSONParser(Context ctx){
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
        String username = "";
        String comment = "";

        try {

            username = jEntry.getJSONObject(mContext.getResources().getString(R.string.json_comments_user)).getString(mContext.getResources().getString(R.string.json_comments_username));

            comment = jEntry.getString(mContext.getResources().getString(R.string.json_comments_comment));

            entry.put(mContext.getResources().getString(R.string.key_comments_username), username);
            entry.put(mContext.getResources().getString(R.string.key_comments_comment), comment);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entry;
    }

}