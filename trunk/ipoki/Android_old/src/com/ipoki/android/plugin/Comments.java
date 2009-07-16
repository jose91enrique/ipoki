package com.ipoki.android.plugin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Comments extends Activity {
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.message);
        
        
        Button saveButton = (Button)findViewById(R.id.button_save);
        saveButton.setOnClickListener(mSaveListener);
        Button cancelButton = (Button)findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(mCancelListener);
    }

    private OnClickListener mSaveListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            setResult(RESULT_OK, ((EditText)findViewById(R.id.edit_comment)).getText().toString().replace(' ', '+'));
            finish();
        }
    };

    private OnClickListener mCancelListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            setResult(RESULT_CANCELED);
            finish();
        }
    };
}
