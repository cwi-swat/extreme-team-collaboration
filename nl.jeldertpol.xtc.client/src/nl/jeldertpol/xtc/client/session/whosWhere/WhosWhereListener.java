package nl.jeldertpol.xtc.client.session.whosWhere;

/**
 * Listener that can be notified of changes of {@link WhosWhere}.
 * 
 * @author Jeldert Pol
 */
public interface WhosWhereListener {

	/**
	 * {@link WhosWhere} information changed.
	 * 
	 * @param nickname
	 *            The nickname that is changed.
	 * @param filePath
	 *            The filePath that is changed.
	 */
	public void updateWhosWhere(final String nickname, final String filePath);
}
