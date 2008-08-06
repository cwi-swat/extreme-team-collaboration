package nl.jeldertpol.xtc.client.session;

import java.util.List;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.changes.editor.DocumentReplacer;
import nl.jeldertpol.xtc.client.changes.editor.PartListener;
import nl.jeldertpol.xtc.client.changes.resource.ResourceChangeExecuter;
import nl.jeldertpol.xtc.client.changes.resource.ResourceChangeListener;
import nl.jeldertpol.xtc.client.changes.resource.ResourceMoveJob;
import nl.jeldertpol.xtc.client.exceptions.AlreadyInSessionException;
import nl.jeldertpol.xtc.client.exceptions.LeaveSessionException;
import nl.jeldertpol.xtc.client.exceptions.NicknameAlreadyTakenException;
import nl.jeldertpol.xtc.client.exceptions.ProjectAlreadyPresentException;
import nl.jeldertpol.xtc.client.exceptions.ProjectModifiedException;
import nl.jeldertpol.xtc.client.exceptions.ProjectNotOnServerException;
import nl.jeldertpol.xtc.client.exceptions.UnableToConnectException;
import nl.jeldertpol.xtc.client.exceptions.UnrevisionedProjectException;
import nl.jeldertpol.xtc.client.exceptions.WrongRevisionException;
import nl.jeldertpol.xtc.client.preferences.connection.PreferenceConstants;
import nl.jeldertpol.xtc.client.session.infoExtractor.InfoExtractor;
import nl.jeldertpol.xtc.client.session.infoExtractor.SubclipseInfoExtractor;
import nl.jeldertpol.xtc.common.session.SimpleSession;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * A session to the server. This is an abstraction to be called by the client.
 * Only one session can be active.
 * 
 * @author Jeldert Pol
 */
public class Session {
	private InfoExtractor infoExtractor;
	private ResourceChangeExecuter resourceChangeExecuter;

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
		resourceChangeExecuter = new ResourceChangeExecuter();
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
	 * Start or join a session. If the project is already on the server an
	 * attempt is made to join it. When it is not present an attempt is made to
	 * start a new session.
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
	 * @throws WrongRevisionException
	 *             The revision of the project does not match the revision of
	 *             the server.
	 * @throws NicknameAlreadyTakenException
	 *             The nickname is already present in the session.
	 * @throws ProjectNotOnServerException
	 *             The project is not on the server. Cannot join a session.
	 * @see Session#startSession(IProject)
	 * @see Session#joinSession(IProject)
	 */
	public void startJoinSession(IProject project)
			throws UnableToConnectException, AlreadyInSessionException,
			ProjectModifiedException, UnrevisionedProjectException,
			WrongRevisionException, NicknameAlreadyTakenException,
			ProjectAlreadyPresentException, ProjectNotOnServerException {
		List<SimpleSession> sessions = getSessions();
		String projectName = project.getName();

		boolean present = false;
		for (SimpleSession simpleSession : sessions) {
			if (simpleSession.getProjectName().equals(projectName)) {
				joinSession(project);
				present = true;
				break;
			}
		}

		if (!present) {
			startSession(project);
		}
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

		registerListeners();
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
	 * @throws ProjectNotOnServerException
	 *             The project is not on the server. Cannot join a session.
	 */
	public void joinSession(IProject project) throws UnableToConnectException,
			AlreadyInSessionException, ProjectModifiedException,
			UnrevisionedProjectException, WrongRevisionException,
			NicknameAlreadyTakenException, ProjectNotOnServerException {
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
		boolean found = false;
		for (SimpleSession simpleSession : sessions) {
			if (simpleSession.getProjectName().equals(projectName)) {
				found = true;
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
				break;
			}
		}

		if (!found) {
			throw new ProjectNotOnServerException(projectName);
		}

		registerListeners();
	}

	/**
	 * Add the {@link IResourceChangeListener} to the workspace.
	 * 
	 * @see Activator#resourceChangeListener
	 */
	public void addResourceChangeListener() {
		// Registers the resourceChangeListener to the workspace.
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				Activator.resourceChangeListener,
				IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * Removes the {@link IResourceChangeListener} from the workspace.
	 * 
	 * @see Activator#resourceChangeListener
	 */
	public void removeResourceChangeListener() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
				Activator.resourceChangeListener);
	}

