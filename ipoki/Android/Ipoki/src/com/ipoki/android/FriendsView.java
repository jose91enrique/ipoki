package com.ipoki.android;

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

public class FriendsView extends ListActivity {
	FriendsAdapter mAdapter;

	
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
            } });
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
        	t.setText(mFriends[position].mName);
            t.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            // Give it a nice background
            return t;
        }
    }
    
}