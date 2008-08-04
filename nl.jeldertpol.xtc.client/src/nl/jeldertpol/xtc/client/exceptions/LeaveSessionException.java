package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class LeaveSessionException extends XTCException {

	private static final long serialVersionUID = 1L;

	private final static String message1 = "Leaving session with project (";
	private final static String message2 = ") failed. Reason unknown.";

	public LeaveSessionException(String project) {
		super(message1 + project + message2);
	}
}
