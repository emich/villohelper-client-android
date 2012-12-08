package be.emich.labs.villohelper.thirdparty.runkeeper.model;

import org.json.JSONException;
import org.json.JSONObject;

public class PathItem implements JSONAble {
	public long timestamp;
	public double latitude;
	public double longitude;
	public double altitude;
	public PathEventType type;

	public PathItem(long timestamp, double latitude, double longitude,
			double altitude, PathEventType type) {
		this.timestamp = timestamp;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.type = type;
	}
	
	@Override
	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		try {
			object.put("timestamp", timestamp);
			object.put("latitude", latitude);
			object.put("longitude", longitude);
			object.put("altitude", altitude);
			object.put("type",type.toString().toLowerCase());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
}
