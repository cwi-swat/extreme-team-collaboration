package nl.jeldertpol.xtc.server;

import java.util.ArrayList;
import java.util.List;

import nl.jeldertpol.xtc.common.changes.AbstractChange;
import nl.jeldertpol.xtc.common.changes.AddedResourceChange;
import nl.jeldertpol.xtc.common.changes.ContentChange;
import nl.jeldertpol.xtc.common.changes.MoveChange;
import nl.jeldertpol.xtc.common.changes.RemovedResourceChange;
import nl.jeldertpol.xtc.common.changes.TextualChange;
import nl.jeldertpol.xtc.common.conversion.Conversion;
import nl.jeldertpol.xtc.common.session.SimpleSession;
import nl.jeldertpol.xtc.server.session.Session;
import toolbus.adapter.java.AbstractJavaTool;
import aterm.ATerm;
import aterm.ATermBlob;
import aterm.ATermFactory;
import aterm.ATermLong;

/**
 * Server can send and receive messages from the Toolbus. Keeps a list of
 * current sessions.
 * 
 * @author Jeldert Pol
 */
public class Server extends AbstractJavaTool {

	private final ATermFactory factory = getFactory();

	/**
	 * Holds the current projects.
	 */
	private List<Session> sessions = new ArrayList<Session>();

	/**
	 * Starting point for XTC Server. Can be called directly from the Toolbus
	 * script. This supplies the correct args (as required by
	 * {@link AbstractJavaTool#connect(String[])}).
	 * 
	 * @param args
	 *            Arguments to connect to the Toolbus.
	 * @throws Exception
	 *             Thrown when something goes wrong during the parsing of the
	 *             arguments or the establishing of the connection.
	 */
	public static void main(final String[] args) throws Exception {
		System.out
				.println("Arguments: -TB_TOOL_NAME server -TB_HOST localhost -TB_PORT 60000");
		System.out.println("-TB_HOST and -TB_PORT can be redefined.");
		new Server().connect(args);
	}

	@Override
	public void receiveAckEvent(final ATerm term) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveTerminate(final ATerm term) {
		// TODO Auto-generated method stub

	}

	/**
	 * A client request all the sessions on the server.
	 * 
	 * @return An {@link ArrayList} with {@link SimpleSession}, inside an
	 *         {@link ATermBlob}
	 */
	public ATerm getSessions() {
		ArrayList<SimpleSession> simpleSessions = new ArrayList<SimpleSession>(
				sessions.size());

		// Downcasting / Converting Session to SimpleSession, to loose client
		// dependency on Session, and changes recorded.
		for (Session session : sessions) {
			String projectName = session.getProjectName();
			Long revision = session.getRevision();
			List<String> clients = session.getClients();

			SimpleSession simpleSession = new SimpleSession(projectName,
					revision);
			for (String client : clients) {
				simpleSession.addClient(client);
			}
			simpleSessions.add(simpleSession);
		}

		byte[] blob = Conversion.objectToByte(simpleSessions);
		ATermBlob termBlob = factory.makeBlob(blob);

		ATerm response = factory.make("getSessions(<term>)", termBlob);
		return response;
	}

	/**
	 * A client starts a new session.
	 * 
	 * @param projectName
	 *            Name of the project in the session.
	 * @param revisionTerm
	 *            An {@link ATermLong} containing the revision of the project.
	 * @param nickname
	 *            The nickname of the client.
	 * @return An {@link ATerm} containing a {@link Boolean}, representing the
	 *         success of this action.
	 */
	public ATerm startSession(final String projectName,
			final ATerm revisionTerm, final String nickname) {
		boolean success = false;

		// Convert ATerms to right ATerm
		ATermLong revisionTermLong = (ATermLong) revisionTerm;
		Long revision = revisionTermLong.getLong();

		// Check if project exists
		Session existingSession = getSession(projectName);
		if (existingSession == null) {
			// Create new session
			Session session = new Session(projectName, revision, nickname);
			sessions.add(session);
			success = true;
		}

		ATerm startSession = factory.make("startSession(<bool>)", success);
		return startSession;
	}

	/**
	 * A client joins an existing session.
	 * 
	 * @param projectName
	 *            Name of the project, to identify the session.
	 * @param nickname
	 *            The nickname of the client.
	 * @return An {@link ATerm} containing a {@link Boolean}, representing the
	 *         success of this action.
	 */
	public ATerm joinSession(final String projectName, final String nickname) {
		boolean success = false;

		// Check if project exists
		Session session = getSession(projectName);
		if (session != null) {
			// Check if nickname is available
			boolean available = nicknameAvailable(session, nickname);

			// Add client if nickname is available
			if (available) {
				session.addClient(nickname);
				success = true;
			}
		}

		ATerm joinSession = factory.make("joinSession(<bool>)", success);
		return joinSession;
	}

	/**
	 * Request all changes made in a session.
	 * 
	 * @param projectName
	 * @return
	 */
	public ATerm requestChanges(final String projectName) {
		List<AbstractChange> changes = new ArrayList<AbstractChange>();

		Session session = getSession(projectName);
		changes = session.getChanges();

		byte[] blob = Conversion.objectToByte(changes);
		ATermBlob termBlob = factory.makeBlob(blob);

		ATerm response = factory.make("requestChanges(<term>)", termBlob);
		return response;
	}

