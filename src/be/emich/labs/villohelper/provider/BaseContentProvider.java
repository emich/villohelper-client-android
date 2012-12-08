package be.emich.labs.villohelper.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import be.emich.labs.villohelper.parser.Parser;
import be.emich.labs.villohelper.util.Log;

public abstract class BaseContentProvider extends ContentProvider {
	
	public abstract int getDatabaseVersion();
	public abstract String getDatabaseName();
	public abstract String getBasePackage();
	
	public static final int MODE_DELETE = 1;
	public static final int MODE_INSERT = 2;
	public static final int MODE_BULK_INSERT = 3;
	public static final int MODE_UPDATE = 4;
	
	//Entities MUST be subclass of JSONParser and must have a constructor with no parameters!
	@SuppressWarnings("rawtypes")
	public abstract Class[] getEntities();
	
	private HashMap<String,Integer> entities=new HashMap<String,Integer>();

	protected SQLiteDatabase db;
	
	@Override
	public boolean onCreate() {
		@SuppressWarnings("rawtypes")
		Class[] declaredEntities = getEntities();
		
		try {
            for (int i = 0;i<declaredEntities.length;i++) {
                Parser baseParser = (Parser)declaredEntities[i].newInstance();
                entities.put(baseParser.getEntityName(), i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
        db = (new BaseSQLiteHelper(getContext(), declaredEntities,getDatabaseName(),getDatabaseVersion())).getWritableDatabase();
		
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		if(uri.getPathSegments().size()==0)return null;
		String entityName = uri.getPathSegments().get(1);
		if(entities.containsKey(entityName))return "vnd."+getBasePackage()+".dir/"+entityName;
		else return null;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		if(uri.getPathSegments().size()==0){
			throw new RuntimeException("Delete not supported for "+uri);
		}
		else{
			String entityName = uri.getPathSegments().get(0);
			if(entities.containsKey(entityName)){
				count += db.delete(entityName, selection, selectionArgs);
			}
			else{
				throw new RuntimeException("Delete not supported for "+uri);
			}
		}
		if(count>0)notifyChange(MODE_DELETE,uri);
		return count;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long id = -1;
		if(uri.getPathSegments().size()==0){
			throw new RuntimeException("Insert not supported for "+uri);
		}
		else{
			String entityName = uri.getPathSegments().get(0);
			if(entities.containsKey(entityName)){
				id = db.insert(entityName, "", values);
			}
			else{
				throw new RuntimeException("Insert not supported for "+uri);
			}
		}
		if(id!=-1){
			notifyChange(MODE_INSERT,uri);
			return Uri.withAppendedPath(uri, id+""); 
		}
		return null;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor c = null;
		if(uri.getPathSegments().size()==0){
			throw new RuntimeException("Query not supported for "+uri);
		}
		else{
			String entityName = uri.getPathSegments().get(0);
			if(entities.containsKey(entityName)){
				c = db.query(entityName, projection, selection, selectionArgs, null, null, sortOrder);
			}
			else{
				throw new RuntimeException("Query not supported for "+uri);
			}
		}
		
		if(c!=null){
			c.setNotificationUri(getContext().getContentResolver(), uri);
		}
		
		return c;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = 0;
		if(uri.getPathSegments().size()==0){
			throw new RuntimeException("Update not supported for "+uri);
		}
		else{
			String entityName = uri.getPathSegments().get(0);
			if(entities.containsKey(entityName)){
				count += db.update(entityName, values, selection, selectionArgs);
			}
			else{
				throw new RuntimeException("Update not supported for "+uri);
			}
		}
		
		if(count>0)notifyChange(MODE_UPDATE,uri);
		
		return count;
	}
	
	protected void notifyChange(int mode,Uri uri){
		getContext().getContentResolver().notifyChange(uri, null);
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		Log.v(getClass().getName(), "Uri: "+uri);
		if(uri.getPathSegments().size()==0){
			throw new RuntimeException("Bulk insert not supported for "+uri);
		}
		else{
			String entityName = uri.getPathSegments().get(0);
			if(entities.containsKey(entityName)){
				return doBulkInsert(uri, entityName, values);
			}
			else{
				throw new RuntimeException("Bulk insert not supported for "+uri);
			}
		}
	}
	
	protected int doBulkInsert(Uri uri,String entityName,ContentValues[] values){
    	int counter=0;
    	try {
    		db.beginTransaction();
    		for (ContentValues value : values) {
    			if(db.insert(entityName, "", value)!=-1)counter++;
    		}
    		db.setTransactionSuccessful();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		db.endTransaction();
    		Log.v(getClass().getName(),"Uri: "+uri);
    		notifyChange(MODE_BULK_INSERT,uri);
    	}
    	return counter;
    }
}
