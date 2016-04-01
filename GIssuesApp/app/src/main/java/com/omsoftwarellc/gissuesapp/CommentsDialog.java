package com.omsoftwarellc.gissuesapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class CommentsDialog extends ProgressDialog {

    TextView commentsTV;

    public CommentsDialog(Context context) {
        super(context);
        this.setCanceledOnTouchOutside(false);
    }

    public CommentsDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_dialog_view);

        commentsTV = (TextView) findViewById(R.id.comments_textview);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();

    }

}