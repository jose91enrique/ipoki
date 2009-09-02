package com.ipoki.android;
 
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Friendmap extends MapActivity implements OnClickListener {
    private MapView mMapView;
    private MyLocationOverlay mMyLocationOverlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // TODO cambiar el titulo: donde esta xxxxx
        
        setContentView(R.layout.friendmap);
        FrameLayout frame = (FrameLayout) findViewById(R.id.framefriend);
       
        // 
        // TODO --> revisar la API Key, esta es la de produccion
        //
        mMapView = new MapView(this, "0cbsCnwzkViQZwFgU2Coie94cLA__ycwQxX3pqg");
        frame.addView(mMapView, 
                new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setSatellite(false);
        mMapView.setBuiltInZoomControls(true);        

        // pintar los iconitos de los amigos en el mapa        
        List<Overlay> mapOverlays;
        Drawable drawable;
        IpokitoIcon itemizedOverlay;
        mapOverlays = mMapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.ipokito2);
        itemizedOverlay = new IpokitoIcon(drawable);
        int friendsNum = FriendsView.mFriends.length;
        
    	for (int i = 0; i < friendsNum; i++) {
    		GeoPoint point = new GeoPoint((int)(FriendsView.mFriends[i].mLatitude * 1E6),
    							          (int)(FriendsView.mFriends[i].mLongitude* 1E6));
    		OverlayItem overlayitem = new OverlayItem(point, "", "");
            itemizedOverlay.addOverlay(overlayitem, FriendsView.mFriends[i].mName);
    	}
        mapOverlays.add(itemizedOverlay);
        
        // Create an overlay to show current location
        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMyLocationOverlay.runOnFirstFix(new Runnable() { public void run() {
            mMapView.getController().animateTo(mMyLocationOverlay.getMyLocation());
        }});

        mMapView.getOverlays().add(mMyLocationOverlay);
        mMapView.getController().setZoom(15);
    }

    @Override
    protected void onResume() {
        mMyLocationOverlay.enableMyLocation();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mMyLocationOverlay.disableMyLocation();
        super.onStop();
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

	@Override
	public void onClick(View arg0) {
	}
   
}