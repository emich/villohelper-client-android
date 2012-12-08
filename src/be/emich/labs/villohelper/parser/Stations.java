package be.emich.labs.villohelper.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;

public class Stations extends XMLParser {
	public static final String ENTITY_NAME = "stations";
	public List<Station> stations = new ArrayList<Station>();
	public boolean isClosest;
	private String language;
	private double currentLatitude;
	private double currentLongitude;
	
	public Stations() {
		super(null,ENTITY_NAME);
	}
	
	public Stations(boolean isClosest,double currentLatitude,double currentLongitude,String language) {
		super(null,ENTITY_NAME);
		this.isClosest = isClosest;
		this.language = language;
		this.currentLatitude = currentLatitude;
		this.currentLongitude = currentLongitude;
	}
	
	@Override
	public String[] getFieldsWithCustomParser() {
		return new String[]{Station.STATION};
	}
	
	@Override
	public void customParse(String field, XmlPullParser xpp)
			throws XmlPullParserException, IOException {
		Station station = new Station(isClosest,currentLatitude,currentLongitude,language);
		station.parseXML(xpp);
		stations.add(station);
	}
	
	public List<Station> getStations() {
		return stations;
	}
	
	public ContentValues[] getStationsAsContentValues(){
		ContentValues[] cvs = new ContentValues[stations.size()];
		int i = 0;
		for(Station station:stations){
			cvs[i]=station.contentValues;
			i++;
		}
		return cvs;
	}
}
