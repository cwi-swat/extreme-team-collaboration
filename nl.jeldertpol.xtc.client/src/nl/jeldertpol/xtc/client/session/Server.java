package nl.jeldertpol.xtc.client.session;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.LeaveSessionException;
import nl.jeldertpol.xtc.client.exceptions.NicknameAlreadyTakenException;
import nl.jeldertpol.xtc.client.exceptions.ProjectAlreadyPresentException;
import nl.jeldertpol.xtc.client.exceptions.UnableToConnectException;
import nl.jeldertpol.xtc.common.conversion.Conversion;
import nl.jeldertpol.xtc.common.session.SimpleSession;

import org.eclipse.core.resources.IResource;
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
	 * @throws NicknameAlreadyTakenException
	 *             The nickname is already present in the session.
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
	 * 
	 * @throws LeaveSessionException
	 *             Something failed when leaving the session.
	 */
	public void leaveSession(String projectName, String nickname)
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
	 *            The nickname of the client the move originated from.
	 * 
	 * @see IPath#toPortableString()
	 */
	public void sendChange(String projectName, String filePath, int length,
			int offset, String text, String nickname) {
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

	/**
	 * Receive a change from the server / other clients.
	 * 
	 * @param projectName
	 *            The name of the project the change originated from.
	 * @param filePath
	 *            The file the change originated from, path is relative to the
	 *            project, and portable.
	 * @param length
	 *            Length of the replaced document text.
	 * @param offset
	 *            The document offset.
	 * @param text
	 *            Text inserted into the document.
	 * @param nickname
	 *            The nickname of the client the change originated from.
	 */
	public void receiveChange(String projectName, String filePath, int length,
			int offset, String text, String nickname) {
		Activator.SESSION.receiveChange(projectName, filePath, length, offset,
				text, nickname);
	}

	/**
	 * Send a move to the server.
	 * 
	 * @param projectName
	 *            The name of the project the move originated from.
	 * @param from
	 *            Full path of original resource location, must be portable.
	 * @param to
	 *            Full path of new resource location, must be portable.
	 * @param nickname
	 *            The nickname of the client the move originated from.
	 * 
	 * @see IPath#toPortableString()
	 */
	public void sendMove(String projectName, String from, String to,
			String nickname) {
		ATerm sendMove = factory.make("sendMove(<str>, <str>, <str>, <str>)",
				projectName, from, to, nickname);
		ATermAppl reply = sendRequest(sendMove);

		ATerm answer = reply.getArgument(0);
		boolean success = Boolean.parseBoolean(answer.toString());

		if (!success) {
			// throw new LeaveSessionException(projectName);
		}
	}

	/**
	 * Receive a move from the server / other clients.
	 * 
	 * @param projectName
	 *            The name of the project the move originated from.
	 * @param from
	 *            Full path of original resource location, must be portable.
	 * @param to
	 *            Full path of new resource location, must be portable.
	 * @param nickname
	 *            The nickname of the client the move originated from.
	 */
	public void receiveMove(String projectName, String from, String to,
			String nickname) {
		Activator.SESSION.receiveMove(projectName, from, to, nickname);
	}

	/**
	 * Send new content to the server.
	 * 
	 * @param projectName
	 *            The name of the project the content belongs to.
	 * @param filePath
	 *            The file the content belongs to, path is relative to the
	 *            project, and portable.
	 * @param content
	 *            The actual content. Will be closed.
	 * @param nickname
	 *            The nickname of the client the content originated from.
	 */
	public void sendContent(String projectName, String filePath,
			InputStream content, String nickname) {
		byte[] blob = Conversion.inputStreamToByte(content);

		try {
			content.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ATermBlob termBlob = factory.makeBlob(blob);

		ATerm sendContent = factory.make(
				"sendContent(<str>, <str>, <term>, <str>))", projectName,
				filePath, termBlob, nickname);
		ATermAppl reply = sendRequest(sendContent);

		ATerm answer = reply.getArgument(0);
		boolean success = Boolean.parseBoolean(answer.toString());

		if (!success) {
			// throw new LeaveSessionException(projectName);
		}
	}

	/**
	 * Receive new content from the server / other clients.
	 * 
	 * @param projectName
	 *            The name of the project the content belongs to.
	 * @param filePath
	 *            The file the content belongs to, path is relative to the
	 *            project, and portable.
	 * @param contentTerm
	 *            The actual content.
	 * @param nickname
	 *            The nickname of the client the content originated from.
	 */
	public void receiveContent(String projectName, String filePath,
			ATerm contentTerm, String nickname) {
		// TODO bug in toolbus, needed for handshaking only.
		// ATermBlob blob = (ATermBlob) contentTerm;
		// InputStream content = (InputStream) Conversion.ByteToObject(blob
		// .getBlobData());
		//
		// Activator.session.receiveContent(projectName, filePath, content,
		// nickname);
	}

	/**
	 * Receive new content from the server / other clients.
	 * 
	 * @param projectName
	 *            The name of the project the content belongs to.
	 * @param filePath
	 *            The file the content belongs to, path is relative to the
	 *            project, and portable.
	 * @param contentTerm
	 *            The actual content.
	 * @param nickname
	 *            The nickname of the client the content originated from.
	 */
	public void receiveContent(String projectName, String filePath,
			byte[] contentTerm, String nickname) {
		// TODO bug in toolbus, this method will be called.
		InputStream content = Conversion.byteToInputStream(contentTerm);

		Activator.SESSION.receiveContent(projectName, filePath, content,
				nickname);
	}

	/**
	 * Send an added resource to the server.
	 * 
	 * @param projectName
	 *            The name of the project the resource is added to.
	 * @param resourcePath
	 *            The added resource, path is relative to the project, and
	 *            portable.
	 * @param type
	 *            The type of resource added.
	 * @param nickname
	 *            The nickname of the client the added resource originated from.
	 * 
	 * @see IResource#getType()
	 */
	public void sendAddedResource(String projectName, String resourcePath,
			int type, String nickname) {
		ATerm sendAddedResource = factory.make(
				"sendAddedResource(<str>, <str>, <int>, <str>)", projectName,
				resourcePath, type, nickname);
		ATermAppl reply = sendRequest(sendAddedResource);

		ATerm answer = reply.getArgument(0);
		boolean success = Boolean.parseBoolean(answer.toString());

		if (!success) {
			// throw new LeaveSessionException(projectName);
		}
	}

	/**
	 * Receive an added resource from the server / other clients.
	 * 
	 * @param projectName
	 *            The name of the project the resource is added to.
	 * @param resourcePath
	 *            The added resource, path is relative to the project, and
	 *            portable.
	 * @param type
	 *            The type of resource added.
	 * @param nickname
	 *            The nickname of the client the added resource originated from.
	 * 
	 * @see IResource#getType()
	 */
	public void receiveAddedResource(String projectName, String resourcePath,
			int type, String nickname) {
		Activator.SESSION.receiveAddedResource(projectName, resourcePath, type,
				nickname);
	}

	/**
	 * Send an removed resource to the server.
	 * 
	 * @param projectName
	 *            The name of the project the resource belongs to.
	 * @param resourcePath
	 *            The removed resource, path must be relative to the project.
	 * @param nickname
	 *            The nickname of the client the removed resource originated
	 *            from.
	 */
	public void sendRemovedResource(String projectName, String resourcePath,
			String nickname) {
		ATerm sendRemovedResource = factory.make(
				"sendRemovedResource(<str>, <str>, <str>)", projectName,
				resourcePath, nickname);
		ATermAppl reply = sendRequest(sendRemovedResource);

		ATerm answer = reply.getArgument(0);
		boolean success = Boolean.parseBoolean(answer.toString());

		if (!success) {
			// throw new LeaveSessionException(projectName);
		}
	}

	/**
	 * Receive an removed resource from the server / other clients.
	 * 
	 * @param projectName
	 *            The name of the project the resource is added to.
	 * @param resourcePath
	 *            The removed resource, path is relative to the project, and
	 *            portable.
	 * @param nickname
	 *            The nickname of the client the removed resource originated
	 *            from.
	 */
	public void receiveRemovedResource(String projectName, String resourcePath,
			String nickname) {
		Activator.SESSION.receiveRemovedResource(projectName, resourcePath,
				nickname);
	}

}
