package ipoki.plugin;
 
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class Mymap extends MapActivity implements OnClickListener {
    private MapView mMapView;
    private MyLocationOverlay mMyLocationOverlay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.mymap);
        FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
       
        // 
        // TODO --> revisar la API Key, esta es la de produccion
        //
        mMapView = new MapView(this, "0cbsCnwzkViQZwFgU2Coie94cLA__ycwQxX3pqg");
        frame.addView(mMapView, new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        // Create an overlay to show current location
        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMyLocationOverlay.runOnFirstFix(new Runnable() { public void run() {
            mMapView.getController().animateTo(mMyLocationOverlay.getMyLocation());
        }});

        mMapView.getOverlays().add(mMyLocationOverlay);
        mMapView.getController().setZoom(17);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setSatellite(true);
        mMapView.setBuiltInZoomControls(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMyLocationOverlay.enableMyLocation();
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