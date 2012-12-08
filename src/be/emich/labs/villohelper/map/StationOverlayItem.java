package be.emich.labs.villohelper.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class StationOverlayItem extends OverlayItem {

	private String stationId;
	private int bikes;
	private int parking;
	private boolean acceptsCreditCard;
	private boolean isOpen;
	
	public StationOverlayItem(GeoPoint geoPoint, String stationName, int bikes, int parking, boolean acceptsCreditCard, boolean isOpen,String stationId) {
		super(geoPoint, stationName, stationName);
		this.bikes = bikes;
		this.parking = parking;
		this.acceptsCreditCard = acceptsCreditCard;
		this.isOpen = isOpen;
		this.stationId = stationId;
	}
	
	public int getBikes() {
		return bikes;
	}
	
	public int getParking() {
		return parking;
	}
	
	public boolean acceptsCreditCard() {
		return acceptsCreditCard;
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	public String getStationName() {
		return super.getTitle();
	}
	
	public String getStationId() {
		return stationId;
	}

}
