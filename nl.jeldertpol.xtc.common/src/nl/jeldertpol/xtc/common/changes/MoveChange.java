package nl.jeldertpol.xtc.common.changes;

/**
 * A resource has been moved.
 * 
 * @author Jeldert Pol
 */
public class MoveChange extends AbstractChange {

	private static final long serialVersionUID = 2L;

	private final String from;
	private final String to;

	/**
	 * A resource has been moved.
	 * 
	 * @param from
	 *            Full path of original resource location, must be portable.
	 * @param to
	 *            Full path of new resource location, must be portable.
	 * @param nickname
	 *            The nickname of the client the move originated from.
	 */
	public MoveChange(final String from, final String to,
			final String projectName, final String nickname) {
		super(projectName, nickname);

		this.from = from;
		this.to = to;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.jeldertpol.xtc.common.changes.AbstractChange#toString()
	 */
	@Override
	public String toString() {
		String string = "MoveChange: " + from + "\n\t to: " + to
				+ "\n\t client: " + getNickname();
		return string;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.jeldertpol.xtc.common.changes.AbstractChange#toXMLString()
	 */
	@Override
	public String toXMLString() {
		StringBuilder sb = new StringBuilder(65); // Guaranteed minimum needed.

		sb.append("<movechange>");

		sb.append("<from>");
		sb.append(from);
		sb.append("</from>");

		sb.append("<to>");
		sb.append(to);
		sb.append("</to>");

		sb.append("<client>");
		sb.append(getNickname());
		sb.append("</client>");

		sb.append("</movechange>");

		return sb.toString();
	}
}
