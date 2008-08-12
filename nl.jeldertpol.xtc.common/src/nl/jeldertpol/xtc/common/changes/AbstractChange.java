package nl.jeldertpol.xtc.common.changes;

/**
 * A change to a resource made during a session.
 * 
 * @author Jeldert Pol
 */
public abstract class AbstractChange {
	private final String nickname;

	public AbstractChange(final String nickname) {
		this.nickname = nickname;
	}

	/**
	 * Get the nickname belonging to this change.
	 * 
	 * @return The nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public abstract String toString();

}
