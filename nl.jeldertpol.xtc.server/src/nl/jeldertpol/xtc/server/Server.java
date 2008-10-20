package nl.jeldertpol.xtc.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import nl.jeldertpol.xtc.common.changes.AbstractChange;
import nl.jeldertpol.xtc.common.changes.ContentChange;
import nl.jeldertpol.xtc.common.changes.TextualChange;
import nl.jeldertpol.xtc.common.chat.ChatMessage;
import nl.jeldertpol.xtc.common.conversion.Conversion;
import nl.jeldertpol.xtc.common.exceptions.NicknameAlreadyTakenException;
import nl.jeldertpol.xtc.common.exceptions.WrongRevisionException;
import nl.jeldertpol.xtc.common.exceptions.XtcException;
import nl.jeldertpol.xtc.common.logging.FileLogger;
import nl.jeldertpol.xtc.common.logging.Logger;
import nl.jeldertpol.xtc.common.logging.FileLogger.LogType;
import nl.jeldertpol.xtc.common.session.SimpleSession;
import nl.jeldertpol.xtc.server.session.Session;
import toolbus.adapter.java.AbstractJavaTool;
import aterm.ATerm;
import aterm.ATermBlob;
import aterm.ATermFactory;
import aterm.ATermLong;

/**
 * Server can send and receive messages from the ToolBus. Keeps a list of
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

	/**
	 * Holds the chat messages.
	 */
	private final List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();

	private static final Logger LOGGER = new FileLogger(LogType.XML);

	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.jeldertpol.xtc.server.Server#Server(String[])
	 */
	public static void main(final String[] args) throws InterruptedException,
			Exception {
		new Server(args);
	}

	/**
	 * Start a XTC server. Can connect to an external ToolBus, or start an
	 * internal one.
	 * 
	 * @param args
	 *            The arguments needed for setting up the connection.
	 * @throws InterruptedException
	 *             When starting an internal ToolBus, a pause in performed, to
	 *             let the ToolBus start.
	 * @throws Exception
	 *             Thrown when something goes wrong during the parsing of the
	 *             arguments or the establishing of the connection.
	 * 
	 * @see #printHelp()
	 */
	public Server(final String[] args) throws InterruptedException, Exception {
		printHelp();

		boolean localToolbus = false;
		boolean debugToolbus = false;

		String argumentsToolbusServer[] = { "", "" };
		String argumentsToolbus[] = { "", "", "", "" };

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (arg.equals("-TB_HOST")) {
				argumentsToolbus[0] = "-TB_HOST";
				argumentsToolbus[1] = args[i + 1];
			} else if (arg.equals("-TB_PORT")) {
				argumentsToolbusServer[0] = "-P" + args[i + 1];

				argumentsToolbus[2] = "-TB_PORT";
				argumentsToolbus[3] = args[i + 1];
			} else if (arg.equals("-LOCAL")) {
				localToolbus = true;
			} else if (arg.equals("-DEBUG")) {
				debugToolbus = true;
			} else if (arg.equals("-S")) {
				argumentsToolbusServer[1] = "-S" + args[i + 1];
			}
		}

		if (debugToolbus || localToolbus) {
			Thread toolbusThread = new Thread(new ToolbusThread(
					argumentsToolbusServer, debugToolbus));
			toolbusThread.start();
			Thread.sleep(1000);
		}

		connect(argumentsToolbus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see toolbus.adapter.java.AbstractJavaTool#connect(java.lang.String[])
	 */
	@Override
	public void connect(String[] args) throws Exception {
		String[] arguments = new String[args.length + 2];

		arguments[0] = "-TB_TOOL_NAME";
		arguments[1] = "server";

		for (int i = 0; i < args.length; i++) {
			arguments[i + 2] = args[i];
		}

		super.connect(arguments);
	}

	/**
	 * Prints help about how to start the server.
	 */
	public static void printHelp() {
		System.out
				.println("Arguments: -TB_HOST localhost -TB_PORT 60000 (-LOCAL/-DEBUG -S scriptname)");
		System.out.println("-TB_HOST gives the host of the ToolBus.");
		System.out.println("-TB_PORT gives the port.");
		System.out
				.println("If -LOCAL is provided, the ToolBus will be started internally.");
		System.out
				.println("If -DEBUG is provided, the ToolBus will be started internally, and run in debug mode.");
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
				// Saving all changes
				LOGGER.log(Level.INFO, "Saving session.");
				saveSession(session);

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
	 * Log chat.
	 * 
	 * @param nickname
	 *            The nickname of the client the chat message originated from.
	 * @param message
	 *            The message.
	 */
	public ATerm sendChat(final byte[] chatBlob) {
		ChatMessage chatMessage = (ChatMessage) Conversion
				.byteToObject(chatBlob);

		chatMessage.setTimestamp(System.currentTimeMillis());
		
		chatMessages.add(chatMessage);
		LOGGER.log(Level.FINE, chatMessage.toXMLString());

		byte[] blob = Conversion.objectToByte(chatMessage);

		ATerm response = factory.make("sendChat(<blob>)", blob);
		return response;
	}
	
	public void saveChatMessages() {
		try {
			String filename = "chatMessages" + System.currentTimeMillis();

			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(chatMessages);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	/**
	 * Save a session to a file.
	 * 
	 * @param session
	 *            The session to be saved.
	 */
	private void saveSession(final Session session) {
		try {
			String filename = session.getProjectName() + session.getRevision();

			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(session);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load a {@link Session} from a file.
	 * 
	 * @param filename
	 *            The session to be loaded.
	 * @return The {@link Session}, or <code>null</code>.
	 */
	private Session loadSession(final String filename) {
		Session session = null;

		try {
			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			session = (Session) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return session;
	}

}
