package nl.jeldertpol.xtc.client.session.chat;

import java.util.ArrayList;
import java.util.List;

import nl.jeldertpol.xtc.common.chat.ChatMessage;

/**
 * Tracks all received chat messages.
 * 
 * @author Jeldert Pol
 */
public class ChatTracker {

	/**
	 * Holds chat messages.
	 */
	private List<ChatMessage> chatMessages;

	/**
	 * Holds listeners.
	 */
	private final List<ChatListener> listeners;

	/**
	 * TODO Javadoc
	 */
	public ChatTracker() {
		super();

		chatMessages = new ArrayList<ChatMessage>();
		listeners = new ArrayList<ChatListener>();
	}

	/**
	 * @return the chatMessages
	 */
	public List<ChatMessage> getChatMessages() {
		return chatMessages;
	}

	/**
	 * A new chat message is received. Updates all listeners.
	 * 
	 * @param chatMessage
	 *            The chat message.
	 */
	public void newMessage(final ChatMessage chatMessage) {
		chatMessages.add(chatMessage);

		for (ChatListener listener : listeners) {
			listener.updateChat(chatMessage);
		}
	}

	public void clear() {
		chatMessages = new ArrayList<ChatMessage>();
	}

	/**
	 * Add a listener to be notified. Does nothing when listener is already
	 * present.
	 * 
	 * @param chatListener
	 *            The listener.
	 */
	public void addListener(final ChatListener chatListener) {
		if (!listeners.contains(chatListener)) {
			listeners.add(chatListener);
		}
	}

	/**
	 * Remove a listener. Does nothing when listener is not present.
	 * 
	 * @param chatListener
	 *            The listener.
	 */
	public void removeListener(final ChatListener chatListener) {
		listeners.remove(chatListener);
	}

}
