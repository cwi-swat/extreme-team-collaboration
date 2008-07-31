package nl.jeldertpol.xtc.server.session;

import nl.jeldertpol.xtc.common.Session.SimpleSession;


/**
 * Representation of a project. Also holds changes.
 * 
 * @author Jeldert Pol
 */
public class Session extends SimpleSession {

	//private HashMap<String, LinkedList<Change>> changes;
	// TODO changes bijhouden

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
	public Session(String projectName, Long revision, String nickname) {
		// naam project, revision, resources, changes to resources,
		super(projectName, revision, nickname);

		//changes = new HashMap<String, LinkedList<Change>>(resources.size());
	}

}
