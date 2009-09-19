package com.ipoki.android;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
	
	public static String pName;
	public static String pFoto;
	public static double pLat;
	public static double pLon;
	public static String pFecha;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendslist);
        loadFriends();
    }

    public void onListItemClick(ListView l, View v, int position, long id){
		getListView().getItemAtPosition(position); 
		pName = IpokiMain.mFriends[position].mName;
		pLat = IpokiMain.mFriends[position].mLatitude;
		pLon = IpokiMain.mFriends[position].mLongitude;
//		pFoto = mFriends[position].mRutafoto;
		pFecha = IpokiMain.mFriends[position].mLocationDate.toLocaleString();
		if ((pLat==0) & (pLon==0)) {
	 	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 	    builder.setTitle("Warning");
	 	    builder.setIcon(R.drawable.alert_dialog_icon);
	 	    builder.setMessage("This friend don't have position.");
	        builder.show();
		} else {
			// llama a mostrar amigo
			ShowFriend();
		}
	}

    private void loadFriends(){
		mAdapter = new FriendsAdapter(this);
		setListAdapter(mAdapter);
    	if (IpokiMain.mFriends.length==0) {
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
			if (IpokiMain.mFriends == null)
				return 0;
			return IpokiMain.mFriends.length;
		}

		public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

        	ViewHolder holder;

            convertView = mInflater.inflate(R.layout.list_item_friend_text, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.text1 = (TextView) convertView.findViewById(R.id.text1);
            holder.text2 = (TextView) convertView.findViewById(R.id.text2);
            holder.text3 = (TextView) convertView.findViewById(R.id.text3);
            //holder.icon = (ImageView) convertView.findViewById(R.id.icon);

            convertView.setTag(holder);

            // Bind the data with the holder.
            holder.text1.setText("  " + IpokiMain.mFriends[position].mName);
            
            double mwork[] = IpokiMain.mFriends[position].getDistanceBearing();
            double m1 = IpokiMain.mFriends[position].mLatitude;
            double m2 = IpokiMain.mFriends[position].mLongitude;
            
            if ((m1==0) & (m2==0)) {
        		holder.text2.setText("   No located.");
        		holder.text3.setText("    " + IpokiMain.mFriends[position].mLocationDate);
    		} else {
    			holder.text2.setText("   Spotted at " + String.format("%.1f", mwork[0])+ " Km. from you.");
        		holder.text3.setText("    " + String.format("%.5f",m1) + "  " + String.format("%.5f",m2) + "    " + IpokiMain.mFriends[position].mLocationDate);
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
  		intent.setClass(FriendsView.this, IpokiMain.class); 
 		startActivity(intent);
     }

}