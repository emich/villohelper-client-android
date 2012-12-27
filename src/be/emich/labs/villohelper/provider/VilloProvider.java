package be.emich.labs.villohelper.provider;

import android.content.ContentValues;
import android.net.Uri;
import be.emich.labs.villohelper.parser.Favorite;
import be.emich.labs.villohelper.parser.Station;
import be.emich.labs.villohelper.util.Log;

public class VilloProvider extends BaseContentProvider {

	public static final String AUTHORITY = "be.emich.labs.villohelper";
	public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/");
	public static final Uri URI_STATIONS = Uri.parse(CONTENT_URI+Station.ENTITY_NAME);
	public static final Uri URI_STATIONS_SPECIFIC = Uri.parse(CONTENT_URI+Station.ENTITY_NAME_SPECIFIC);
	public static final Uri URI_FAVORITES = Uri.parse(CONTENT_URI+Favorite.ENTITY_NAME);
	
	@Override
	public int getDatabaseVersion() {
		return 11;
	}

	@Override
	public String getDatabaseName() {
		return "villo.db";
	}

	@Override
	public String getBasePackage() {
		return "be.emich.labs.villohelper";
	}

	@Override
	public Class[] getEntities() {
		return new Class[]{Station.class,Favorite.class};
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int count = 0;
		if(values.length==0)return 0;
		if(uri.getPathSegments().get(0).equals(Station.ENTITY_NAME) || uri.getPathSegments().get(0).equals(Station.ENTITY_NAME_SPECIFIC)){
			db.beginTransaction();
			try{
				ContentValues cvFlagDelete = new ContentValues();
				
				//In case we are bulk inserting stations that are closeby we do not need the deletion of the other stations
				if(!uri.getPathSegments().get(0).equals(Station.ENTITY_NAME_SPECIFIC)){
					cvFlagDelete.put(Station.FLAG_FOR_DELETE, 1);
					db.update(Station.ENTITY_NAME, cvFlagDelete, null, null);
				}
				
				for(ContentValues cv:values){
					String system = cv.getAsString(Station.SYSTEM);
					String id = cv.getAsString(Station.ID);
					cv.put(Station.FLAG_FOR_DELETE, 0);
					Log.v(getClass().getName(),"CV: "+cv.toString());
					int updateCount = db.update(Station.ENTITY_NAME, cv, Station.ID+"="+id+" and "+Station.SYSTEM+"='"+system+"'", null);
					count+=updateCount;
					if(updateCount==0){
						long retVal = db.insert(Station.ENTITY_NAME, "", cv);
						Log.v(getClass().getName(),"Inserting..."+retVal);
						count++;
					}
					else{
						Log.v(getClass().getName(),"Updating...");
					}
				}
				db.delete(Station.ENTITY_NAME, Station.FLAG_FOR_DELETE+"=1", null);
				db.setTransactionSuccessful();
				notifyChange(MODE_INSERT,uri);
				notifyChange(MODE_INSERT,VilloProvider.URI_STATIONS);
				Log.v(getClass().getName(),"DONE! "+uri);
			}
			finally{
				//e.printStackTrace();
				db.endTransaction();
			}
			return count;
		}
		else return super.bulkInsert(uri, values);
	}

}
