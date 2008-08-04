package nl.jeldertpol.xtc.client.session;

import java.util.List;

import nl.jeldertpol.xtc.client.exceptions.NicknameAlreadyTakenException;
import nl.jeldertpol.xtc.client.exceptions.ProjectAlreadyPresentException;
import nl.jeldertpol.xtc.client.exceptions.UnableToConnectException;
import nl.jeldertpol.xtc.common.Conversion.Conversion;
import nl.jeldertpol.xtc.common.Session.SimpleSession;
import toolbus.adapter.java.AbstractJavaTool;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermBlob;
import aterm.ATermFactory;
import aterm.ATermLong;

/**
 * Abstraction of the Toolbus connection. Handles all communication between
 * client and server. This will also abstract away the Toolbus classes, and only
 * return classes useful to the client.
 * 
 * Not to be called from the client directly, but through {@link Session}.
 * 
 * @author Jeldert Pol
 */
public class Server extends AbstractJavaTool {

	private ATermFactory factory = getFactory();

	private final String toolname = "client";

	@Override
	public void receiveAckEvent(ATerm term) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveTerminate(ATerm term) {
		// TODO Auto-generated method stub

	}

	/**
	 * Connect to the Toolbus.
	 * 
	 * @param host
	 *            the address of the server.
	 * @param port
	 *            the port of the server.
	 * @throws UnableToConnectException
	 *             thrown when establishing the connection failed.
	 */
	public void connect(String host, String port)
			throws UnableToConnectException {
		try {
			String[] connectioninfo = { "-TB_TOOL_NAME", toolname, "-TB_HOST",
					host, "-TB_PORT", port };
			connect(connectioninfo);
		} catch (Exception ex) {
			// disconnect();
			throw new UnableToConnectException(ex.getMessage());
		}
	}

	/**
	 * Disconnect from the Toolbus.
	 */
	public void disconnect() {
		ATerm term = factory.make("Client disconnect...");
		super.disconnect(term);
	}

	/**
	 * Get the sessions from the server.
	 * 
	 * @return The sessions.
	 */
	public List<SimpleSession> getSessions() {
		ATerm getSessions = factory.make("getSessions");
		ATermAppl reply = sendRequest(getSessions);

		ATermBlob blob = (ATermBlob) reply.getArgument(0);
		List<SimpleSession> sessions = (List<SimpleSession>) Conversion
				.ByteToObject(blob.getBlobData());

		return sessions;
	}

	/**
	 * Start a new session on the server.
	 * 
	 * @param projectName
	 *            The name of the project, to identify the session.
	 * @param revision
	 *            The revision of the project.
	 * @param nickname
	 *            The nickname of the client.
	 * @throws ProjectAlreadyPresentException
	 *             The project is already present on the server.
	 */
	public void startSession(String projectName, Long revision, String nickname)
			throws ProjectAlreadyPresentException {
		ATermLong revisionLong = factory.makeLong(revision);

		ATerm startSession = factory.make("startSession(<str>, <term>, <str>)",
				projectName, revisionLong, nickname);
		ATermAppl reply = sendRequest(startSession);

		ATerm answer = reply.getArgument(0);
		boolean success = Boolean.parseBoolean(answer.toString());

		if (!success) {
			throw new ProjectAlreadyPresentException(projectName);
		}
	}

	/**
	 * Join a session on the server.
	 * 
	 * @param projectName
	 *            The name of the project, to identify the session.
	 * @param nickname
	 *            The nickname of the client.
	 * @throws NicknameAlreadyTakenException The nickname is already present in the session.
	 */
	public void joinSession(String projectName, String nickname)
			throws NicknameAlreadyTakenException {
		ATerm joinSession = factory.make("joinSession(<str>, <str>)",
				projectName, nickname);
		ATermAppl reply = sendRequest(joinSession);

		ATerm answer = reply.getArgument(0);
		boolean success = Boolean.parseBoolean(answer.toString());

		if (!success) {
			// Everything else should already be checked, this is the only
			// exception left.
			throw new NicknameAlreadyTakenException(nickname);
		}
	}

	/**
	 * Leave a session on the server.
	 * 
	 * @param projectName
	 *            The name of the project, to identify the session.
	 * @param nickname
	 *            The nickname of the client.
	 * @return <code>true</code> is everything went ok, <code>false</code>
	 *         otherwise.
	 */
	public boolean leaveSession(String projectName, String nickname) {
		ATerm leaveSession = factory.make("leaveSession(<str>, <str>)",
				projectName, nickname);
		ATermAppl reply = sendRequest(leaveSession);

		ATerm answer = reply.getArgument(0);
		boolean success = Boolean.parseBoolean(answer.toString());
		return success;
		// TODO testen
	}

	// /**
	// * Set the nickname on the server.
	// *
	// * @param nickname
	// * the nickname to be set.
	// * @throws NicknameAlreadyTakenException
	// * thrown when nickname is already taken.
	// */
	// private void setNickname(String nickname)
	// throws NicknameAlreadyTakenException {
	// ATerm setNickname = factory.make("setNickname(<str>)", nickname);
	// ATermAppl response = sendRequest(setNickname);
	//
	// ATerm answer = response.getArgument(0);
	// boolean set = Boolean.parseBoolean(answer.toString());
	//
	// if (!set) {
	// throw new NicknameAlreadyTakenException(nickname);
	// }
	// }
	//
	// /**
	// * Get the revision of the project from the server.
	// *
	// * @param project
	// * the project to get the revision from.
	// * @return the revision of the project on the server.
	// * @throws ProjectNotPresentException
	// * thrown if the project does not exist on server.
	// */
	// public Long getRevision(String project) throws ProjectNotPresentException
	// {
	// ATerm getRevision = factory.make("getRevision(<str>)", project);
	// ATermAppl revision = sendRequest(getRevision);
	//
	// ATermLong answer = (ATermLong) revision.getArgument(0);
	// if (answer.getLong() > -1L) {
	// return answer.getLong();
	// } else {
	// throw new ProjectNotPresentException(project);
	// }
	// }

}
