package nl.jeldertpol.xtc.common.chat;

import java.io.Serializable;

/**
 * A chat message.
 * 
 * @author Jeldert Pol
 */
public class ChatMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String nickname;
	private final String message;

	/**
	 * 
	 */
	public ChatMessage(final String nickname, final String message) {
		super();
		
		this.nickname = nickname;
		this.message = message;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
