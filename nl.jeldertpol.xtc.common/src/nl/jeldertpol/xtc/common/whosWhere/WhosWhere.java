package nl.jeldertpol.xtc.common.whosWhere;

import java.io.Serializable;

/**
 * @author Jeldert Pol
 * 
 */
public class WhosWhere implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String projectName;

	private final String resourceName;

	/**
	 * The nickname.
	 */
	private final String nickname;

	private long timestamp = 0L;

	public WhosWhere(final String projectName, final String resourceName,
			final String nickname) {
		this.projectName = projectName;
		this.resourceName = resourceName;
		this.nickname = nickname;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getNickname() + ": " + getResourceName();
	}

	/**
	 * Returns a textual representation of the object, in XML format.
	 * 
	 * @return a textual representation of the object, in XML format.
	 */
	public String toXMLString() {
		StringBuilder sb = new StringBuilder(89); // Guaranteed minimum needed.

		sb.append("<whoswhere>");

		sb.append("<client>");
		sb.append(getNickname());
		sb.append("</client>");

		sb.append("<projectname>");
		sb.append(getProjectName());
		sb.append("</projectname>");

		sb.append("<resource>");
		sb.append(getResourceName());
		sb.append("</resource>");

		sb.append("</whoswhere>");

		return sb.toString();
	}

}
