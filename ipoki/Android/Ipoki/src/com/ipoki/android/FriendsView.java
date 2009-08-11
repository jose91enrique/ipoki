package com.ipoki.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class FriendsView extends ListActivity {
	FriendsAdapter mAdapter;
	final String mUserKey = "utXHANIfJeLGgDmfvEjgreRLS";
    private static final int DIALOG_DOWNLOAD = 0;

	
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
            	showDialog(DIALOG_DOWNLOAD);
            	getFriends();
            } });
    }
    
    class Friend {
    	public String Name;
    	public String Longitude;
    	public String Latitude;
    	public String SessionKey;
    	
    	public Friend(String name, String longitude, String latitude, String sessionKey) {
    		Name = name;
    		Longitude = longitude;
    		Latitude = latitude;
    		SessionKey = sessionKey;
    	}
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD: {
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("Getting data");
                dialog.setMessage("Please wait while downloading...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;
            }
        }
        return null;
    }

    
    class FriendsAdapter extends BaseAdapter {
        private Context mContext;
        private Friend[] mFriends;

        public FriendsAdapter(Context c) {
            mContext = c;
        }

		@Override
		public int getCount() {
			if (mFriends == null)
				return 0;
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
        	t.setText(mFriends[position].Name);
            t.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            // Give it a nice background
            return t;
        }
        
        public void setFriends(Friend[] friends) {
        	mFriends = friends;
        	notifyDataSetChanged();
        }
        
    }
    
    private void getFriends(){
    	URL url;
		String userUrl = getString(R.string.friends_url) + mUserKey;
		try {
			url = new URL(userUrl);
			new DownloadFriends().execute(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 
		
    }
    
    class DownloadFriends extends AsyncTask<URL, Integer, String> {

    	@Override
		protected String doInBackground(URL... params) {
			String friendsString = "";
	    	try {
	    		HttpURLConnection urlConnection = (HttpURLConnection)params[0].openConnection();
	    		int responseCode = urlConnection.getResponseCode();

	    		if (responseCode == HttpURLConnection.HTTP_OK) {
	    			InputStream is = urlConnection.getInputStream();
	    			BufferedReader br = new BufferedReader(new InputStreamReader(is));
	    			friendsString = br.readLine();
	    		}
	    	} catch (IOException e) {
				e.printStackTrace();
			}
	    	return friendsString;
		}
		
	    protected void onPostExecute(String result) {
	    	String[] friendsData = null;
	    	friendsData = result.substring(3).split("\\${3}");
	    	
	    	if (friendsData.length % 4 != 0)
	    		Log.w("Ipoki", "Malformed data from server");

	    	int friendsNum = friendsData.length / 4;
	    	Friend[] friends = new Friend[friendsNum];
	    	for (int i = 0; i < friendsNum; i++) {
	    		friends[i] = new Friend(friendsData[4 * i], 
	    								friendsData[4 * i + 1], 
	    								friendsData[4 * i + 2], 
	    								friendsData[4 * i + 3]);
	    	}
	    	
	    	mAdapter.setFriends(friends);
	    	dismissDialog(DIALOG_DOWNLOAD);
	    }
    }
}