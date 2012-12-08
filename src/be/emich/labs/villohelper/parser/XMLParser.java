package be.emich.labs.villohelper.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.net.Uri;
import be.emich.labs.villohelper.exception.VilloHelperException;
import be.emich.labs.villohelper.exception.VilloHelperXMLException;

public class XMLParser extends Parser {
	
	public XMLParser(Uri contentProviderUri, String entityName) {
		super(contentProviderUri, entityName);
	}

	@Override
	public void parse(String strToParse) throws VilloHelperException {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(strToParse));
			parseXML(xpp);
		} catch (XmlPullParserException e) {
			throw new VilloHelperXMLException("Problem parsing XML", e);
		} catch (IOException e) {
			throw new VilloHelperXMLException("IOException when parsing XML", e);
		}
	}
	
	public void parseXML(File f) throws XmlPullParserException,IOException {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			FileInputStream fis = new FileInputStream(f);
			xpp.setInput(new InputStreamReader(fis));
			parseXML(xpp);
	}

	public void parseXML(XmlPullParser xpp) throws XmlPullParserException,
			IOException {
		int eventType = xpp.getEventType();
		String textValue=null;
		//First tag is to avoid stack overflows when the customparser is used for the same entity (typical case: nested nodes)
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				if(customFields.contains(xpp.getName())){
					String field = xpp.getName();
					xpp.next();
					customParse(field,xpp);
				}
			}
			else if (eventType == XmlPullParser.END_TAG) {
				try{
					if(stringFields.contains(xpp.getName())){
						contentValues.put(mapToPersistableField(xpp.getName()), textValue);
					}else if(intFields.contains(xpp.getName())){
						contentValues.put(mapToPersistableField(xpp.getName()), Integer.parseInt(textValue));
					}else if(doubleFields.contains(xpp.getName())){
						contentValues.put(mapToPersistableField(xpp.getName()), Double.parseDouble(textValue));
					}else if(dateTimeFields.contains(xpp.getName())){
						contentValues.put(mapToPersistableField(xpp.getName()), Long.parseLong(textValue));
					}else if(boolFields.contains(xpp.getName())){
						contentValues.put(mapToPersistableField(xpp.getName()), Boolean.parseBoolean(textValue));
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				textValue = null;
				
				if(xpp.getName().equals(getEndField())){
					//xpp.next();
					prepare();
					return;
				}
			} else if (eventType == XmlPullParser.TEXT) {
				textValue = xpp.getText();
			}
			eventType = xpp.next();
		}
		prepare();
	}
	
	public void customParse(String field,XmlPullParser xpp) throws XmlPullParserException,IOException{
		;
	}
	
	public String getEndField(){
		return entityName;
	}
}
