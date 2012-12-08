package be.emich.labs.villohelper.exception;

public class VilloHelperException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6490974035853602112L;

	public VilloHelperException(String message) {
		super(message);
	}
	
	public VilloHelperException(Throwable t){
		super(t);
	}
	
	public VilloHelperException(String message,Throwable t){
		super(message,t);
	}
}
