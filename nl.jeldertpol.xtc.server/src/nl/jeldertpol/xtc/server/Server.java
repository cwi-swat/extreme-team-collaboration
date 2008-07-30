package nl.jeldertpol.xtc.server;

import java.util.ArrayList;

import nl.jeldertpol.xtc.server.client.Client;
import nl.jeldertpol.xtc.server.project.Project;
import toolbus.adapter.java.AbstractJavaTool;
import aterm.ATerm;
import aterm.ATermBlob;
import aterm.ATermFactory;
import aterm.ATermLong;

/**
 * @author Jeldert Pol
 * 
 */
public class Server extends AbstractJavaTool {

	private ATermFactory factory = getFactory();

	/**
	 * Holds the connected clients.
	 */
	private ArrayList<Client> clients = new ArrayList<Client>();

	/**
	 * Holds the current projects.
	 */
	private ArrayList<Project> projects = new ArrayList<Project>();

	/**
	 * Starting point for XTC Server. Can be called directly from the Toolbus
	 * script. This supplies the correct args (as required by
	 * {@link AbstractJavaTool#connect(String[])}).
	 * 
	 * @param args
	 *            arguments to connect to the Toolbus.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new Server().connect(args);
	}

	@Override
	public void receiveAckEvent(ATerm term) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveTerminate(ATerm term) {
		// TODO Auto-generated method stub

	}
	
	public ATerm startSession(String name, ATermLong revision, ATermBlob resources) {
				
		
		ATerm sessionStart = factory.make("sessionStart(<bool>)", true);
		return sessionStart;
	}

	/**
	 * A client wants to set its nickname.
	 * 
	 * @param nickname
	 *            the nickname to be set
	 * @return a boolean, <code>true</code> when nickname is set,
	 *         <code>false</code> when nickname is already taken.
	 */
	public ATerm setNickname(String nickname) {
		System.out.println("server nickname: " + nickname);

		boolean valid = true;
		for (Client client : clients) {
			if (client.getNickname().equals(nickname)) {
				valid = false;
				break;
			}
		}
		if (valid) {
			clients.add(new Client(nickname));
		}

		ATerm nicknameSet = factory.make("nicknameSet(<bool>)", valid);
		return nicknameSet;
	}

	/**
	 * A client requests the revision of a project.
	 * 
	 * @param projectName
	 *            the project to get the revision from.
	 * @return a {@link Long}, containing the revision, or -1 if the project is
	 *         not present.
	 */
	public ATerm getRevision(String projectName) {
		System.out.println("server revision: " + projectName);

		Long revision = -1L;
		for (Project project : projects) {
			if (project.getProjectName().equals(projectName)) {
				revision = project.getRevision();
				break;
			}
		}

		ATermLong atermRevision = factory.makeLong(revision);

		ATerm gotRevision = factory.make("gotRevision(<term>)", atermRevision);
		return gotRevision;
	}

	/**
	 * A client is disconnected.
	 * 
	 * @param nickname
	 *            The nickname of the disconnected client.
	 */
	public void disconnect(String nickname) {
		Client clientToDisconnect = null;
		for (Client client : clients) {
			if (client.getNickname().equals(nickname)) {
				clientToDisconnect = client;
				break;
			}
		}
		clients.remove(clientToDisconnect);
	}

}
