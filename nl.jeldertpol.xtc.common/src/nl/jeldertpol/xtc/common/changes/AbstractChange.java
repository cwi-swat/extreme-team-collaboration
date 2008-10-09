package nl.jeldertpol.xtc.common.changes;

import java.io.Serializable;

/**
 * A change to a resource made during a session.
 * 
 * @author Jeldert Pol
 */
public abstract class AbstractChange implements Serializable {

	private static final long serialVersionUID = 2L;

	private final String projectName;

	private final String nickname;

	/**
	 * A change to a resource made during a session.
	 * 
	 * @param projectName
	 *            The project which is affected by this change.
	 * @param nickname
	 *            The nickname who initiated to this change.
	 */
	public AbstractChange(final String projectName, final String nickname) {
		this.projectName = projectName;
		this.nickname = nickname;
	}

	/**
	 * Get the project which is affected by this change.
	 * 
	 * @return The projectName.
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Get the nickname who initiated to this change.
	 * 
	 * @return The nickname.
	 */
	public String getNickname() {
		return nickname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public abstract String toString();

	/**
	 * Returns a textual representation of the object, in XML format.
	 * 
	 * @return a textual representation of the object, in XML format.
	 */
	public abstract String toXMLString();

}
