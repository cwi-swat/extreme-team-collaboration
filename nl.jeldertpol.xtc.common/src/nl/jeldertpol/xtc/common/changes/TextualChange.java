package nl.jeldertpol.xtc.common.changes;

/**
 * The text in an editor changed.
 * 
 * @author Jeldert Pol
 */
public class TextualChange extends AbstractChange {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String filename;
	private final int length;
	private final int offset;
	private final String text;

	/**
	 * The text in an editor changed.
	 * 
	 * @param filename
	 *            The file, path must be relative to the project, and be
	 *            portable.
	 * @param length
	 *            Length of the replaced document text.
	 * @param offset
	 *            The document offset.
	 * @param text
	 *            Text inserted into the document.
	 * @param nickname
	 *            The nickname of the client the change originated from.
	 */
	public TextualChange(final String filename, final int length,
			final int offset, final String text, final String nickname) {
		super(nickname);

		this.filename = filename;
		this.length = length;
		this.offset = offset;
		this.text = text;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		String string = "TextualChange: " + filename + "\n\t length: " + length
				+ "\n\t offset: " + offset + "\n\t text: " + text
				+ "\n\t client: " + getNickname();
		return string;
	}

}
