package com.ipoki.android;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class FriendsOverlay extends ItemizedOverlay<OverlayItem> {
	private List<OverlayItem> locations = new ArrayList<OverlayItem>();
	private Drawable mMarker;

	public FriendsOverlay(Drawable marker) {
		super(marker);
		mMarker = marker;
		
		for (Friend f: IpokiMain.mFriends) {
			GeoPoint gp = new GeoPoint((int) (f.mLatitude * 1E6), (int) (f.mLongitude * 1E6));
			locations.add(new OverlayItem(gp, f.mName, f.mSessionKey));		
		}
		populate();
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(mMarker);
	}
	
	@Override
	protected boolean onTap(int i) {
		
		return true;
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return locations.get(i);
	}

	@Override
	public int size() {
		return locations.size();
	}

}
