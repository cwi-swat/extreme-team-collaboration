package nl.jeldertpol.xtc.client.session;

import java.util.List;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.LeaveSessionException;
import nl.jeldertpol.xtc.client.exceptions.NicknameAlreadyTakenException;
import nl.jeldertpol.xtc.client.exceptions.ProjectAlreadyPresentException;
import nl.jeldertpol.xtc.client.exceptions.UnableToConnectException;
import nl.jeldertpol.xtc.common.changes.AbstractChange;
import nl.jeldertpol.xtc.common.conversion.Conversion;
import nl.jeldertpol.xtc.common.session.SimpleSession;

import org.eclipse.core.runtime.IPath;

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
	public void receiveAckEvent(final ATerm term) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveTerminate(final ATerm term) {
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
	public void connect(final String host, final String port)
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
				.byteToObject(blob.getBlobData());

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
	public void startSession(final String projectName, final Long revision,
			final String nickname) throws ProjectAlreadyPresentException {
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
	 * @throws NicknameAlreadyTakenException
	 *             The nickname is already present in the session.
	 */
	public void joinSession(final String projectName, final String nickname)
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
	 * Request and apply all changes made so far from the server.
	 * 
	 * @param projectName
	 */
	public void requestChanges(final String projectName) {
		ATerm requestChanges = factory.make("requestChanges(<str>)",
				projectName);
		ATermAppl reply = sendRequest(requestChanges);

		ATermBlob blob = (ATermBlob) reply.getArgument(0);
		List<AbstractChange> changes = (List<AbstractChange>) Conversion
				.byteToObject(blob.getBlobData());

		Activator.SESSION.applyChanges(projectName, changes);
	}

	/**
	 * Request and apply textual changes made to this resource.
	 * 
	 * @param projectName
	 * @param resource
	 */
	public void requestTextualChanges(final String projectName,
			final String resource) {
		ATerm requestTextualChanges = factory.make(
				"requestTextualChanges(<str>, <str>)", projectName, resource);
		ATermAppl reply = sendRequest(requestTextualChanges);

		ATermBlob blob = (ATermBlob) reply.getArgument(0);
		List<AbstractChange> changes = (List<AbstractChange>) Conversion
				.byteToObject(blob.getBlobData());

		Activator.SESSION.applyChanges(projectName, changes);
	}

	/**
	 * Leave a session on the server.
	 * 
	 * @param projectName
	 *            The name of the project, to identify the session.
	 * @param nickname
	 *            The nickname of the client.
	 * 
	 * @throws LeaveSessionException
	 *             Something failed when leaving the session.
	 */
	public void leaveSession(final String projectName, final String nickname)
			throws LeaveSessionException {
		ATerm leaveSession = factory.make("leaveSession(<str>, <str>)",
				projectName, nickname);
		ATermAppl reply = sendRequest(leaveSession);

		ATerm answer = reply.getArgument(0);
		boolean success = Boolean.parseBoolean(answer.toString());

		if (!success) {
			throw new LeaveSessionException(projectName);
		}
	}

	/**
	 * Send a change to the server.
	 * 
	 * @param projectName
	 *            The name of the project the change originated from.
	 * @param filePath
	 *            The file, path must be relative to the project, and be
	 *            portable.
	 * @param length
	 *            Length of the replaced document text.
	 * @param offset
	 *            The document offset.
	 * @param text
	 *            Text inserted into the document.
	 * @param nickname
	 *            The nickname of the client the change originated from.
	 * 
	 * @see IPath#toPortableString()
	 */
	public void sendChange(final String projectName, final String filePath,
			final int length, final int offset, final String text,
			final String nickname) {
		ATerm sendChange = factory.make(
				"sendChange(<str>, <str>, <int>, <int>, <str>, <str>)",
				projectName, filePath, length, offset, text, nickname);
		ATermAppl reply = sendRequest(sendChange);

		ATerm answer = reply.getArgument(0);
		boolean success = Boolean.parseBoolean(answer.toString());

		if (!success) {
			// throw new LeaveSessionException(projectName);
		}
	}

	public void sendChange(final String projectName,
			final AbstractChange change, final String nickname) {
		byte[] blob = Conversion.objectToByte(change);
		ATermBlob changeTerm = factory.makeBlob(blob);
		
		ATerm sendChange = factory.make("sendChange(<str>, <term>, <str>)",
				projectName, changeTerm, nickname);
		ATermAppl reply = sendRequest(sendChange);

		ATerm answer = reply.getArgument(0);
		boolean success = Boolean.parseBoolean(answer.toString());

		if (!success) {
			//throw new LeaveSessionException(projectName);
		}
	}

	/**
	 * Receive a change from the server / other clients.
	 * 
	 * @param projectName
	 *            The name of the project the change originated from.
	 * @param changeTerm
	 *            An {@link ATermBlob} containing an {@link AbstractChange}.
	 * @param nickname
	 *            The nickname of the client the change originated from.
	 */
	public void receiveChange(final String projectName, final ATerm changeTerm,
			final String nickname) {
		System.out.println("Jeldert, toolbus is fixed, please remove other method!");
		System.exit(1);
		if (changeTerm.getType() == ATerm.BLOB) {
			ATermBlob blobTerm = (ATermBlob) changeTerm;
			AbstractChange change = (AbstractChange) Conversion
					.byteToObject(blobTerm.getBlobData());
			Activator.SESSION.applyChange(projectName, change);
		}
	}
	
	public void receiveChange(final String projectName, final byte[] changeBlob,
			final String nickname) {
			AbstractChange change = (AbstractChange) Conversion
					.byteToObject(changeBlob);
			Activator.SESSION.applyChange(projectName, change);
	}

}
