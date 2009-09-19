package com.ipoki.android;
 
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Friendshow extends MapActivity implements OnClickListener {
    private MapView mMapView;
    
    private TextView outname;
    private TextView outwhere;
    private TextView outfecha;
    private ImageView outfoto;

    private GeoPoint wgeo = new GeoPoint(
            (int) (FriendsView.pLat * 1E6),
            (int) (FriendsView.pLon * 1E6));        

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO cambiar el titulo por : DATOS DE XXXX
        setContentView(R.layout.friendshow);

        // pintar los datos del usuario
	    // los textbox
        outname = (TextView) findViewById(R.id.fname);
        outwhere = (TextView) findViewById(R.id.fgeoname);
        outfecha = (TextView) findViewById(R.id.ffecha);
        outfoto = (ImageView) findViewById(R.id.fphoto);
        outname.setText(FriendsView.pName);
        outfecha.setText(FriendsView.pFecha);        

        // TODO pintar la foto
//        URI wFoto = null;
//        wFoto.resolve(FriendsView.pFoto);
//        outfoto.setImageURI(wFoto);
        
        // halla la direccion
        if ((FriendsView.pLat==0) & (FriendsView.pLon==0)) {
        	outwhere.setText("Not located");
        } else {
        	outwhere.setText("Lat: " + String.format("%.5f",FriendsView.pLat) +  "\nLon: " + String.format("%.5f",FriendsView.pLon));
        	/* TODO hacer el geocoder
	        Geocoder wgc = null;
	        List <Address> wloc = null;
			String wloc1[] = null;
			String wloc2 = null;
	        try {
				wloc = wgc.getFromLocation(FriendsView.pLat, FriendsView.pLon, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			wloc.toArray(wloc1);
			wloc2 = wloc1.toString();
	        outwhere.setText(wloc2);
	        */
        }

        // el mapa
        FrameLayout frame = (FrameLayout) findViewById(R.id.framefriendshow);
        // 
        // TODO --> revisar la API Key, esta es la de produccion
        //
        mMapView = new MapView(this, "0slLUwYJ2HzIjfqA-mmYITQe45PnAN2BnvBuWog");
        frame.addView(mMapView, new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mMapView.getController().setZoom(15);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setSatellite(false);
        mMapView.setBuiltInZoomControls(true);

        // centra el mapa en el amigo
        final MapController controller = mMapView.getController();
        controller.setCenter(wgeo);

        // pintar el iconito del usuario en el mapa        
        List<Overlay> mapOverlays;
        Drawable drawable;
        IpokitoIcon itemizedOverlay;
        mapOverlays = mMapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.ipokito2);
        itemizedOverlay = new IpokitoIcon(drawable);
        OverlayItem overlayitem = new OverlayItem(wgeo, "", "");
        itemizedOverlay.addOverlay(overlayitem, FriendsView.pName);
        mapOverlays.add(itemizedOverlay);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
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