	/**
	 * Registers {@link ResourceChangeListener} and {@link PartListener}.
	 */
	private void registerListeners() {
		addResourceChangeListener();
		
		// Registers a {@link PartListener} to the current {@link
		// IWorkbenchPage}.
		System.out.println("updateDocumentListeners");
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();

		IPartListener2 partListener = new PartListener();
		workbenchPage.addPartListener(partListener);
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

	/**
	 * Send a change to the server.
	 * 
	 * @param project
	 *            The project the change originated from.
	 * @param file
	 *            The file, path must be relative to the project.
	 * @param length
	 *            Length of the replaced document text.
	 * @param offset
	 *            The document offset.
	 * @param text
	 *            Text inserted into the document.
	 * 
	 * @see IResource#getProjectRelativePath()
	 * @see DocumentEvent
	 */
	public void sendChange(IProject project, IPath file, int length,
			int offset, String text) {
		if (shouldSend(project)) {
			String filename = file.toPortableString();
			server.sendChange(projectName, filename, length, offset, text,
					nickname);
		}
	}

	/**
	 * Receive a change from the server / other clients
	 * 
	 * @param projectName
	 *            The name of the project the change originated from.
	 * @param filename
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
	public void receiveChange(String remoteProjectName, String filename,
			int length, int offset, String text, String nickname) {
		if (shouldReceive(remoteProjectName, nickname)) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectName);
			IResource resource = project.findMember(filename);

			DocumentReplacer documentReplacer = new DocumentReplacer();
			documentReplacer.replace(resource, length, offset, text);
		}
	}

	/**
	 * A resource is moved. Send it to the server.
	 * 
	 * @param project
	 *            The project the move originated from.
	 * @param moveFrom
	 *            Full path of original resource location.
	 * @param moveTo
	 *            Full path of new resource location.
	 */
	public void sendMove(IProject project, IPath moveFrom, IPath moveTo) {
		if (shouldSend(project)) {
			String from = moveFrom.toPortableString();
			String to = moveTo.toPortableString();
			server.sendMove(projectName, from, to, nickname);
		}
	}

	/**
	 * Receive a move from the server / other clients
	 * 
	 * @param remoteProjectName
	 *            The name of the project the move originated from.
	 * @param from
	 *            Full path of original resource location, must be portable.
	 * @param to
	 *            Full path of new resource location, must be portable.
	 * @param nickname
	 *            The nickname of the client the move originated from.
	 */
	public void receiveMove(String remoteProjectName, String from, String to,
			String nickname) {
		if (shouldReceive(remoteProjectName, nickname)) {
			IPath moveFrom = Path.fromPortableString(from);
			IPath moveTo = Path.fromPortableString(to);

			IResource resource = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(moveFrom);

			resourceChangeExecuter.move(resource, moveTo);
		}
	}

	/**
	 * Returns if the client is currently in a session.
	 * 
	 * @return <code>true</code> when client is in a session, <code>false</code>
	 *         otherwise.
	 */
	public boolean inSession() {
		return inSession;
	}

	/**
	 * Get the name of the project of the current session. Returns an empty
	 * {@link String} ("") when not in a session.
	 * 
	 * @return The name of the project or an empty {@link String} ("").
	 */
	public String getCurrentProject() {
		return projectName;
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

	/**
	 * Determines whether a change should be send.
	 * 
	 * @param project
	 *            The project the change originated from.
	 * @return <code>true</code> if change should be send to server,
	 *         <code>false</code> otherwise.
	 */
	private boolean shouldSend(IProject project) {
		String remoteProjectName = project.getName();

		return inSession() && remoteProjectName.equals(projectName);
	}

	/**
	 * Determines whether a change should be received.
	 * 
	 * @param remoteProjectName
	 *            The project the change originated from.
	 * @param nickname
	 *            The nickname of the client the change originated from.
	 * @return <code>true</code> if change should be received,
	 *         <code>false</code> otherwise.
	 */
	private boolean shouldReceive(String remoteProjectName, String nickname) {
		// Only act when in a session. Should always be true.
		// Only react if current project is the same as remote.
		// This will ignore changes from the client itself.
		return inSession() && remoteProjectName.equals(projectName)
				&& !this.nickname.equals(nickname);
	}

}
