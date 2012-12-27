package be.emich.labs.villohelper.util;

public class Log {
	public static boolean LOG_ENABLED=false;//BuildConfig.DEBUG;
	
	private static final int MAXCHARS_PER_LINE=1024;
	
	public static void e(String tag,String logMessage){
		if(!LOG_ENABLED)return;
		android.util.Log.e(tag, slash(logMessage));
		if(logMessage.length()>MAXCHARS_PER_LINE)e(tag,logMessage.substring(MAXCHARS_PER_LINE));
	}
	
	public static void w(String tag,String logMessage){
		if(!LOG_ENABLED)return;
		android.util.Log.w(tag, slash(logMessage));
		if(logMessage.length()>MAXCHARS_PER_LINE)w(tag,logMessage.substring(MAXCHARS_PER_LINE));
	}
	
	public static void i(String tag,String logMessage){
		if(!LOG_ENABLED)return;
		android.util.Log.i(tag, slash(logMessage));
		if(logMessage.length()>MAXCHARS_PER_LINE)i(tag,logMessage.substring(MAXCHARS_PER_LINE));
	}
	
	public static void d(String tag,String logMessage){
		if(!LOG_ENABLED)return;
		android.util.Log.d(tag, slash(logMessage));
		if(logMessage.length()>MAXCHARS_PER_LINE)d(tag,logMessage.substring(MAXCHARS_PER_LINE));
	}

	public static void v(String tag,String logMessage){
		if(!LOG_ENABLED)return;
		android.util.Log.v(tag, slash(logMessage));
		if(logMessage.length()>MAXCHARS_PER_LINE)v(tag,logMessage.substring(MAXCHARS_PER_LINE));
	}
	
	private static String slash(String str){
		if(str.length()>MAXCHARS_PER_LINE)return str.substring(0,MAXCHARS_PER_LINE);
		else return str;
	}
}
