package be.emich.labs.villohelper.thirdparty.runkeeper.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Path implements JSONAble {

	private List<PathItem> pathItems;
	private String type;
	private String equipment;
	private String notes;
	private boolean post_to_twitter;
	private boolean post_to_facebook;

	public Path(String type, String notes) {
		this.type = type;
		this.notes = notes;
		pathItems = new ArrayList<PathItem>();
	}

	@Override
	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		try {
			JSONArray pathItemArray = new JSONArray();
			for (PathItem item : pathItems) {
				pathItemArray.put(item.toJSON());
			}
			object.put("type", type);
			object.put("equipment", equipment == null ? "None" : equipment);
			object.put("notes", notes);
			object.put("path", pathItemArray);
			object.put("post_to_twitter", post_to_twitter);
			object.put("post_to_facebook", post_to_facebook);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

	public void setEquipment(String equipment) {
		this.equipment = equipment;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<PathItem> getPathItems() {
		return pathItems;
	}

	public void addPathItem(PathItem item) {
		pathItems.add(item);
	}

}
