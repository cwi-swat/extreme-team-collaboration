package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class AlreadyInSessionException extends XtcException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE1 = "You are already in a session. The requested action can only be completed when you disconnect first.";

	public AlreadyInSessionException() {
		super(MESSAGE1);
	}
}
