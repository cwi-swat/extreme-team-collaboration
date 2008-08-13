package nl.jeldertpol.xtc.common.changes;

import java.io.Serializable;

/**
 * A change to a resource made during a session.
 * 
 * @author Jeldert Pol
 */
public abstract class AbstractChange implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
