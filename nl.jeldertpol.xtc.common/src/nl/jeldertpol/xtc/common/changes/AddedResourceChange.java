package nl.jeldertpol.xtc.common.changes;

/**
 * A resource has been added.
 * 
 * @author Jeldert Pol
 */
public class AddedResourceChange extends AbstractChange {
	private final String resourcePath;
	private final int type;

	/**
	 * A resource has been added.
	 * 
	 * @param resourcePath
	 *            The added resource, path is relative to the project, and
	 *            portable.
	 * @param type
	 *            The type of resource added.
	 * @param nickname
	 *            The nickname of the client the added resource originated from.
	 */
	public AddedResourceChange(final String resourcePath, final int type,
			final String nickname) {
		super(nickname);

		this.resourcePath = resourcePath;
		this.type = type;
	}

	/**
	 * @return the resourcePath
	 */
	public String getResourcePath() {
		return resourcePath;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	@Override
	public String toString() {
		String string = "AddedResource: " + resourcePath + "\n\t type: " + type
				+ "\n\t client: " + getNickname();
		return string;
	}

}
