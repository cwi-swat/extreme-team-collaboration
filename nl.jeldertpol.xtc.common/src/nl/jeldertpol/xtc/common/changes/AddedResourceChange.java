package nl.jeldertpol.xtc.common.changes;

/**
 * A resource has been added.
 * 
 * @author Jeldert Pol
 */
public class AddedResourceChange extends AbstractChange {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String resourceName;
	private final int type;

	/**
	 * A resource has been added.
	 * 
	 * @param resourceName
	 *            The added resource, path is relative to the project, and
	 *            portable.
	 * @param type
	 *            The type of resource added.
	 * @param nickname
	 *            The nickname of the client the added resource originated from.
	 */
	public AddedResourceChange(final String resourceName, final int type,
			final String nickname) {
		super(nickname);

		this.resourceName = resourceName;
		this.type = type;
	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	@Override
	public String toString() {
		String string = "AddedResource: " + resourceName + "\n\t type: " + type
				+ "\n\t client: " + getNickname();
		return string;
	}

}
