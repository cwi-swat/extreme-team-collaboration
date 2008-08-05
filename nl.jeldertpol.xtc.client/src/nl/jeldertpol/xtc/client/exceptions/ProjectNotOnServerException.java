package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class ProjectNotOnServerException extends XtcException {

	private static final long serialVersionUID = 1L;

	private final static String message1 = "Could not join session. No session is assosiated with the project (";
	private final static String message2 = ").";

	public ProjectNotOnServerException(String project) {
		super(message1 + project + message2);
	}
}
