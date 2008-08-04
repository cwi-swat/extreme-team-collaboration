package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class WrongRevisionException extends XtcException {

	private static final long serialVersionUID = 1L;

	private final static String message1 = "Your revision (";
	private final static String message2 = ") does not match the revision on the server (";
	private final static String message3 = ") for this project. Please get the same revision.";
	
	public WrongRevisionException(Long localRevision, Long serverRevision) {
		super(message1 + localRevision + message2 + serverRevision + message3);
	}
}
