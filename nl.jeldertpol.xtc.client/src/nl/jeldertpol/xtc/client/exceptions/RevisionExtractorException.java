package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class RevisionExtractorException extends XtcException {

	private static final long serialVersionUID = 1L;

	public RevisionExtractorException(Exception e) {
		super(e.getMessage());
	}
}
