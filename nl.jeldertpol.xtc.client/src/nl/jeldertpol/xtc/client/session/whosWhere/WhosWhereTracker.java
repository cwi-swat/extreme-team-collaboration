package nl.jeldertpol.xtc.client.session.whosWhere;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.jeldertpol.xtc.common.whosWhere.WhosWhere;

/**
 * Tracks which client is editing which file.
 * 
 * @author Jeldert Pol
 */
public class WhosWhereTracker {

	/**
	 * Holds the nicknames and their filePath.
	 */
	private Map<String, String> whosWhereMap;

	/**
	 * Holds listeners.
	 */
	private final List<WhosWhereListener> listeners;

	/**
	 * Constructor.
	 */
	public WhosWhereTracker() {
		super();

		whosWhereMap = new HashMap<String, String>();
		listeners = new ArrayList<WhosWhereListener>();
	}

	/**
	 * A client changes location. Notifies all listeners of this new location.
	 * 
	 * @param whosWhere
	 *            Location information.
	 */
	public void change(final WhosWhere whosWhere) {
		whosWhereMap.put(whosWhere.getNickname(), whosWhere.getResourceName());

		for (WhosWhereListener listener : listeners) {
			listener.updateWhosWhere(whosWhere);
		}
	}

	/**
	 * Get all nicknames known.
	 * 
	 * @return all nicknames.
	 */
	public Set<String> getNicknames() {
		return whosWhereMap.keySet();
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
		return whosWhereMap.get(nickname);
	}

	public void clear() {
		whosWhereMap = new HashMap<String, String>();
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
