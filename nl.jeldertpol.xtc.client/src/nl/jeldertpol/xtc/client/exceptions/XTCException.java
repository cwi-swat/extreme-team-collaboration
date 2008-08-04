package nl.jeldertpol.xtc.client.exceptions;

/**
 * An {@link XTCException} is an {@link Exception} thrown by XTC. This is used
 * so that the client only needs to catch one error. The error message shows
 * more detailed information about the error.
 * 
 * @author Jeldert Pol
 */
public abstract class XTCException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see Exception#Exception(String)
	 */
	public XTCException(String message) {
		super(message);
	}

}
