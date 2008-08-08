package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class ProjectModifiedException extends XtcException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE1 = "The project (";
	private static final String MESSAGE2 = ") has local modifications. Only unmodified projects can be used.";

	public ProjectModifiedException(final String project) {
		super(MESSAGE1 + project + MESSAGE2);
	}
}
