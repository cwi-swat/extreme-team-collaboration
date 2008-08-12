package nl.jeldertpol.xtc.common.changes;

/**
 * A resource has been removed.
 * 
 * @author Jeldert Pol
 * 
 */
public class RemovedResourceChange extends AbstractChange {
	private final String resourcePath;

	/**
	 * A resource has been removed.
	 * 
	 * @param resourcePath
	 *            The added resource, path is relative to the project, and
	 *            portable.
	 * @param nickname
	 *            The nickname of the client the removed resource originated
	 *            from.
	 */
	public RemovedResourceChange(final String resourcePath,
			final String nickname) {
		super(nickname);

		this.resourcePath = resourcePath;
	}

	/**
	 * @return the resourcePath
	 */
	public String getResourcePath() {
		return resourcePath;
	}

	@Override
	public String toString() {
		String string = "RemovedResource: " + resourcePath + "\n\t client: "
				+ getNickname();
		return string;
	}
}
