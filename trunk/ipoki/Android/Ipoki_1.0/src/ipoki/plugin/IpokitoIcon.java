package ipoki.plugin;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class IpokitoIcon extends ItemizedOverlay {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private ArrayList<String> wName = new ArrayList<String>();
	
	public IpokitoIcon(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public void addOverlay(OverlayItem overlay, String name) {
	    mOverlays.add(overlay);
	    wName.add(name);
	    populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	@Override
	public boolean onTap(GeoPoint p, MapView mapView) { 
          //Here add code for click event 
		
		// TODO pintar el nombre del pavo...
/*		Toast.makeText(getBaseContext(), 
				   index, 
				   Toast.LENGTH_LONG).show();
         return super.onTap(index);
*/ 
		return true;
     }  
}
