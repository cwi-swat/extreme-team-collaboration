package nl.jeldertpol.xtc.common.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a project. Only holds projectname, revision and clients.
 * 
 * @author Jeldert Pol
 */
public class SimpleSession {

	private String projectName;
	private Long revision;
	private ArrayList<String> clients;

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
	public SimpleSession(String projectName, Long revision, String nickname) {
		// naam project, revision, resources, changes to resources,
		super();
		this.projectName = projectName;
		this.revision = revision;

		clients = new ArrayList<String>();
		addClient(nickname);
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
	public void addClient(String nickname) {
		clients.add(nickname);
	}

	/**
	 * Remove a client from a project.
	 * 
	 * @param nickname
	 *            Nickname of the client.
	 */
	public void removeClient(String nickname) {
		clients.remove(nickname);
	}

}
