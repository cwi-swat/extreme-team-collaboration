package nl.jeldertpol.xtc.client.session;

import java.util.List;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.AlreadyInSessionException;
import nl.jeldertpol.xtc.client.exceptions.LeaveSessionException;
import nl.jeldertpol.xtc.client.exceptions.NicknameAlreadyTakenException;
import nl.jeldertpol.xtc.client.exceptions.ProjectAlreadyPresentException;
import nl.jeldertpol.xtc.client.exceptions.ProjectModifiedException;
import nl.jeldertpol.xtc.client.exceptions.UnableToConnectException;
import nl.jeldertpol.xtc.client.exceptions.UnrevisionedProjectException;
import nl.jeldertpol.xtc.client.exceptions.WrongRevisionException;
import nl.jeldertpol.xtc.client.preferences.connection.PreferenceConstants;
import nl.jeldertpol.xtc.client.session.infoExtractor.InfoExtractor;
import nl.jeldertpol.xtc.client.session.infoExtractor.SubclipseInfoExtractor;
import nl.jeldertpol.xtc.common.Session.SimpleSession;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Preferences;

/**
 * A session to the server. This is an abstraction to be called by the client.
 * Only one session can be active.
 * 
 * @author Jeldert Pol
 */
public class Session {
	private InfoExtractor infoExtractor;

	/**
	 * Holds the connection state with the server.
	 */
	private boolean connected;

	/**
	 * Holds whether the client is in a session or not.
	 */
	private boolean inSession;

	/**
	 * Holds the project of the current session, or an empty String;
	 */
	private String projectName;

	/**
	 * Holds the nickname used in the current session. Is refilled on starting
	 * or joining a new session.
	 */
	private String nickname;

	/**
	 * Holds the server.
	 */
	private Server server;

	/**
	 * Create a new session. Will not connect to the Toolbus. This will be done
	 * when it is needed.
	 */
	public Session() {
		super();

		infoExtractor = new SubclipseInfoExtractor();
		connected = false;
		inSession = false;
		projectName = "";
		nickname = "";

		server = new Server();
	}

	/**
	 * Connect to the server.
	 * 
	 * This is private, because it needs only be called when there is no
	 * connection. This is done automatically. Now the client does not need to
	 * worry about having a connection or not.
	 */
	private void connect() throws UnableToConnectException {
		Preferences preferences = Activator.getDefault().getPluginPreferences();
		String host = preferences.getString(PreferenceConstants.P_HOST);
		String port = preferences.getString(PreferenceConstants.P_PORT);

		server.connect(host, port);
		connected = true;
	}

	/**
	 * Disconnect from the server. Sets connected to false.
	 * 
	 * This one is public, so it can be called when the plug-in is de-activated.
	 */
	public void disconnect() {
		try {
			leaveSession();
		} catch (LeaveSessionException e) {
			e.printStackTrace();
		}
		server.disconnect();
		connected = false;
	}

	/**
	 * Get current sessions from the server.
	 * 
	 * @return The current sessions from the server, or <code>null</code>.
	 * @throws UnableToConnectException
	 *             Connecting to the server failed.
	 */
	public List<SimpleSession> getSessions() throws UnableToConnectException {
		if (!connected) {
			connect();
		}
		return server.getSessions();
	}

	/**
	 * Start a new session on the server. All needed information for this is
	 * extracted from the project and the preferences.
	 * 
	 * @param project
	 *            The project for the session.
	 * @throws UnableToConnectException
	 *             Connecting to the server failed.
	 * @throws AlreadyInSessionException
	 *             Client is already in a session.
	 * @throws ProjectModifiedException
	 *             The project has local modifications.
	 * @throws UnrevisionedProjectException
	 *             The project is not under version control.
	 * @throws ProjectAlreadyPresentException
	 *             The project is already present on the server.
	 */
	public void startSession(IProject project) throws UnableToConnectException,
			AlreadyInSessionException, ProjectModifiedException,
			UnrevisionedProjectException, ProjectAlreadyPresentException {
		if (!connected) {
			connect();
		}
		if (inSession) {
			throw new AlreadyInSessionException();
		}

		String projectName = project.getName();

		if (!unmodifiedProject(project)) {
			throw new ProjectModifiedException(projectName);
		}

		Long revision = infoExtractor.getRevision(project);

		// Get nickname
		Preferences preferences = Activator.getDefault().getPluginPreferences();
		nickname = preferences.getString(PreferenceConstants.P_NICKNAME);

		server.startSession(projectName, revision, nickname);

		// Nothing went wrong, so client is now in a session.
		inSession = true;
		this.projectName = projectName;
	}

	/**
	 * Join a session on the server. All needed information for this is
	 * extracted from the project and the preferences.
	 * 
	 * @param project
	 *            The project for the session.
	 * @throws UnableToConnectException
	 *             Connecting to the server failed.
	 * @throws AlreadyInSessionException
	 *             Client is already in a session.
	 * @throws ProjectModifiedException
	 *             The project has local modifications.
	 * @throws UnrevisionedProjectException
	 *             The project is not under version control.
	 * @throws WrongRevisionException
	 *             The revision of the project does not match the revision of
	 *             the server.
	 * @throws NicknameAlreadyTakenException
	 *             The nickname is already present in the session.
	 */
	public void joinSession(IProject project) throws UnableToConnectException,
			AlreadyInSessionException, ProjectModifiedException,
			UnrevisionedProjectException, WrongRevisionException,
			NicknameAlreadyTakenException {
		if (!connected) {
			connect();
		}
		if (inSession) {
			throw new AlreadyInSessionException();
		}

		String projectName = project.getName();

		if (!unmodifiedProject(project)) {
			throw new ProjectModifiedException(projectName);
		}

		// Get nickname
		Preferences preferences = Activator.getDefault().getPluginPreferences();
		nickname = preferences.getString(PreferenceConstants.P_NICKNAME);

		List<SimpleSession> sessions = getSessions();
		for (SimpleSession simpleSession : sessions) {
			if (simpleSession.getProjectName().equals(projectName)) {
				Long localRevision = getRevision(project);
				Long serverRevision = simpleSession.getRevision();

				if (localRevision.equals(serverRevision)) {
					server.joinSession(projectName, nickname);
					inSession = true;
					this.projectName = projectName;
				} else {
					throw new WrongRevisionException(localRevision,
							serverRevision);
				}
			}
		}
	}

	/**
	 * Leave the currently joined session. Does nothing when not in a session.
	 * 
	 * @throws LeaveSessionException
	 */
	public void leaveSession() throws LeaveSessionException {
		if (inSession) {
			server.leaveSession(projectName, nickname);
			inSession = false;
			projectName = "";
			nickname = "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see InfoExtractor#getRevision(IProject)
	 */
	private Long getRevision(IProject project)
			throws UnrevisionedProjectException {
		return infoExtractor.getRevision(project);
	}

	/**
	 * Checks if the project is unmodified.
	 * 
	 * @param project
	 *            The project to check.
	 * @return <code>true</code> if project is unmodified, <code>false</code>
	 *         otherwise.
	 */
	private boolean unmodifiedProject(IProject project) {
		List<IResource> modifiedFiles = infoExtractor.modifiedFiles(project);

		return modifiedFiles.isEmpty();
	}

}
