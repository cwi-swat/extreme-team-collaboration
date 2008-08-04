package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class AlreadyInSessionException extends XtcException {

	private static final long serialVersionUID = 1L;

	private final static String message1 = "You are already in a session. The requested action can only be completed when you disconnect first.";

	public AlreadyInSessionException() {
		super(message1);
	}
}
