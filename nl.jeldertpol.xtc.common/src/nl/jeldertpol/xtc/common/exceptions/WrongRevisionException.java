package nl.jeldertpol.xtc.common.exceptions;

/**
 * @author Jeldert Pol
 */
public class WrongRevisionException extends XtcException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE1 = "Your revision (";
	private static final String MESSAGE2 = ") does not match the revision on the server (";
	private static final String MESSAGE3 = ") for this project. Please get the same revision.";

	public WrongRevisionException(final Long localRevision,
			final Long serverRevision) {
		super(MESSAGE1 + localRevision + MESSAGE2 + serverRevision + MESSAGE3);
	}
}
