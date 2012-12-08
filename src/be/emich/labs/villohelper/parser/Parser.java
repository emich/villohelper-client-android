package be.emich.labs.villohelper.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import be.emich.labs.villohelper.exception.VilloHelperException;
import be.emich.labs.villohelper.util.Log;

public abstract class Parser {
	protected List<String> stringFields;
	protected List<String> intFields;
	protected List<String> boolFields;
	protected List<String> dateTimeFields;
	protected List<String> customFields;
	protected List<String> doubleFields;
	protected List<String> recursiveFields;
	protected List<String> nonPersistableFields;
	protected Map<String,String> indexes = new HashMap<String,String>();

	protected String entityName;
	
	protected int resultCode;
	protected String errorMessage;
	protected Uri contentProviderUri;
	
	public ContentValues contentValues=new ContentValues();
	private Map<String,String> nonPersistableFieldsMap=new HashMap<String,String>();
	
	public static final String _ID = "_id";

	public Parser(Uri contentProviderUri, String entityName) {
		this.entityName = entityName;
		this.contentProviderUri = contentProviderUri;
		
		stringFields = Arrays.asList(getStringFields());
		intFields = Arrays.asList(getIntFields());
		boolFields = Arrays.asList(getBoolFields());
		dateTimeFields = Arrays.asList(getDateTimeFields());
		doubleFields = Arrays.asList(getDoubleFields());
		customFields = Arrays.asList(getFieldsWithCustomParser());
		recursiveFields = Arrays.asList(getFieldsWithRecursiveParser());
		nonPersistableFields = Arrays.asList(getNonPersistableFields());
	}

	public abstract void parse(String strToParse) throws VilloHelperException;
	
	public String[] getStringFields(){
		return new String[0];
	}
	
	public String[] getIntFields(){
		return new String[0];
	}
	
	public String[] getBoolFields(){
		return new String[0];
	}
	
	public String[] getDateTimeFields(){
		return new String[0];
	}
	
	public String[] getDoubleFields(){
		return new String[0];
	}
	
	public String[] getFieldsWithCustomParser() {
		return new String[0];
	}
	
	public String[] getFieldsWithRecursiveParser() {
		return new String[0];
	}
	
	public String[] getMandatoryFields(){
		return new String[0];
	}
	
	public String[] getIdFields(){
		return null;
	}
	
	public String[] getNonPersistableFields(){
		return new String[0];
	}
	
	public void mapNonPersistableFieldToColumn(String field,String column){
		nonPersistableFieldsMap.put(field,column);
	}
	
	public String getCreateStatement(){
		StringBuilder builder = new StringBuilder();
		builder.append("create table ").append(entityName).append("(");
		int argCounter = 0;
		
		if (intFields != null)
			for (String key : intFields) {
				if(!nonPersistableFields.contains(key)){
					if (argCounter != 0)
						builder.append(",");
					builder.append(key + " integer");
					argCounter++;
				}
			}
		
		if (doubleFields != null)
			for (String key : doubleFields) {
				if(!nonPersistableFields.contains(key)){
					if (argCounter != 0)
						builder.append(",");
					builder.append(key + " float");
					argCounter++;
				}
			}
		
		if (stringFields != null)
			for (String key : stringFields) {
				if(!nonPersistableFields.contains(key)){
					if (argCounter != 0)
						builder.append(",");
					builder.append(key + " text");
					argCounter++;
				}
			}
		
		if (dateTimeFields != null)
			for (String key : dateTimeFields) {
				if(!nonPersistableFields.contains(key)){
					if (argCounter != 0)
						builder.append(",");
					builder.append(key + " long");
					argCounter++;
				}
			}
		
		if (boolFields != null)
			for (String key : boolFields) {
				if(!nonPersistableFields.contains(key)){
					if (argCounter != 0)
						builder.append(",");
					builder.append(key + " long");
					argCounter++;
				}
			}

		if (argCounter != 0)
			builder.append(",");
		builder.append("_id long");
		
		if(getIdFields()!=null && getIdFields().length!=0){
			builder.append(", primary key (");
			int count=0;
			for(String idField : getIdFields()){
				if(count>0)builder.append(",");
				builder.append(idField);
				count++;
			}
			builder.append(")");
		}
		builder.append(")");
		
		Log.v(getClass().getName(), "Generated SQL statement: "+builder.toString());
		
		return builder.toString();
	}
	
	public void persist(Context ctx){
		prepare();
		Log.v(getClass().getName(), "Persisting...");
		ctx.getContentResolver().insert(Uri.withAppendedPath(contentProviderUri, entityName), contentValues);
	}
	
	public void prepare(){
		/*for(Map.Entry<String,String> entry : nonPersistableFieldsMap.entrySet()){
			if(stringFields.contains(entry.getValue())){
				contentValues.put(entry.getValue(), contentValues.getAsString(entry.getKey()));
				contentValues.remove(entry.getKey());
			}else if(intFields.contains(entry.getValue())){
				contentValues.put(entry.getValue(), contentValues.getAsInteger(entry.getKey()));
				contentValues.remove(entry.getKey());
			}
		}*/
		
		if(getId()==0)contentValues.put("_id", UUID.randomUUID().getMostSignificantBits());
	}
	
	public long getId(){
		if(contentValues.containsKey("_id") && contentValues.getAsString("_id").length()>0)
			return contentValues.getAsLong("_id");
		else return 0;
	}
	
	public String getEntityName() {
		return entityName;
	}
	
	public Map<String,String> getIndexes(){
		return indexes;
	}
	
	public String mapToPersistableField(String field){
		String mapsToField = nonPersistableFieldsMap.get(field);
		if(mapsToField==null)return field;
		return mapsToField;
	}

}
