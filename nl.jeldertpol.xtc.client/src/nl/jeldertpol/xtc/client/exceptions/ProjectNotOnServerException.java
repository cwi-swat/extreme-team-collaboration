package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class ProjectNotOnServerException extends XtcException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE1 = "Could not join session. No session is assosiated with the project (";
	private static final String MESSAGE2 = ").";

	public ProjectNotOnServerException(final String project) {
		super(MESSAGE1 + project + MESSAGE2);
	}
}
