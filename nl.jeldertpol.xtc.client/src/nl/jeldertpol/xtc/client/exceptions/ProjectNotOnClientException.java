package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class ProjectNotOnClientException extends XtcException {

	private static final long serialVersionUID = 1L;

	private final static String message1 = "The project (";
	private final static String message2 = ") is not present on the client.";

	public ProjectNotOnClientException(String project) {
		super(message1 + project + message2);
	}
}