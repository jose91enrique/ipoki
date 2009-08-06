package com.ipoki.android;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class Ipoki extends ListActivity {
	FriendsAdapter mAdapter;
	final String mUserKey = "utXHANIfJeLGgDmfvEjgreRLS";
	String mTemp;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);
        Button connect = (Button) findViewById(R.id.connect);
        
        mAdapter = new FriendsAdapter(this);
        setListAdapter(mAdapter);
        
        connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	getFriends();
            } });
    }
    
    public class FriendsAdapter extends BaseAdapter {
        private Context mContext;
        private String[] mFriends = { "Amigo1", "Amigo2", "Amigo3" };

        public FriendsAdapter(Context c) {
            mContext = c;
        }

		@Override
		public int getCount() {
			return mFriends.length;
		}

		public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // Make an ImageView to show a photo
        	TextView t = new TextView(mContext);
        	t.setText(mFriends[position]);
            t.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            // Give it a nice background
            return t;
        }
    }
    
    private void getFriends(){
    	URL url;
    	try {
    		String userUrl = getString(R.string.friends_url) + mUserKey;
    		url = new URL(userUrl);
    		
    		HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
    		int responseCode = urlConnection.getResponseCode();
    		int i = urlConnection.getContentLength();
    		if (responseCode == HttpURLConnection.HTTP_OK) {
    			InputStream is = urlConnection.getInputStream();
    			BufferedInputStream bis = new BufferedInputStream(is);
    			
    			StringBuilder sb = new StringBuilder();
    			StringBuffer ssb = new StringBuffer();
    			ByteArrayBuffer bab = new ByteArrayBuffer(200);
    			int current = 0; 
                while((current = bis.read()) != -1){ 
                     bab.append((byte)current); 
                } 

                mTemp = new String(bab.toByteArray()); 
    		}
    	} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	finally {
    		
    	}
    }
}