package be.emich.labs.villohelper.exception;

public class VilloHelperNetworkException extends VilloHelperException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4020754824346469140L;

	public VilloHelperNetworkException(String message, Throwable t) {
		super(message, t);
	}

	public VilloHelperNetworkException(String message) {
		super(message);
	}

	public VilloHelperNetworkException(Throwable t) {
		super(t);
	}
	
}
