package nl.jeldertpol.xtc.client.exceptions;

import nl.jeldertpol.xtc.common.exceptions.XtcException;

/**
 * @author Jeldert Pol
 */
public class RevisionExtractorException extends XtcException {

	private static final long serialVersionUID = 1L;

	public RevisionExtractorException(final Exception exception) {
		super(exception.getMessage());
	}
}
