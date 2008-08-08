package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class ProjectAlreadyPresentException extends XtcException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE1 = "The project (";
	private static final String MESSAGE2 = ") is already present on the server. You cannot start a new session with this project.";

	public ProjectAlreadyPresentException(final String project) {
		super(MESSAGE1 + project + MESSAGE2);
	}
}
