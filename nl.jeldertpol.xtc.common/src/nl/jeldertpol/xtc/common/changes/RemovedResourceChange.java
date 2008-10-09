package nl.jeldertpol.xtc.common.changes;

/**
 * A resource has been removed.
 * 
 * @author Jeldert Pol
 * 
 */
public class RemovedResourceChange extends AbstractChange {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	private final String resourceName;

	/**
	 * A resource has been removed.
	 * 
	 * @param resourceName
	 *            The added resource, path is relative to the project, and
	 *            portable.
	 * @param nickname
	 *            The nickname of the client the removed resource originated
	 *            from.
	 */
	public RemovedResourceChange(final String resourceName,
			final String projectName, final String nickname) {
		super(projectName, nickname);

		this.resourceName = resourceName;
	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.jeldertpol.xtc.common.changes.AbstractChange#toString()
	 */
	@Override
	public String toString() {
		String string = "RemovedResource: " + resourceName + "\n\t client: "
				+ getNickname();
		return string;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.jeldertpol.xtc.common.changes.AbstractChange#toXMLString()
	 */
	@Override
	public String toXMLString() {
		StringBuilder sb = new StringBuilder(94); // Guaranteed minimum needed.

		sb.append("<removedresourcechange>");

		sb.append("<resourcename>");
		sb.append(resourceName);
		sb.append("</resourcename>");

		sb.append("<client>");
		sb.append(getNickname());
		sb.append("</client>");

		sb.append("</removedresourcechange>");

		return sb.toString();
	}
}
