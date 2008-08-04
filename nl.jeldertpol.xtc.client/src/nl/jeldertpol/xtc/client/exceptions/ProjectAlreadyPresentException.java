package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class ProjectAlreadyPresentException extends XtcException {

	private static final long serialVersionUID = 1L;

	private final static String message1 = "The project (";
	private final static String message2 = ") is already present on the server. You cannot start a new session with this project.";

	public ProjectAlreadyPresentException(String project) {
		super(message1 + project + message2);
	}
}
