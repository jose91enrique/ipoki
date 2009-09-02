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
	
	static Friend[] mFriends = null;
	static FriendsUpdateThread mFriendsUpdateThread = null;

	public static String pName;
	public static String pFoto;
	public static double pLat;
	public static double pLon;
	public static String pFecha;	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendslist);
        
		getFriends();
    }

    public void onListItemClick(ListView l, View v, int position, long id){
		getListView().getItemAtPosition(position); 
		pName = mFriends[position].mName;
		pLat = mFriends[position].mLatitude;
		pLon = mFriends[position].mLongitude;
//		pFoto = mFriends[position].mRutafoto;
		pFecha = mFriends[position].mLocationDate;
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

    private void CargaAmigos(){
		mAdapter = new FriendsAdapter(this);
		setListAdapter(mAdapter);
    	if (mFriends.length==0) {
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
            holder.text1.setText("  " + mFriends[position].mName);
            
            double mwork[] = mFriends[position].getDistanceBearing();
            double m1 = mFriends[position].mLatitude;
            double m2 = mFriends[position].mLongitude;
            
            if ((m1==0) & (m2==0)) {
        		holder.text2.setText("   No located.");
        		holder.text3.setText("    " + mFriends[position].mLocationDate);
    		} else {
    			holder.text2.setText("   Spotted at " + String.format("%.1f", mwork[0])+ " Km. from you.");
        		holder.text3.setText("    " + String.format("%.5f",m1) + "  " + String.format("%.5f",m2) + "    " + mFriends[position].mLocationDate);
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
    
    private void getFriends(){
    	URL url;
    	// TODO falta meter Main.mServer para que este bien...
		String userUrl = getString(R.string.friends_url) + IpokiMain.mUserKey;

		try {
			url = new URL(userUrl);
			new DownloadFriends().execute(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 
    }

    private class DownloadFriends extends AsyncTask<URL, Integer, Friend[]> {

    	@Override
		protected Friend[] doInBackground(URL... params) {
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
	    	return processFriends(friendsString);
		}
    	
    	private Friend[] processFriends(String result)
    	{
	    	String[] friendsData = null;
	    	friendsData = result.substring(3).split("\\${3}");
	    	
	    	if (friendsData.length % 6 != 0)
	    		Log.w("Ipoki", "Malformed data from server");

	    	int friendsNum = friendsData.length / 6;
	    	Friend[] friends = new Friend[friendsNum];
	    	for (int i = 0; i < friendsNum; i++) {
	    		friends[i] = new Friend(friendsData[6 * i], 
	    								friendsData[6 * i + 1], 
	    								friendsData[6 * i + 2],
	    								friendsData[6 * i + 3],
	    								friendsData[6 * i + 4],
	    								friendsData[6 * i + 5]);
	    		friends[i].updateDistanceBearing(IpokiMain.mLongitude, IpokiMain.mLatitude);
//   		double[] d = friends[i].getDistanceBearing();
//   		Log.i("Ipoki", friends[i].mName + " : " + Double.toString(d[0]) + " - " + Double.toString(d[1]));
	    	}	    	
	    	return friends;
    	}
		
	    protected void onPostExecute(Friend[] friends) {
	    	mFriends = friends;
	    	CargaAmigos();
			//Log.i("Ipoki", "acaba Onpostexecute");   
			//Log.i("Ipoki", "hay amigos " + String.valueOf(mAdapter.getCount()));	    
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