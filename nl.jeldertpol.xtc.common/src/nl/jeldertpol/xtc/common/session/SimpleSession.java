package nl.jeldertpol.xtc.common.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a session. Only holds the name and revision of project, and
 * clients.
 * 
 * @author Jeldert Pol
 */
public class SimpleSession implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String projectName;
	private final Long revision;
	private final List<String> clients;

	/**
	 * Create a new session.
	 * 
	 * Not to be called by clients: they need to call
	 * {@link SimpleSession#SimpleSession(String, Long, String)}
	 * 
	 * @param projectName
	 *            The name of the project.
	 * @param revision
	 *            The revision of the project.
	 */
	public SimpleSession(final String projectName, final Long revision) {
		super();
		this.projectName = projectName;
		this.revision = revision;

		clients = new ArrayList<String>();
	}

	/**
	 * Get the name of this project.
	 * 
	 * @return The name of this project.
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * The revision of this project (at the time of creation).
	 * 
	 * @return The revision.
	 */
	public Long getRevision() {
		return revision;
	}

	/**
	 * Return the clients.
	 * 
	 * @return The clients.
	 */
	public List<String> getClients() {
		return clients;
	}

	/**
	 * Add a client to the project.
	 * 
	 * @param nickname
	 *            Nickname of the client.
	 */
	public void addClient(final String nickname) {
		clients.add(nickname);
	}

	/**
	 * Remove a client from a project.
	 * 
	 * @param nickname
	 *            Nickname of the client.
	 */
	public void removeClient(final String nickname) {
		clients.remove(nickname);
	}

}
