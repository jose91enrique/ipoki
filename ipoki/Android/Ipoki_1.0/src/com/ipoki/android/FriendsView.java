package com.ipoki.android;
 
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsView extends ListActivity {

	static double mLatitude;
	static double mLongitude;

	FriendsAdapter mAdapter;
	static String[] mData = null;
	
	public static String pName;
	public static String pFoto;
	public static double pLat;
	public static double pLon;
	public static String pFecha;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		int numFriends = IpokiMain.mFriends.length;
		mData = new String[numFriends*5];
		for(int i = 0; i < numFriends; i++) {
			mData[i*5] = IpokiMain.mFriends[i].mName;
			mData[i*5+1] = String.valueOf(IpokiMain.mFriends[i].mLongitude);
			mData[i*5+2] = String.valueOf(IpokiMain.mFriends[i].mLatitude);
			mData[i*5+3] = String.format("%.1f", IpokiMain.mFriends[i].mDistance);
			mData[i*5+4] = IpokiMain.mFriends[i].mLocationDate.toLocaleString();
		}
        setContentView(R.layout.friendslist);
        loadFriends();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }

    public void onListItemClick(ListView l, View v, int position, long id){
		getListView().getItemAtPosition(position); 
		pName = mData[position*5];
		pLon = Double.parseDouble(mData[position*5+1]);
		pLat = Double.parseDouble(mData[position*5+2]);
//		pFoto = mFriends[position].mRutafoto;
		pFecha = mData[position*5+4];
		if ((pLat==0) & (pLon==0)) {
	 	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 	    builder.setTitle("Warning");
	 	    builder.setIcon(R.drawable.alert_dialog_icon);
	 	    builder.setMessage("This friend is not located.");
	        builder.show();
		} else {
			// llama a mostrar amigo
			ShowFriend();
		}
	}

    private void loadFriends(){
		mAdapter = new FriendsAdapter(this);
		setListAdapter(mAdapter);
    	if (mData==null) {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 	    builder.setTitle("Information");
	 	    builder.setIcon(R.drawable.alert_dialog_icon);
	 	    builder.setMessage("Sorry, no Ipoki friends around.");
	        builder.show();
	    	}
		}

    private static class FriendsAdapter extends BaseAdapter {
    	private LayoutInflater mInflater = null;

    	private Context mContext;
        //private Friend[] mFriends;  OJO>>>>>>>

        public FriendsAdapter(Context c) {
            mInflater = LayoutInflater.from(c);
            mContext = c;
        }

        FriendsAdapter mAdapter;
        
		@Override
		public int getCount() {
			if (mData == null)
				return 0;
			return mData.length / 5;
		}

		public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

     		ViewHolder holder;

     		if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.list_item_friend_text, null);
	
	            // Creates a ViewHolder and store references to the two children views
	            // we want to bind data to.
	            holder = new ViewHolder();
	            holder.text1 = (TextView) convertView.findViewById(R.id.text1);
	            holder.text2 = (TextView) convertView.findViewById(R.id.text2);
	            holder.text3 = (TextView) convertView.findViewById(R.id.text3);
	            //holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
     		}
     		else {
                holder = (ViewHolder) convertView.getTag();
     		}

            // Bind the data with the holder.
            holder.text1.setText("  " + mData[position*5]);
            
            if ((mData[position*5+2]=="0.0000") & (mData[position*5+1]=="0.0000")) {
        		holder.text2.setText("   No located.");
        		holder.text3.setText("    " + mData[position*5+4]);
    		} else {
    			holder.text2.setText("   Spotted at " + mData[position*5+3]+ " Km. from you.");
        		holder.text3.setText("    " + mData[position*5+2] + "  " + mData[position*5+1] + "    " + mData[position*5+4]);
    		}

            //holder.icon.setImageBitmap((position & 1) == 1 ? mIcon1 : mIcon2);
            return convertView;
        }

        static class ViewHolder {
            TextView text1;
            TextView text2;
            TextView text3;
            //ImageView icon;
        }
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menufriends, menu);
        return true;
    }
        
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showmap:
                //map friends
            	ShowMapFriends();
            	break;            
            case R.id.real:
                //Augmented Reality
            	ShowAR();
            	break;            
            case R.id.friendexit:
                //exit
            	finish();
            	break;            
        }
        return false;
    }

    public void ShowFriend() {
     	// Muestra los datos del amigo
     	Intent intent = new Intent(); 
    	intent.setClass(FriendsView.this, Friendshow.class); 
    	startActivity(intent); 
     }
    
    public void ShowMapFriends() {
     	// Muestra el mapa de los amigos
		Toast.makeText(getBaseContext(), 
				   "Loading map friends...", 
				   Toast.LENGTH_LONG).show();
     	Intent intent = new Intent(); 
    	intent.setClass(FriendsView.this, Friendmap.class); 
    	startActivity(intent); 
     }
     
     public void ShowAR() {
     	// Muestra los amigos en RA
 		Toast.makeText(getBaseContext(), 
				   "Loading Augmented Reality...", 
				   Toast.LENGTH_LONG).show();
 		Intent intent = new Intent(); 
  		intent.setClass(FriendsView.this, IpokiAR.class); 
 		startActivity(intent);
     }

}