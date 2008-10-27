package nl.jeldertpol.xtc.common.chat;

import java.io.Serializable;

/**
 * A chat message.
 * 
 * @author Jeldert Pol
 */
public class ChatMessage implements Serializable {

	private static final long serialVersionUID = 3L;

	/**
	 * The project.
	 */
	private final String projectName;

	/**
	 * The nickname.
	 */
	private final String nickname;

	/**
	 * The message.
	 */
	private final String message;

	private long timestamp = 0L;

	/**
	 * A chat message.
	 * 
	 * @param nickname
	 *            The client who send the message.
	 * @param message
	 *            The message.
	 */
	public ChatMessage(final String projectName, final String nickname,
			final String message) {
		super();

		this.projectName = projectName;
		this.nickname = nickname;
		this.message = message;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return The timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            The timestamp to set
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
		return getNickname() + ": " + getMessage();
	}

	/**
	 * Returns a textual representation of the object, in XML format.
	 * 
	 * @return a textual representation of the object, in XML format.
	 */
	public String toXMLString() {
		StringBuilder sb = new StringBuilder(50); // Guaranteed minimum needed.

		sb.append("<chat>");

		sb.append("<client>");
		sb.append(getNickname());
		sb.append("</client>");

		sb.append("<message>");
		sb.append(getMessage());
		sb.append("</message>");

		sb.append("</chat>");

		return sb.toString();
	}
}
