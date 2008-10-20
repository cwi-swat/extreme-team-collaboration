package nl.jeldertpol.xtc.client.session.whosWhere;

import nl.jeldertpol.xtc.common.whosWhere.WhosWhere;

/**
 * Listener that can be notified of changes of {@link WhosWhereTracker}.
 * 
 * @author Jeldert Pol
 */
public interface WhosWhereListener {

	/**
	 * {@link WhosWhereTracker} information changed.
	 * 
	 * @param nickname
	 *            The nickname that is changed.
	 * @param filePath
	 *            The filePath that is changed.
	 */
	public void updateWhosWhere(final WhosWhere whosWhere);
}
