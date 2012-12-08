package be.emich.labs.villohelper.map;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import be.emich.labs.villohelper.activity.DetailActivity;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

public class StationItemizedOverlay<Item extends OverlayItem> extends BalloonItemizedOverlay<StationOverlayItem> {

	private ArrayList<StationOverlayItem> m_overlays = new ArrayList<StationOverlayItem>();
	private Context c;
	
	public StationItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenterBottom(defaultMarker), mapView);
		c = mapView.getContext();
		
	}

	public void addOverlay(StationOverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}

	@Override
	protected StationOverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}
	
	

	@Override
	protected boolean onBalloonTap(int index, StationOverlayItem item) {
		Intent i = new Intent(c,DetailActivity.class);
		i.putExtra(DetailActivity.EXTRA_ID, item.getStationId());
		c.startActivity(i);
		return true;
	}

	@Override
	protected BalloonOverlayView<StationOverlayItem> createBalloonOverlayView() {
		// use our custom balloon view with our custom overlay item type:
		return new StationOverlayView<StationOverlayItem>(getMapView().getContext(), getBalloonBottomOffset());
	}

}