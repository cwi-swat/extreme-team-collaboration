package nl.jeldertpol.xtc.client.session.whosWhere;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tracks which client is editing which file.
 * 
 * @author Jeldert Pol
 */
public class WhosWhere {

	/**
	 * Holds the nicknames and their filePath.
	 */
	private Map<String, String> whosWhere;

	/**
	 * Holds listeners.
	 */
	private List<WhosWhereListener> listeners;

	/**
	 * Constructor.
	 */
	public WhosWhere() {
		super();

		whosWhere = new HashMap<String, String>();
		listeners = new ArrayList<WhosWhereListener>();
	}

	/**
	 * A change was received. Notifies all listeners of this change.
	 * 
	 * @param nickname
	 *            The nickname of the client the change originated from.
	 * @param filePath
	 *            The file the change originated from, path is relative to the
	 *            project, and portable.
	 */
	public void change(final String nickname, final String filePath) {
		whosWhere.put(nickname, filePath);

		for (WhosWhereListener listener : listeners) {
			listener.updateWhosWhere(nickname, filePath);
		}
	}

	/**
	 * Get all nicknames known.
	 * 
	 * @return all nicknames.
	 */
	public Set<String> getNicknames() {
		return whosWhere.keySet();
	}

	/**
	 * Get the last reported filePath for a nickname.
	 * 
	 * @param nickname
	 *            The nickname the filePath is asked for.
	 * @return The filePath belonging to nickname, or <code>null</code> if
	 *         nickname is not present.
	 */
	public String getFilePath(final String nickname) {
		return whosWhere.get(nickname);
	}

	/**
	 * Add a listener to be notified. Does nothing when listener is already
	 * present.
	 * 
	 * @param whosWhereListener
	 *            The listener.
	 */
	public void addListener(final WhosWhereListener whosWhereListener) {
		if (!listeners.contains(whosWhereListener)) {
			listeners.add(whosWhereListener);
		}
	}

	/**
	 * Remove a listener. Does nothing when listener is not present.
	 * 
	 * @param whosWhereListener
	 *            The listener.
	 */
	public void removeListener(final WhosWhereListener whosWhereListener) {
		listeners.remove(whosWhereListener);
	}

}
