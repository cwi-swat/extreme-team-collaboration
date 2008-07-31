package nl.jeldertpol.xtc.server;

import java.util.ArrayList;

import nl.jeldertpol.xtc.common.Conversion.Conversion;
import nl.jeldertpol.xtc.common.Session.SimpleSession;
import nl.jeldertpol.xtc.server.session.Session;
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
	 * Holds the current projects.
	 */
	private ArrayList<Session> sessions = new ArrayList<Session>();

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
	
	public ATerm getSessions() {
		ArrayList<SimpleSession> simpleSessions = new ArrayList<SimpleSession>(sessions);
		simpleSessions.addAll(sessions);
		
		byte[] blob = Conversion.ObjectToByte(simpleSessions);
		ATermBlob termBlob = factory.makeBlob(blob);
		return termBlob;
	}
	
	public ATerm startSession(String projectName, ATerm revisionTerm, String nickname) {
		boolean succes = true;
		
		// Convert ATerms to right ATerm
		ATermLong revisionTermLong = (ATermLong) revisionTerm;
		Long revision = revisionTermLong.getLong();
		
		// Check if project exists
		for (Session session : sessions) {
			if (session.getProjectName().equals(projectName)) {
				succes = false;
				break;
			}
		}
		if (succes) {
			// Create new session
			Session session = new Session(projectName, revision, nickname);
			sessions.add(session);
		}
		
		ATerm startSession = factory.make("startSession(<bool>)", succes);
		return startSession;
	}
	
	public ATerm joinSession(String projectName, String nickname) {
		boolean succes = false;
		
		// Check if project exists
		for (Session session : sessions) {
			if (session.getProjectName().equals(projectName)) {
				// Check if nickname is available
				session.addClient(nickname);
				succes = true;
			}
		}
		
		ATerm joinSession = factory.make("joinSession(<bool>)", succes);
		return joinSession;
	}

	public ATerm leaveSession(String projectName, String nickname) {
		boolean succes = false;
		
		// Check if project exists
		for (Session session : sessions) {
			if (session.getProjectName().equals(projectName)) {
				session.removeClient(nickname);
				succes = true;
			}
		}
		
		ATerm leaveSession = factory.make("leaveSession(<bool>)", succes);
		return leaveSession;
	}

}
