package nl.jeldertpol.xtc.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import nl.jeldertpol.xtc.common.changes.AbstractChange;
import nl.jeldertpol.xtc.common.changes.ContentChange;
import nl.jeldertpol.xtc.common.changes.TextualChange;
import nl.jeldertpol.xtc.common.conversion.Conversion;
import nl.jeldertpol.xtc.common.exceptions.NicknameAlreadyTakenException;
import nl.jeldertpol.xtc.common.exceptions.WrongRevisionException;
import nl.jeldertpol.xtc.common.exceptions.XtcException;
import nl.jeldertpol.xtc.common.logging.Logger;
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
	private final List<Session> sessions = new ArrayList<Session>();

	private static final Logger LOGGER = new Logger(Logger.LogType.XML);

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
		LOGGER.log(Level.INFO, "receiveAckEvent: " + term.toString());
	}

	@Override
	public void receiveTerminate(final ATerm term) {
		LOGGER.log(Level.INFO, "receiveTerminate: " + term.toString());
	}

	/**
	 * A client request all the sessions on the server.
	 * 
	 * @return An {@link ArrayList} with {@link SimpleSession}, inside an
	 *         {@link ATermBlob}
	 */
	public ATerm getSessions() {
		LOGGER.log(Level.INFO, "Requested sessions.");

		List<SimpleSession> simpleSessions = getSimpleSessions();

		byte[] blob = Conversion.objectToByte(simpleSessions);

		ATerm response = factory.make("getSessions(<blob>)", blob);
		return response;
	}

	public List<SimpleSession> getSimpleSessions() {
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

		return simpleSessions;
	}

	/**
	 * A client wants to start or join a session.
	 * 
	 * @param projectName
	 *            Name of the project in the session.
	 * @param revisionTerm
	 *            An {@link ATermLong} containing the revision of the project.
	 * @param nickname
	 *            The nickname of the client.
	 * @return An {@link ATerm} containing an {@link XtcException}, either
	 *         {@link NicknameAlreadyTakenException} or
	 *         {@link WrongRevisionException}.
	 */
	public ATerm startJoinSession(final String projectName,
			final ATerm revisionTerm, final String nickname) {
		LOGGER.log(Level.INFO, "StartJoin session (" + projectName + ", "
				+ nickname + ").");

		XtcException exception = null;

		// Convert ATerms to right ATerm
		ATermLong revisionTermLong = (ATermLong) revisionTerm;
		Long revision = revisionTermLong.getLong();

		// Check if project exists
		Session session = getSession(projectName);
		if (session == null) {
			// Create new session
			session = new Session(projectName, revision, nickname);
			sessions.add(session);
		} else {
			// Check if revision is the same.
			if (session.getRevision().equals(revision)) {
				// Check if nickname is available
				if (nicknameAvailable(session, nickname)) {
					// Join the session.
					session.addClient(nickname);
				} else {
					exception = new NicknameAlreadyTakenException(nickname);
				}
			} else {
				exception = new WrongRevisionException(revision, session
						.getRevision());
			}
		}

		byte[] blob = Conversion.objectToByte(exception);

		ATerm response = factory.make("startJoinSession(<blob>)", blob);
		return response;
	}

	/**
	 * Request all changes made in a session.
	 * 
	 * @param projectName
	 * @return
	 */
	public ATerm requestChanges(final String projectName) {
		LOGGER.log(Level.INFO, "Requesting changes (" + projectName + ").");

		Session session = getSession(projectName);
		List<AbstractChange> sessionChanges = session.getChanges();
		List<AbstractChange> relevantChanges = new ArrayList<AbstractChange>();

		// From start, add changes
		for (AbstractChange change : sessionChanges) {
			// If contentchange is found:
			if (change instanceof ContentChange) {
				// Previous textual changes can be ignored.
				// Previous contentchanges can also be ignored.

				List<AbstractChange> toBeRemoved = new ArrayList<AbstractChange>();

				for (AbstractChange relevantChange : relevantChanges) {
					if (relevantChange instanceof ContentChange) {
						// For same file?
						ContentChange thisChange = (ContentChange) change;
						ContentChange previousChange = (ContentChange) relevantChange;

						if (previousChange.getFilename().equals(
								thisChange.getFilename())) {
							toBeRemoved.add(previousChange);
						}
					} else if (relevantChange instanceof TextualChange) {
						// For same file?
						ContentChange thisChange = (ContentChange) change;
						TextualChange previousChange = (TextualChange) relevantChange;

						if (previousChange.getFilename().equals(
								thisChange.getFilename())) {
							toBeRemoved.add(previousChange);
						}
					}
				}
				relevantChanges.removeAll(toBeRemoved);
			}
			relevantChanges.add(change);
		}

		byte[] blob = Conversion.objectToByte(relevantChanges);

		ATerm response = factory.make("requestChanges(<blob>)", blob);
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
		LOGGER.log(Level.INFO, "Requesting textual changes (" + projectName
				+ ", " + resource + ").");

		List<AbstractChange> changes = new ArrayList<AbstractChange>();

		Session session = getSession(projectName);
		List<AbstractChange> allChanges = session.getChanges();

		// Looping backwards through changes, until a ContentChange is found.
		for (int i = allChanges.size() - 1; i >= 0; i--) {
			AbstractChange change = allChanges.get(i);
			if (change instanceof TextualChange) {
				TextualChange textualChange = (TextualChange) change;
				if (textualChange.getFilename().equals(resource)) {
					changes.add(textualChange);
				}
			} else if (change instanceof ContentChange) {
				ContentChange contentChange = (ContentChange) change;
				if (contentChange.getFilename().equals(resource)) {
					break;
				}
			}
		}

		// Since changes are backwards, order them again properly.
		Collections.reverse(changes);

		byte[] blob = Conversion.objectToByte(changes);

		ATerm response = factory.make("requestTextualChanges(<blob>)", blob);
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
		LOGGER.log(Level.INFO, "Leaving session (" + projectName + ", "
				+ nickname + ").");

		boolean success = false;

		// Check if project exists
		Session session = getSession(projectName);
		if (session != null) {
			// Remove client
			session.removeClient(nickname);
			success = true;

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
	 *            Name of the project, to identify the session.
	 * @param changeBlob
	 *            Serialized {@link AbstractChange}.
	 * @param nickname
	 *            The nickname of the client.
	 * 
	 * @return <code>true</code> if the session was found, and changeTerm
	 *         contains an {@link AbstractChange}, <code>false</code> otherwise.
	 */
	public ATerm sendChange(final String projectName, final byte[] changeBlob,
			final String nickname) {
		LOGGER.log(Level.INFO, "Client send a change (" + projectName + ", "
				+ nickname + ").");
		boolean success = false;

		Session session = getSession(projectName);
		if (session != null) {
			AbstractChange change = (AbstractChange) Conversion
					.byteToObject(changeBlob);
			session.addChange(change);
			success = true;
			LOGGER.log(Level.FINE, change.toXMLString());
		}

		ATerm sendChange = factory.make("sendChange(<bool>)", success);
		return sendChange;
	}

	/**
	 * Log WhosWhere.
	 * 
	 * @param nickname
	 *            The client viewing the resource.
	 * @param projectName
	 *            The project the resource belongs to.
	 * @param resource
	 *            The viewed resource, path must be relative to the project, and
	 *            portable.
	 */
	public void whosWhere(final String nickname, final String projectName,
			final String resource) {
		StringBuilder sb = new StringBuilder(89); // Guaranteed minimum needed.

		sb.append("<whoswhere>");

		sb.append("<client>");
		sb.append(nickname);
		sb.append("</client>");

		sb.append("<projectname>");
		sb.append(projectName);
		sb.append("</projectname>");

		sb.append("<resource>");
		sb.append(resource);
		sb.append("</resource>");

		sb.append("</whoswhere>");

		LOGGER.log(Level.FINE, sb.toString());
	}

	/**
	 * Log chat.
	 * 
	 * @param nickname
	 *            The nickname of the client the chat message originated from.
	 * @param message
	 *            The message.
	 */
	public void chat(final String nickname, final String message) {
		StringBuilder sb = new StringBuilder(50); // Guaranteed minimum needed.

		sb.append("<chat>");

		sb.append("<client>");
		sb.append(nickname);
		sb.append("</client>");

		sb.append("<message>");
		sb.append(message);
		sb.append("</message>");

		sb.append("</chat>");

		LOGGER.log(Level.FINE, sb.toString());
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
