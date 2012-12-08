package be.emich.labs.villohelper.parser;

import be.emich.labs.villohelper.exception.VilloHelperException;
import be.emich.labs.villohelper.provider.VilloProvider;

public class Favorite extends Parser {

	public static final String ENTITY_NAME = "favorite";
	
	public static final String STATION_ID = "stationid";
	public static final String SYSTEM = "bikesystem";
	
	public Favorite() {
		super(VilloProvider.CONTENT_URI,ENTITY_NAME);
	}
	
	@Override
	public void parse(String strToParse) throws VilloHelperException {
		;
	}
	
	@Override
	public String[] getStringFields() {
		return new String[]{STATION_ID,SYSTEM};
	}
	
	@Override
	public String[] getIdFields() {
		return getStringFields();
	}

}
