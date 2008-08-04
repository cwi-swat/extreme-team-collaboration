package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class UnrevisionedProjectException extends XtcException {

	private static final long serialVersionUID = 1L;

	private final static String message1 = "The project (";
	private final static String message2 = ") is not under version control. Only versioned projects can be used.";

	public UnrevisionedProjectException(String project) {
		super(message1 + project + message2);
	}
}
