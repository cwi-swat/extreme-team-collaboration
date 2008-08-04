package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class ProjectModifiedException extends XtcException {

	private static final long serialVersionUID = 1L;

	private final static String message1 = "The project (";
	private final static String message2 = ") has local modifications. Only unmodified projects can be used.";

	public ProjectModifiedException(String project) {
		super(message1 + project + message2);
	}
}
