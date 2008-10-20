package nl.jeldertpol.xtc.server.session;

import java.util.ArrayList;
import java.util.List;

import nl.jeldertpol.xtc.common.changes.AbstractChange;
import nl.jeldertpol.xtc.common.session.SimpleSession;

/**
 * Representation of a project. Also holds changes.
 * 
 * @author Jeldert Pol
 */
public class Session extends SimpleSession {

	private final List<AbstractChange> changes;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new project.
	 * 
	 * @param projectName
	 *            The name of the project.
	 * @param revision
	 *            The revision of the project.
	 * @param nickname
	 *            The client that started this session.
	 */
	public Session(final String projectName, final Long revision,
			final String nickname) {
		super(projectName, revision);
		addClient(nickname);

		changes = new ArrayList<AbstractChange>();
	}

	/**
	 * Add a change to this session.
	 * 
	 * @param change
	 *            The change.
	 */
	public void addChange(final AbstractChange change) {
		change.setTimestamp(System.currentTimeMillis());
		changes.add(change);
	}

	/**
	 * Get all changes made in this session.
	 * 
	 * @return All changes, in order from first, to last.
	 */
	public List<AbstractChange> getChanges() {
		return changes;
	}

}
