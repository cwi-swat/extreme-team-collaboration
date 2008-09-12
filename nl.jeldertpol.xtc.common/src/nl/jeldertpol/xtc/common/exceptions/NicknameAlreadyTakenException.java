package nl.jeldertpol.xtc.common.exceptions;

/**
 * @author Jeldert Pol
 */
public class NicknameAlreadyTakenException extends XtcException {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE1 = "Your nickname (";
	private static final String MESSAGE2 = ") is already taken. Please choose another one.";

	public NicknameAlreadyTakenException(final String nickname) {
		super(MESSAGE1 + nickname + MESSAGE2);
	}
}
