package be.emich.labs.villohelper.parser;

import java.util.Calendar;

import be.emich.labs.villohelper.application.VilloHelperApplication;
import be.emich.labs.villohelper.exception.VilloHelperException;
import be.emich.labs.villohelper.provider.VilloProvider;
import be.emich.labs.villohelper.util.Log;
import be.emich.labs.villohelper.util.Util;

public class Station extends XMLParser {
	private double currentLatitude;
	private double currentLongitude;
	private boolean isClosest;
	private String language;
	
	public static final String ENTITY_NAME = "station";
	public static final String ENTITY_NAME_SPECIFIC = "stationspecific";
	
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String BIKES = "bikes";
	public static final String PARKING = "parking";
	public static final String TICKET = "ticket";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String STATION = "station";
	public static final String SYSTEM = "system";
	public static final String DISTANCE = "distance";
	public static final String IS_CLOSEST = "isclosest";
	public static final String IS_FAVORITE = "isfavorite";
	public static final String LAST_UPDATE = "lastUpdate";
	public static final String OPEN = "open";
	public static final String FLAG_FOR_DELETE = "deletion_flag";
	
	public Station() {
		super(VilloProvider.CONTENT_URI,ENTITY_NAME);
	}
	
	public Station(boolean isClosest,double currentLatitude,double currentLongitude,String language){
		this();
		if(isClosest)contentValues.put(IS_CLOSEST, isClosest);
		this.isClosest = isClosest;
		this.currentLatitude = currentLatitude;
		this.currentLongitude = currentLongitude;
		this.language = language;
	}
	
	@Override
	public String[] getIntFields() {
		return new String[]{BIKES,PARKING,TICKET,OPEN,FLAG_FOR_DELETE};
	}
	
	@Override
	public String[] getStringFields() {
		return new String[]{ID,NAME,SYSTEM};
	}
	
	@Override
	public String[] getDoubleFields() {
		return new String[]{LATITUDE,LONGITUDE,DISTANCE};
	}
	
	@Override
	public String[] getBoolFields() {
		return new String[]{IS_CLOSEST,IS_FAVORITE};
	}
	
	@Override
	public String[] getDateTimeFields() {
		return new String[]{LAST_UPDATE};
	}
	
	@Override
	public String[] getIdFields() {
		return new String[]{ID,SYSTEM};
	}
	
	@Override
	public void prepare() {
		String id = contentValues.getAsString(ID);
		String system = contentValues.getAsString(SYSTEM);
		String stationName = contentValues.getAsString(NAME);
		Log.v(getClass().getName(), "Prepare for system "+system+"/"+id);
		boolean isFavorite = VilloHelperApplication.getInstance().getDataHelper().isFavorite(id, system);
		contentValues.put(IS_FAVORITE, isFavorite);
		contentValues.put(LAST_UPDATE, Calendar.getInstance().getTimeInMillis());
		
		if(isClosest){
			Double latitude = contentValues.getAsDouble(LATITUDE);
			Double longitude = contentValues.getAsDouble(LONGITUDE);
			
			Double distance = Util.distance(latitude, longitude, currentLatitude, currentLongitude, 'M');
			contentValues.put(DISTANCE, distance);
		}
		
		if(system.equals(BikeSystem.VILLO.getSystemId()) && stationName!=null){
			String[] stationNames = stationName.split("/");
			Log.v(getClass().getName(),"StationNames: "+stationNames.length+" "+language);
			if(language.equals("nl") && stationNames.length>1){contentValues.put(NAME, stationNames[1]);}
			else contentValues.put(NAME, stationNames[0]);
		}

		String name = contentValues.getAsString(NAME);
		contentValues.put(NAME, name.trim());
		super.prepare();
	}
}
