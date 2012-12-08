package be.emich.labs.villohelper.parser;

import be.emich.labs.villohelper.util.Log;
import be.emich.villo.R;


//TODO: needs to evolve to a more dynamic version. For now, we only support Brussels and Antwerp.
public enum BikeSystem {
	ALL,
	VILLO("be-bxl-villo",50.84d,4.37,R.string.system_brussels),
	AVELO("be-ant-avelo",51.22d,4.4,R.string.system_antwerp),
	LIBIA("be-nam-libia",50.46d,4.86,R.string.system_namur);
	
	private String systemId;
	private Double centerLatitude;
	private Double centerLongitude;
	private int nameStringResourceId;
	
	private BikeSystem() {
		this.nameStringResourceId = R.string.system_all;
		this.systemId = "all";
	}
	
	private BikeSystem(String systemId,Double centerLatitude,Double centerLongitude,int nameStringResourceId){
		this.systemId = systemId;
		this.centerLatitude = centerLatitude;
		this.centerLongitude = centerLongitude;
		this.nameStringResourceId = nameStringResourceId;
	}
	
	public String getSystemId() {
		return systemId;
	}
	
	public Double getCenterLatitude() {
		return centerLatitude;
	}
	
	public Double getCenterLongitude() {
		return centerLongitude;
	}
	
	public int getNameStringResourceId() {
		return nameStringResourceId;
	}
	
	public static BikeSystem getBestSystemForLatLong(double latitude,double longitude){
		double minDistance = Double.MAX_VALUE;
		BikeSystem closestSystem = null;
		for(BikeSystem system:BikeSystem.values()){
			if(!system.equals(BikeSystem.ALL)){
				double x = Math.abs(latitude-system.getCenterLatitude());
				double y = Math.abs(longitude-system.getCenterLongitude());
				
				double distance = Math.sqrt(x*x+y*y);
				if(distance<minDistance){
					minDistance=distance;
					closestSystem=system;
				}
			}
			
		}
		
		return closestSystem;
	}
}
