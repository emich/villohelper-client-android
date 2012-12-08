package be.emich.labs.villohelper.util;

public enum Availability {
	NOT_AVAILABLE,NONE,PARTIAL,AVAILABLE;
	
	public static final Availability getLevel(int number){
		if(number==-1)return NOT_AVAILABLE;
		else if(number==0 && number<=2)return NONE;
		else if(number<=5)return PARTIAL;
		else return AVAILABLE;
	}
}
