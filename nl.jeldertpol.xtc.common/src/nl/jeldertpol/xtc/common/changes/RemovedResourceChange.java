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
	private static final long serialVersionUID = 1L;

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

	@Override
	public String toString() {
		String string = "RemovedResource: " + resourceName + "\n\t client: "
				+ getNickname();
		return string;
	}
}
