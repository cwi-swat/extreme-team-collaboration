package nl.jeldertpol.xtc.client.session;

import java.util.List;

import nl.jeldertpol.xtc.client.exceptions.NicknameAlreadyTakenException;
import nl.jeldertpol.xtc.client.exceptions.ProjectNotPresentException;
import nl.jeldertpol.xtc.client.exceptions.UnableToConnectException;

import org.eclipse.core.resources.IProject;

import toolbus.adapter.java.AbstractJavaTool;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermFactory;
import aterm.ATermLong;

/**
 * @author Jeldert Pol
 * 
 */
public class Server extends AbstractJavaTool {

	private ATermFactory factory = getFactory();

	private final String toolname = "client";

	public void connect(String host, String port, String nickname)
			throws UnableToConnectException, NicknameAlreadyTakenException{
		try {
			String[] connectioninfo = { "-TB_TOOL_NAME", toolname, "-TB_HOST",
					host, "-TB_PORT", port };
			connect(connectioninfo);
			setNickname(nickname);
		} catch (Exception ex) {
			throw new UnableToConnectException(ex);
		}
	}
	
	public void disconnect() {
		ATerm term = factory.make("Client disconnect...");
		super.disconnect(term);
	}



	@Override
	public void receiveAckEvent(ATerm term) {
		// TODO Auto-generated method stub
		// TODO Nickname ack komt hier binnen.
	}

	@Override
	public void receiveTerminate(ATerm term) {
		// TODO Auto-generated method stub

	}

	/**
	 * Set the nickname on the server.
	 * 
	 * @param nickname
	 *            the nickname to be set.
	 * @throws NicknameAlreadyTakenException
	 *             thrown when nickname is already taken.
	 */
	private void setNickname(String nickname)
			throws NicknameAlreadyTakenException {
		ATerm setNickname = factory.make("setNickname(<str>)", nickname);
		ATermAppl response = sendRequest(setNickname);

		ATerm answer = response.getArgument(0);
		boolean set = Boolean.parseBoolean(answer.toString());

		if (!set) {
			throw new NicknameAlreadyTakenException(nickname);
		}
	}

	/**
	 * Get the revision of the project from the server.
	 * 
	 * @param project
	 *            the project to get the revision from.
	 * @return the revision of the project on the server.
	 * @throws ProjectNotPresentException
	 *             thrown if the project does not exist on server.
	 */
	public Long getRevision(String project) throws ProjectNotPresentException {
		ATerm getRevision = factory.make("getRevision(<str>)", project);
		ATermAppl revision = sendRequest(getRevision);

		ATermLong answer = (ATermLong) revision.getArgument(0);
		if (answer.getLong() > -1) {
			return answer.getLong();
		} else {
			throw new ProjectNotPresentException(project);
		}
	}

	/**
	 * Join a session on the server. If the project is already present it will
	 * join that session.
	 * 
	 * @param project
	 *            The project associated with the session.
	 */
	public void joinSession(IProject project) {
		// TODO Auto-generated method stub

	}

	public void start(String name, Long revision, List<String> resources) {
		// TODO Auto-generated method stub
		
	}

}
