package be.emich.labs.villohelper.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import be.emich.labs.villohelper.parser.BikeSystem;
import be.emich.labs.villohelper.parser.Favorite;
import be.emich.labs.villohelper.parser.Station;
import be.emich.labs.villohelper.util.Log;

public class DataHelper {
	private Context ctx;
	
	public DataHelper(Context ctx) {
		this.ctx = ctx;
	}
	
	public final Cursor getFavoriteStations(){
		return ctx.getContentResolver().query(VilloProvider.URI_STATIONS,null,Station.IS_FAVORITE+"=1",null,null);
	}
	
	public final CursorLoader getLoaderForAllStations(){
		return new CursorLoader(ctx, VilloProvider.URI_STATIONS,null,null,null,Station.NAME);
	}
	
	public final Cursor getAllStations(){
		return ctx.getContentResolver().query(VilloProvider.URI_STATIONS,null,null,null,Station.NAME);
	}
	
	public final CursorLoader getLoaderForClosestStations(){
		return new CursorLoader(ctx, VilloProvider.URI_STATIONS,null,Station.IS_CLOSEST+"=1 and "+Station.DISTANCE+"<2",null,Station.DISTANCE);
	}
	
	public final CursorLoader getLoaderForFavoriteStations(){
		return new CursorLoader(ctx, VilloProvider.URI_STATIONS,null,Station.IS_FAVORITE+"=1",null,Station.DISTANCE);
	}
	
	public final String[] getFavorites(BikeSystem system){
		Cursor c = ctx.getContentResolver().query(VilloProvider.URI_FAVORITES, null, Favorite.SYSTEM+"='"+system.getSystemId()+"'", null, null);
		String[] ids = new String[c.getCount()];
		c.moveToFirst();
		if(c.getCount()>0){
			int index=0;
			while(!c.isAfterLast()){
				ids[index]=c.getString(c.getColumnIndex(Favorite.STATION_ID));
				index++;
				c.moveToNext();
			}
		}
		c.close();
		
		return ids;
	}
	
	public final boolean isFavorite(String id,String system){
		Cursor c = ctx.getContentResolver().query(VilloProvider.URI_FAVORITES,null,Favorite.STATION_ID+"='"+id+"' and "+Favorite.SYSTEM+"='"+system+"'",null,null);
		Log.v(getClass().getName(),"Favorite? "+c.getCount());
		boolean retVal = (c.getCount()>0);
		c.close();
		return retVal;
	}
	
	public final boolean toggleFavorite(String id,String system){
		if(isFavorite(id, system)){
			ctx.getContentResolver().delete(VilloProvider.URI_FAVORITES,Favorite.STATION_ID+"='"+id+"' and "+Favorite.SYSTEM+"='"+system+"'",null);
			ContentValues cv = new ContentValues();
			cv.put(Station.IS_FAVORITE, false);
			ctx.getContentResolver().update(VilloProvider.URI_STATIONS, cv, Station.ID+"='"+id+"' and "+Station.SYSTEM+"='"+system+"'", null);
			
			return false;
		}
		else{
			ContentValues favoriteCV = new ContentValues();
			favoriteCV.put(Favorite.STATION_ID, id);
			favoriteCV.put(Favorite.SYSTEM, system);
			ctx.getContentResolver().insert(VilloProvider.URI_FAVORITES, favoriteCV);
			
			ContentValues stationCV = new ContentValues();
			stationCV.put(Station.IS_FAVORITE, true);
			ctx.getContentResolver().update(VilloProvider.URI_STATIONS, stationCV, Station.ID+"='"+id+"' and "+Station.SYSTEM+"='"+system+"'",null);
			
			return true;
		}
	}
	
	public final void deleteAllStationsFromSystem(String system){
		ctx.getContentResolver().delete(VilloProvider.URI_STATIONS, system==null?("system='"+system+"'"):null, null);
	}

	public void insertStations(ContentValues[] stationsAsContentValues,boolean truncate) {
		ctx.getContentResolver().bulkInsert(truncate?VilloProvider.URI_STATIONS:VilloProvider.URI_STATIONS_SPECIFIC, stationsAsContentValues);
	}
	
	public int updateStation(ContentValues cv){
		String system = cv.getAsString(Station.SYSTEM);
		String id = cv.getAsString(Station.ID);
		int returnVal = ctx.getContentResolver().update(VilloProvider.URI_STATIONS, cv, Station.ID+"='"+id+"' and "+Station.SYSTEM+"='"+system+"'", null);
		Log.v(getClass().getName(),"Retval: "+returnVal+" System: "+system+" ID: "+id);
		return returnVal;
	}
	
	public int updateStations(ContentValues cv){
		return ctx.getContentResolver().update(VilloProvider.URI_STATIONS, cv, null, null);
	}
	
	public int deleteAllOtherSystems(String system){
		return ctx.getContentResolver().delete(VilloProvider.URI_STATIONS, Station.SYSTEM+"!='"+system+"'", null);
	}
	
	public String getStationIdByRowId(String system,String rowId){
		Cursor c = ctx.getContentResolver().query(VilloProvider.URI_STATIONS, null, Station._ID+"='"+rowId+"' and "+Station.SYSTEM+"='"+system+"'", null, null);
		c.moveToFirst();
		String retVal=null;
		if(c.getCount()>0){
			retVal = c.getString(c.getColumnIndex(Station.ID));
		}
		c.close();
		return retVal;
	}
	
	public Cursor getStationById(String system,String id){
		return ctx.getContentResolver().query(VilloProvider.URI_STATIONS, null, Station.ID+"='"+id+"' and "+Station.SYSTEM+"='"+system+"'", null, null);
	}
}