	/**
	 * Request most recent textual changes for a resource since last
	 * {@link ContentChange}.
	 * 
	 * @param projectName
	 * @param resource
	 * @return
	 */
	public ATerm requestTextualChanges(final String projectName,
			final String resource) {
		System.out.println("requestTextualChanges: " + resource);
		List<AbstractChange> changes = new ArrayList<AbstractChange>();

		Session session = getSession(projectName);
		List<AbstractChange> allChanges = session.getChanges();

		// Looping backwards through changes, until a ContentChange is found.
		for (int i = allChanges.size() - 1; i >= 0; i--) {
			AbstractChange change = allChanges.get(i);
			if (change instanceof TextualChange) {
				TextualChange textualChange = (TextualChange) change;
				if (textualChange.getFilename().equals(resource)) {
					changes.add(change);
				}
			} else if (change instanceof ContentChange) {
				ContentChange contentChange = (ContentChange) change;
				if (contentChange.getFilename().equals(resource)) {
					break;
				}
			}
		}

		// Since changes are backwards, order them again properly.
		List<AbstractChange> orderedChanges = new ArrayList<AbstractChange>(
				changes.size());
		for (int i = changes.size() - 1; i >= 0; i--) {
			orderedChanges.add(changes.get(i));
		}
		System.out.println("Size: " + orderedChanges.size());

		byte[] blob = Conversion.objectToByte(orderedChanges);
		ATermBlob termBlob = factory.makeBlob(blob);

		ATerm response = factory
				.make("requestTextualChanges(<term>)", termBlob);
		return response;
	}

	/**
	 * A client leaves an existing session.
	 * 
	 * @param projectName
	 *            Name of the project, to identify the session.
	 * @param nickname
	 *            The nickname of the client.
	 * @return An {@link ATerm} containing a {@link Boolean}, representing the
	 *         success of this action.
	 */
	public ATerm leaveSession(final String projectName, final String nickname) {
		boolean success = false;

		// Check if project exists
		Session session = getSession(projectName);
		if (session != null) {
			// Check if nickname is available
			boolean available = nicknameAvailable(session, nickname);

			// Remove client if nickname is present
			if (!available) {
				session.removeClient(nickname);
				success = true;
			}

			if (session.getClients().isEmpty()) {
				sessions.remove(session);
			}
		}

		ATerm leaveSession = factory.make("leaveSession(<bool>)", success);
		return leaveSession;
	}

	/**
	 * A client sends a change.
	 * 
	 * @param projectName
	 * @param filename
	 * @param length
	 * @param offset
	 * @param text
	 * @return
	 */
	public ATerm sendChange(final String projectName, final String filename,
			final int length, final int offset, final String text,
			final String nickname) {
		boolean success = false;

		Session session = getSession(projectName);
		if (session != null) {
			TextualChange change = new TextualChange(filename, length, offset,
					text, nickname);
			session.addChange(change);
			success = true;
		}

		ATerm sendChange = factory.make("sendChange(<bool>)", success);
		return sendChange;
	}

	/**
	 * A client sends a move.
	 * 
	 * @param projectName
	 * @param from
	 * @param to
	 * @param nickname
	 * @return
	 */
	public ATerm sendMove(final String projectName, final String from,
			final String to, final String nickname) {
		boolean success = false;

		Session session = getSession(projectName);
		if (session != null) {
			MoveChange change = new MoveChange(from, to, nickname);
			session.addChange(change);
			success = true;
		}

		ATerm sendMove = factory.make("sendMove(<bool>)", success);
		return sendMove;
	}

	public ATerm sendContent(final String projectName, final String filename,
			final ATerm content, final String nickname) {
		// bug in Toolbus, only needed for handshake.
		return null;
	}

	public ATerm sendContent(final String projectName, final String filename,
			final byte[] content, final String nickname) {
		boolean success = false;

		Session session = getSession(projectName);
		if (session != null) {
			ContentChange change = new ContentChange(filename, content,
					nickname);
			session.addChange(change);
			success = true;
		}

		ATerm sendChange = factory.make("sendContent(<bool>)", success);
		return sendChange;
	}

	public ATerm sendAddedResource(final String projectName,
			final String resourcePath, final int type, final String nickname) {
		boolean success = false;

		Session session = getSession(projectName);
		if (session != null) {
			AddedResourceChange change = new AddedResourceChange(resourcePath,
					type, nickname);
			session.addChange(change);
			success = true;
		}

		ATerm sendAddedResource = factory.make("sendAddedResource(<bool>)",
				success);
		return sendAddedResource;
	}

	public ATerm sendRemovedResource(final String projectName,
			final String resourcePath, final String nickname) {
		boolean success = false;

		Session session = getSession(projectName);
		if (session != null) {
			RemovedResourceChange change = new RemovedResourceChange(
					resourcePath, nickname);
			session.addChange(change);
			success = true;
		}

		ATerm sendRemovedResource = factory.make("sendRemovedResource(<bool>)",
				success);
		return sendRemovedResource;
	}

	/**
	 * Get a session.
	 * 
	 * @param projectName
	 *            The identifier of the session.
	 * @return The session, or <code>null</code> if it does not exist.
	 */
	private Session getSession(final String projectName) {
		Session foundSession = null;

		// Check if project exists
		for (Session session : sessions) {
			if (session.getProjectName().equals(projectName)) {
				foundSession = session;
				break;
			}
		}
		return foundSession;
	}

	/**
	 * Checks if the nickname is available in the gives session.
	 * 
	 * @param session
	 *            The session to check in.
	 * @param nickname
	 *            The nickname to check.
	 * @return <code>true</code> if nickname is available, <code>false</code>
	 *         otherwise.
	 */
	private boolean nicknameAvailable(final Session session,
			final String nickname) {
		// Check if nickname is available
		boolean available = true;
		for (String client : session.getClients()) {
			if (client.equals(nickname)) {
				available = false;
				break;
			}
		}
		return available;
	}
}
