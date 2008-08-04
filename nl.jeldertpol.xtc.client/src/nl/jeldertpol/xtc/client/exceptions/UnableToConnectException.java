package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class UnableToConnectException extends XtcException {

	private static final long serialVersionUID = 1L;

	private final static String message1 = "Connecting to the server failed:";

	public UnableToConnectException(String cause) {
		super(message1 + cause);
	}

}
