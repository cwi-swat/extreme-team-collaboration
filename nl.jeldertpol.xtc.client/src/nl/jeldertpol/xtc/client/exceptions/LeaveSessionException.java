package nl.jeldertpol.xtc.client.exceptions;

import nl.jeldertpol.xtc.common.exceptions.XtcException;

/**
 * @author Jeldert Pol
 */
public class LeaveSessionException extends XtcException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE1 = "Leaving session with project (";
	private static final String MESSAGE2 = ") failed. Reason unknown.";

	public LeaveSessionException(final String project) {
		super(MESSAGE1 + project + MESSAGE2);
	}
}
