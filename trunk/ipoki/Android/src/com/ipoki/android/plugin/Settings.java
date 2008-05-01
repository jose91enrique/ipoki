package com.ipoki.android.plugin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Settings extends Activity {
	public static String username = "";
	public static String password = "";
	public static int pingServer = 60;
	public static int language = Integer.MIN_VALUE;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.settings);
        this.loadLanguages();
        
        ((EditText)findViewById(R.id.edit_username)).setText(Settings.username);
        ((EditText)findViewById(R.id.edit_password)).setText(Settings.password);
        ((EditText)findViewById(R.id.edit_ping_server)).setText(String.valueOf(Settings.pingServer));
        
        Button saveButton = (Button)findViewById(R.id.button_save);
        saveButton.setOnClickListener(mSaveListener);
        Button cancelButton = (Button)findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(mCancelListener);
    }

    private OnClickListener mSaveListener = new OnClickListener()
    {
        public void onClick(View v)
        {
            Settings.username = ((EditText)findViewById(R.id.edit_username)).getText().toString();
            Settings.password = ((EditText)findViewById(R.id.edit_password)).getText().toString();
            Settings.pingServer = 
            	Integer.decode(((EditText)findViewById(R.id.edit_ping_server)).getText().toString()).intValue();
            setResult(RESULT_OK);
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
    
    private void loadLanguages() {
        Spinner spLang = (Spinner) findViewById(R.id.spinner_lang);
        ArrayAdapter<CharSequence> adapter = 
        	ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLang.setAdapter(adapter);
    }
}