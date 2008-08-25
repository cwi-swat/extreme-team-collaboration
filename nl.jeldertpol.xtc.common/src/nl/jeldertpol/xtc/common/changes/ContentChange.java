package nl.jeldertpol.xtc.common.changes;

/**
 * The content of a resource has been changed.
 * 
 * @author Jeldert Pol
 */
public class ContentChange extends AbstractChange {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	@Override
	public String toString() {
		String string = "ContentChange: " + filename + "\n\t size: "
				+ content.length + "\n\t client: " + getNickname();
		return string;
	}

}
