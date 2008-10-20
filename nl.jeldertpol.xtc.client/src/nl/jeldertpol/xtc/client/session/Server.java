package nl.jeldertpol.xtc.client.session;

import java.util.List;
import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.LeaveSessionException;
import nl.jeldertpol.xtc.client.exceptions.UnableToConnectException;
import nl.jeldertpol.xtc.common.changes.AbstractChange;
import nl.jeldertpol.xtc.common.chat.ChatMessage;
import nl.jeldertpol.xtc.common.conversion.Conversion;
import nl.jeldertpol.xtc.common.exceptions.NicknameAlreadyTakenException;
import nl.jeldertpol.xtc.common.exceptions.WrongRevisionException;
import nl.jeldertpol.xtc.common.exceptions.XtcException;
import nl.jeldertpol.xtc.common.session.SimpleSession;
import nl.jeldertpol.xtc.common.whosWhere.WhosWhere;
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
	// TODO extend Eclipse Blaa Tool

	private final ATermFactory factory = getFactory();

	private final String toolname = "client";

	@Override
	public void receiveAckEvent(final ATerm term) {
		Activator.getLogger().log(Level.INFO,
				"receiveAckEvent: " + term.toString());
	}

	@Override
	public void receiveTerminate(final ATerm term) {
		Activator.getLogger().log(Level.INFO,
				"receiveTerminate: " + term.toString());
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
	 * Start or join a session on the server.
	 * 
	 * @param projectName
	 *            The name of the project, to identify the session.
	 * @param revision
	 *            The revision of the project.
	 * @param nickname
	 *            The nickname of the client.
	 * 
	 * @throws NicknameAlreadyTakenException
	 *             The nickname is already present in the session.
	 * @throws WrongRevisionException
	 *             The revision of the project does not match the revision of
	 *             the server.
	 */
	public void startJoinSession(final String projectName, final Long revision,
			final String nickname) throws NicknameAlreadyTakenException,
			WrongRevisionException {
		ATermLong revisionLong = factory.makeLong(revision);

		ATerm startJoinSession = factory.make(
				"startJoinSession(<str>, <term>, <str>)", projectName,
				revisionLong, nickname);
		ATermAppl reply = sendRequest(startJoinSession);

		ATermBlob blob = (ATermBlob) reply.getArgument(0);
		XtcException exception = (XtcException) Conversion.byteToObject(blob
				.getBlobData());

		// Is null when nothing went wrong
		if (exception != null) {
			if (exception instanceof NicknameAlreadyTakenException) {
				throw (NicknameAlreadyTakenException) exception;
			} else if (exception instanceof WrongRevisionException) {
				throw (WrongRevisionException) exception;
			} else {
				Activator.getLogger().log(Level.SEVERE, exception);
			}
		}
	}

	/**
	 * TODO javadoc Request and apply all changes made so far from the server.
	 * 
	 * @param projectName
	 */
	public List<AbstractChange> requestChanges(final String projectName) {
		ATerm requestChanges = factory.make("requestChanges(<str>)",
				projectName);
		ATermAppl reply = sendRequest(requestChanges);

		ATermBlob blob = (ATermBlob) reply.getArgument(0);
		List<AbstractChange> changes = (List<AbstractChange>) Conversion
				.byteToObject(blob.getBlobData());

		return changes;
	}

	/**
	 * TODO javadoc Request and apply textual changes made to this resource.
	 * 
	 * @param projectName
	 * @param resource
	 */
	public List<AbstractChange> requestTextualChanges(final String projectName,
			final String resource) {
		ATerm requestTextualChanges = factory.make(
				"requestTextualChanges(<str>, <str>)", projectName, resource);
		ATermAppl reply = sendRequest(requestTextualChanges);

		ATermBlob blob = (ATermBlob) reply.getArgument(0);
		List<AbstractChange> changes = (List<AbstractChange>) Conversion
				.byteToObject(blob.getBlobData());

		return changes;
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
	 * @param change
	 *            The actual change.
	 * @param nickname
	 *            The nickname of the client the change originated from.
	 */
	public void sendChange(final String projectName,
			final AbstractChange change, final String nickname) {
		byte[] blob = Conversion.objectToByte(change);

		ATerm sendChange = factory.make("sendChange(<str>, <blob>, <str>)",
				projectName, blob, nickname);
		ATermAppl reply = sendRequest(sendChange);

		ATerm answer = reply.getArgument(0);
		boolean success = Boolean.parseBoolean(answer.toString());

		if (!success) {
			// throw new LeaveSessionException(projectName);
		}
	}

	/**
	 * Receive a change from the server / other clients.
	 * 
	 * @param projectName
	 *            The name of the project the change originated from.
	 * @param changeBlob
	 *            Serialized {@link AbstractChange}.
	 * @param nickname
	 *            The nickname of the client the change originated from.
	 */
	public void receiveChange(final String projectName,
			final byte[] changeBlob, final String nickname) {
		AbstractChange change = (AbstractChange) Conversion
				.byteToObject(changeBlob);
		Activator.SESSION.applyChange(projectName, change);
	}

	/**
	 * Send a chat message to the server.
	 * 
	 * @param nickname
	 *            The nickname of the client the chat message originated from.
	 * @param message
	 *            The message.
	 */
	public void sendChat(final ChatMessage chatMessage) {
		byte[] blob = Conversion.objectToByte(chatMessage);

		ATerm sendChat = factory.make("sendChat(<blob>)", blob);
		sendEvent(sendChat);
	}

	/**
	 * Receive a chat message from the server.
	 * 
	 * @param chatBlob
	 *            Serialized {@link ChatMessage}.
	 */
	public void receiveChat(final byte[] chatBlob) {
		ChatMessage chatMessage = (ChatMessage) Conversion
				.byteToObject(chatBlob);

		Activator.SESSION.receiveChat(chatMessage);
	}

	/**
	 * Send what resource is being viewed.
	 * 
	 * @param nickname
	 *            The client viewing the resource.
	 * @param project
	 *            The project the resource belongs to.
	 * @param resource
	 *            The viewed resource, path must be relative to the project, and
	 *            portable.
	 */
	public void sendWhosWhere(final WhosWhere whosWhere) {
		byte[] blob = Conversion.objectToByte(whosWhere);

		ATerm sendWhosWhere = factory.make("sendWhosWhere(<blob>)", blob);
		sendEvent(sendWhosWhere);
	}

	/**
	 * Receive what resource is being viewed by another client.
	 * 
	 * @param nickname
	 *            The client viewing the resource.
	 * @param project
	 *            The project the resource belongs to.
	 * @param resource
	 *            The viewed resource, path is relative to the project, and
	 *            portable.
	 */
	public void receiveWhosWhere(final byte[] whosWhereBlob) {
		WhosWhere whosWhere = (WhosWhere) Conversion
		.byteToObject(whosWhereBlob);

		Activator.SESSION.receiveWhosWhere(whosWhere);
	}

}
