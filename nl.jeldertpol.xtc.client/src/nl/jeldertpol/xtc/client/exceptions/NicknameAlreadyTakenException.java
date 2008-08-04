package nl.jeldertpol.xtc.client.exceptions;

/**
 * @author Jeldert Pol
 */
public class NicknameAlreadyTakenException extends XtcException {

	private static final long serialVersionUID = 1L;

	private final static String message1 = "Your nickname (";
	private final static String message2 = ") is already taken. Please choose another one.";

	public NicknameAlreadyTakenException(String nickname) {
		super(message1 + nickname + message2);
	}
}
