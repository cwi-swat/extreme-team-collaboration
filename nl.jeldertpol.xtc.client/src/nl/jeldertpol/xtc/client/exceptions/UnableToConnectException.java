package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class UnableToConnectException extends XtcException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE1 = "Connecting to the server failed:";

	public UnableToConnectException(final String cause) {
		super(MESSAGE1 + cause);
	}

}
