package com.omsoftwarellc.gissuesapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CustomProgressDialog extends ProgressDialog {
    TextView mMessage;

    ImageView image;

    Context myActivity;

    ProgressBar mSpinner;

    public CustomProgressDialog(Context context) {
        super(context);
        myActivity = context;
        this.setCanceledOnTouchOutside(false);

    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_dialog_view);

        image = (ImageView) findViewById(R.id.dialogicon);

        mMessage = (TextView) findViewById(R.id.customProgressText);

        mSpinner = (ProgressBar) findViewById(R.id.pb_loading_user);
        mSpinner.getIndeterminateDrawable().setColorFilter(0xFF00adef, android.graphics.PorterDuff.Mode.SRC_ATOP);

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