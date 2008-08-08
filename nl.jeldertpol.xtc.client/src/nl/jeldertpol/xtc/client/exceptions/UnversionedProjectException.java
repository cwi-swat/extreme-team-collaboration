package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class UnversionedProjectException extends XtcException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE1 = "The project (";
	private static final String MESSAGE2 = ") is not under version control. Only versioned projects can be used.";

	public UnversionedProjectException(final String project) {
		super(MESSAGE1 + project + MESSAGE2);
	}
}
