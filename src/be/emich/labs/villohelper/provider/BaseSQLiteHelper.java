package be.emich.labs.villohelper.provider;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import be.emich.labs.villohelper.parser.Parser;
import be.emich.labs.villohelper.util.Log;

public class BaseSQLiteHelper extends SQLiteOpenHelper {

	
	
	@SuppressWarnings("rawtypes")
	private Class[] entities;

	@SuppressWarnings("rawtypes") 
	public BaseSQLiteHelper(Context context,Class[] entities,String dbName,int dbVersion) {
		super(context,dbName, null, dbVersion);
		this.entities=entities;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
            for (Class<Parser> clazz : entities) {
                Parser baseParser = clazz.newInstance();
                Log.v(getClass().getName(),baseParser.getCreateStatement());
                String statement = baseParser.getCreateStatement();
                db.execSQL(statement);
                Log.d(getClass().getSimpleName(), statement);
                Map<String,String> indexes = baseParser.getIndexes();
                for(Entry<String, String> index:indexes.entrySet()){
                	db.execSQL("create index "+index.getKey()+" on "+baseParser.getEntityName()+" ("+index.getValue()+")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {
            for (Class<Parser> clazz : entities) {
                Parser baseParser = clazz.newInstance();
                Log.v(getClass().getName(),"drop table if exists " + baseParser.getEntityName());
                db.execSQL("drop table if exists " + baseParser.getEntityName());
            }
            onCreate(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
	}
	
}
