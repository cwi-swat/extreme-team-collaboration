package nl.jeldertpol.xtc.client.session.chat;

import nl.jeldertpol.xtc.common.chat.ChatMessage;

/**
 * Listener that can be notified of new chat messages.
 * 
 * @author Jeldert Pol
 */
public interface ChatListener {

	/**
	 * A new chat message is received.
	 * 
	 * @param chatMessage
	 *            The new message.
	 */
	public void updateChat(ChatMessage chatMessage);
}
