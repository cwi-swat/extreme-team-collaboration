package nl.jeldertpol.xtc.common.changes;

/**
 * The content of a resource has been changed.
 * 
 * @author Jeldert Pol
 */
public class ContentChange extends AbstractChange {

	private static final long serialVersionUID = 2L;

	private final String filename;
	private final byte[] content;

	/**
	 * The content of a resource has been changed.
	 * 
	 * @param filename
	 *            The file the content belongs to, path is relative to the
	 *            project, and portable.
	 * @param content
	 *            The actual content.
	 * @param nickname
	 *            The nickname of the client the content originated from.
	 */
	public ContentChange(final String filename, final byte[] content,
			final String projectName, final String nickname) {
		super(projectName, nickname);

		this.filename = filename;
		this.content = content;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the content
	 */
	public byte[] getContent() {
		return content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.jeldertpol.xtc.common.changes.AbstractChange#toString()
	 */
	@Override
	public String toString() {
		String string = "ContentChange: " + filename + "\n\t size: "
				+ content.length + "\n\t client: " + getNickname();
		return string;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.jeldertpol.xtc.common.changes.AbstractChange#toXMLString()
	 */
	@Override
	public String toXMLString() {
		StringBuilder sb = new StringBuilder(83); // Guaranteed minimum needed.

		sb.append("<contentchange>");

		sb.append("<filename>");
		sb.append(filename);
		sb.append("</filename>");

		sb.append("<size>");
		sb.append(content.length);
		sb.append("</size>");

		sb.append("<client>");
		sb.append(getNickname());
		sb.append("</client>");

		sb.append("</contentchange>");

		return sb.toString();
	}

}
