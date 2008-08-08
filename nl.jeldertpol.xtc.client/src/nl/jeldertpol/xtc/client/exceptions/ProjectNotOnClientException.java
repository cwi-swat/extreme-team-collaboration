package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class ProjectNotOnClientException extends XtcException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE1 = "The project (";
	private static final String MESSAGE2 = ") is not present on the client.";

	public ProjectNotOnClientException(final String project) {
		super(MESSAGE1 + project + MESSAGE2);
	}
}
