package nl.jeldertpol.xtc.client.session;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.changes.editor.DocumentReplacerJob;
import nl.jeldertpol.xtc.client.changes.editor.PartListener;
import nl.jeldertpol.xtc.client.changes.resource.jobs.ResourceAddedResourceJob;
import nl.jeldertpol.xtc.client.changes.resource.jobs.ResourceMoveJob;
import nl.jeldertpol.xtc.client.changes.resource.jobs.ResourceReceiveContentJob;
import nl.jeldertpol.xtc.client.changes.resource.jobs.ResourceRemovedResourceJob;
import nl.jeldertpol.xtc.client.changes.resource.jobs.ResourceSendContentJob;
import nl.jeldertpol.xtc.client.exceptions.AlreadyInSessionException;
import nl.jeldertpol.xtc.client.exceptions.LeaveSessionException;
import nl.jeldertpol.xtc.client.exceptions.NicknameAlreadyTakenException;
import nl.jeldertpol.xtc.client.exceptions.ProjectAlreadyPresentException;
import nl.jeldertpol.xtc.client.exceptions.ProjectModifiedException;
import nl.jeldertpol.xtc.client.exceptions.ProjectUnmanagedFilesException;
import nl.jeldertpol.xtc.client.exceptions.RevisionExtractorException;
import nl.jeldertpol.xtc.client.exceptions.UnableToConnectException;
import nl.jeldertpol.xtc.client.exceptions.UnversionedProjectException;
import nl.jeldertpol.xtc.client.exceptions.WrongRevisionException;
import nl.jeldertpol.xtc.client.exceptions.XtcException;
import nl.jeldertpol.xtc.client.preferences.connection.PreferenceConstants;
import nl.jeldertpol.xtc.client.session.chat.Chat;
import nl.jeldertpol.xtc.client.session.infoExtractor.InfoExtractor;
import nl.jeldertpol.xtc.client.session.infoExtractor.SubclipseInfoExtractor;
import nl.jeldertpol.xtc.client.session.whosWhere.WhosWhere;
import nl.jeldertpol.xtc.common.changes.AbstractChange;
import nl.jeldertpol.xtc.common.changes.AddedResourceChange;
import nl.jeldertpol.xtc.common.changes.ContentChange;
import nl.jeldertpol.xtc.common.changes.MoveChange;
import nl.jeldertpol.xtc.common.changes.RemovedResourceChange;
import nl.jeldertpol.xtc.common.changes.TextualChange;
import nl.jeldertpol.xtc.common.chat.ChatMessage;
import nl.jeldertpol.xtc.common.session.SimpleSession;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.swt.widgets.Display;
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
	final private InfoExtractor infoExtractor;

	/**
	 * Holds the connection state with the server.
	 */
	private boolean connected;

	/**
	 * Holds whether the client is in a session or not.
	 */
	private boolean inSession;

	/**
	 * Holds whether the client is paused or not.
	 */
	private boolean paused;

	/**
	 * Holds all changes while client is paused.
	 */
	private List<AbstractChange> pauseList;

	/**
	 * Holds the paths to ignore.
	 */
	private List<IPath> ignorePathList;

	/**
	 * Holds the project of the current session, or an empty String.
	 */
	private String projectName;

	/**
	 * Holds the nickname used in the current session. Is refilled on starting
	 * or joining a new session.
	 */
	private String nickname;

	final private WhosWhere whosWhere;

	final private Chat chat;

	/**
	 * Holds the server.
	 */
	final private Server server;

	/**
	 * Create a new session. Will not connect to the Toolbus. This will be done
	 * when it is needed.
	 */
	public Session() {
		super();

		infoExtractor = new SubclipseInfoExtractor();
		connected = false;
		inSession = false;
		paused = false;
		pauseList = new ArrayList<AbstractChange>();
		ignorePathList = new ArrayList<IPath>();
		projectName = "";
		nickname = "";
		whosWhere = new WhosWhere();
		chat = new Chat();

		server = new Server();
	}

	/**
	 * @return the whosWhere
	 */
	public WhosWhere getWhosWhere() {
		return whosWhere;
	}

	/**
	 * @return the chat
	 */
	public Chat getChat() {
		return chat;
	}

	/**
	 * Connect to the server.
	 * 
	 * This is private, because it needs only be called when there is no
	 * connection. This is done automatically. Now the client does not need to
	 * worry about having a connection or not.
	 * 
	 * @throws UnableToConnectException
	 *             Connecting to the server failed.
	 */
	private void connect() throws UnableToConnectException {
		Preferences preferences = Activator.getDefault().getPluginPreferences();
		String host = preferences.getString(PreferenceConstants.P_HOST);
		String port = preferences.getString(PreferenceConstants.P_PORT);

		Activator.LOGGER.log(Level.INFO, "Connecting to server: " + host + ":"
				+ port);

		server.connect(host, port);
		connected = true;
	}

	/**
	 * Disconnect from the server. Sets connected to false.
	 * 
	 * This one is public, so it can be called when the plug-in is de-activated.
	 */
	public void disconnect() {
		// TODO Create new server object.
		// TODO Logging
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
	 * @throws AlreadyInSessionException
	 *             Client is already in a session.
	 * @throws ProjectModifiedException
	 *             The project has local modifications.
	 * @throws ProjectUnmanagedFilesException
	 *             The project has unmanaged files.
	 * @throws RevisionExtractorException
	 *             The underlying version control system throws an error.
	 * @throws UnversionedProjectException
	 *             The project is not under version control.
	 * @throws UnableToConnectException
	 *             Connecting to the server failed.
	 * @throws WrongRevisionException
	 *             The revision of the project does not match the revision of
	 *             the server.
	 * @throws ProjectAlreadyPresentException
	 *             The project is already present on the server.
	 * @throws NicknameAlreadyTakenException
	 *             The nickname is already present in the session.
	 */
	public void startJoinSession(final IProject project)
			throws AlreadyInSessionException, ProjectModifiedException,
			ProjectUnmanagedFilesException, RevisionExtractorException,
			UnversionedProjectException, UnableToConnectException,
			WrongRevisionException, ProjectAlreadyPresentException,
			NicknameAlreadyTakenException {
		if (inSession) {
			throw new AlreadyInSessionException();
		}

		// Ignoring output location (build path/bin folder)
		ignoreBuildPath(project);

		List<IResource> modifiedFiles = infoExtractor.modifiedFiles(project);
		if (!modifiedFiles.isEmpty()) {
			throw new ProjectModifiedException(project.getName(), modifiedFiles);
		}

		List<IResource> unmanagedFiles = infoExtractor.unmanagedFiles(project);
		if (!unmanagedFiles.isEmpty()) {
			throw new ProjectUnmanagedFilesException(project.getName(),
					unmanagedFiles);
		}

		if (!connected) {
			// UnableToConnectException
			connect();
		}

		List<SimpleSession> sessions = getSessions();
		String projectName = project.getName();

		// Is project already present on server?
		boolean present = false;
		Long serverRevision = null;
		for (SimpleSession simpleSession : sessions) {
			if (simpleSession.getProjectName().equals(projectName)) {
				present = true;
				serverRevision = simpleSession.getRevision();
				break;
			}
		}

		// Get nickname
		Preferences preferences = Activator.getDefault().getPluginPreferences();
		nickname = preferences.getString(PreferenceConstants.P_NICKNAME);

		Long revision = infoExtractor.getRevision(project);

		if (!present) {
			// Start new session
			Activator.LOGGER.log(Level.INFO, "Starting new session: "
					+ projectName + ", " + revision + ", " + nickname + ".");

			server.startSession(projectName, revision, nickname);
		} else {
			// Join existing session
			if (revision.equals(serverRevision)) {
				Activator.LOGGER.log(Level.INFO, "Joining session: "
						+ projectName + ", " + nickname + ".");

				server.joinSession(projectName, nickname);
			} else {
				throw new WrongRevisionException(revision, serverRevision);
			}
		}

		// Nothing went wrong, so client is now in a session.
		inSession = true;
		this.projectName = projectName;

		// Request and apply all changes made so far.
		if (present) {
			List<AbstractChange> changes = server.requestChanges(projectName);
			applyChanges(projectName, changes);
		}

		addResourceChangeListener();
		addPartListener();
	}

	/**
	 * Disconnect from the server, revert all changes, and connect again. Will
	 * make a copy of current project.
	 */
	public void rejoin() {
		Activator.LOGGER.log(Level.SEVERE, "Rejoining!");

		IProject project = Activator.COMMON_ACTIONS.getProject(projectName);

		try {
			leaveSession();

			IPath projectPath = project.getFullPath();
			String destinationString = projectPath.lastSegment().concat(
					" (copy)");
			IPath destination = projectPath.removeLastSegments(1).append(
					destinationString);
			boolean force = true;

			// backup
			project.copy(destination, force, null);

			// revert
			infoExtractor.revert(project);

			startJoinSession(project);
		} catch (XtcException e) {
			Activator.LOGGER.log(Level.SEVERE, "Could not rejoin.", e);
		} catch (CoreException e) {
			Activator.LOGGER.log(Level.SEVERE, "Could not rejoin.", e);
		}

	}

	/**
	 * Ignore the build path (usually <code>/bin</code>) if this project is a
	 * {@link IJavaProject}.
	 * 
	 * @param project
	 *            The project of which to ignore the build path.
	 */
	private void ignoreBuildPath(final IProject project) {
		IJavaProject javaProject = JavaCore.create(project);

		// It is a Java project.
		if (javaProject != null) {
			try {
				IPath outputLocation = javaProject.readOutputLocation();
				ignorePathList.add(relativeToProject(outputLocation));

				// Each source location could have their own output location.
				IClasspathEntry[] rawClassPaths = javaProject.getRawClasspath();
				for (IClasspathEntry classpathEntry : rawClassPaths) {
					outputLocation = classpathEntry.getOutputLocation();
					if (outputLocation != null) {
						IPath relative = relativeToProject(outputLocation);
						if (!ignorePathList.contains(relative)) {
							ignorePathList.add(relative);
						}
					}
				}
				Activator.LOGGER.log(Level.INFO, "Ignoring build path: "
						+ ignorePathList);
			} catch (JavaModelException e) {
				Activator.LOGGER.log(Level.SEVERE,
						"Error ignoring build path.", e);
			}
		}
	}

	/**
	 * Make location relative to project.
	 * 
	 * @param path
	 *            An path relative to the root of the workspace.
	 * @return An path relative to the project it is part of.
	 */
	private IPath relativeToProject(final IPath path) {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(path);
		return resource.getProjectRelativePath();
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
	 * Add the {@link PartListener} to the {@link IWorkbenchPage}.
	 * 
	 * @see Activator#partListener
	 */
	private void addPartListener() {
		getWorkbenchPage().addPartListener(Activator.partListener);
	}

	/**
	 * Removes the {@link PartListener} to the {@link IWorkbenchPage}.
	 * 
	 * @see Activator#partListener
	 */
	private void removePartListener() {
		getWorkbenchPage().removePartListener(Activator.partListener);
	}

	/**
	 * Get the active {@link IWorkbenchPage}.
	 * 
	 * @return The active {@link IWorkbenchPage}.
	 */
	private IWorkbenchPage getWorkbenchPage() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();

		return workbenchPage;
	}

	/**
	 * Leave the currently joined session. Does nothing when not in a session.
	 * 
	 * @throws LeaveSessionException
	 *             Something failed when leaving the session.
	 */
	public void leaveSession() throws LeaveSessionException {
		if (inSession) {
			Activator.LOGGER.log(Level.INFO, "Leaving session.");

			// Remove listeners
			removeResourceChangeListener();
			removePartListener();

			server.leaveSession(projectName, nickname);

			// Reset session data
			inSession = false;
			paused = false;
			pauseList = new ArrayList<AbstractChange>();
			ignorePathList = new ArrayList<IPath>();
			projectName = "";
			nickname = "";

			// Clear data of views
			whosWhere.clear();
			chat.clear();
		}
	}

	private void sendChange(final AbstractChange change) {
		if (paused) {
			resume();
		}

		Activator.LOGGER.log(Level.INFO, "Sending change: " + change);

		server.sendChange(projectName, change, nickname);
	}

	/**
	 * Send a change to the server.
	 * 
	 * @param project
	 *            The project the change originated from.
	 * @param filePath
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
	public void sendTextualChange(final IProject project, final IPath filePath,
			final int length, final int offset, final String text) {
		if (shouldSend(project, filePath)) {
			String filename = filePath.toPortableString();

			TextualChange change = new TextualChange(filename, length, offset,
					text, projectName, nickname);

			sendChange(change);
		}
	}

	/**
	 * A resource is moved. Send it to the server.
	 * 
	 * @param project
	 *            The project the move originated from.
	 * @param moveFrom
	 *            Relative path of original resource location.
	 * @param moveTo
	 *            Relative path of new resource location.
	 */
	public void sendMove(final IProject project, final IPath moveFrom,
			final IPath moveTo) {
		if (shouldSend(project, moveFrom)) {
			String from = moveFrom.toPortableString();
			String to = moveTo.toPortableString();

			MoveChange change = new MoveChange(from, to, projectName, nickname);

			sendChange(change);
		}
	}

	/**
	 * Send new content to the server. Creates a new
	 * {@link ResourceSendContentJob} to convert the file, which also calls
	 * {@link #sendContent(String, byte[], IProject)}.
	 * 
	 * @param project
	 *            The project the content belongs to.
	 * @param filePath
	 *            The file, path must be relative to the project.
	 * @param file
	 *            The actual file which holds the content.
	 */
	public void sendContent(final IProject project, final IPath filePath) {
		if (shouldSend(project, filePath)) {
			// Create new job to do the conversion.
			new ResourceSendContentJob(project, filePath);
		}
	}

	/**
	 * Send new content to the server. Should only be called from
	 * {@link ResourceSendContentJob}.
	 * 
	 * @param project
	 *            The project the content belongs to.
	 * @param filePath
	 *            The file, path must be relative to the project.
	 * @param content
	 *            The actual content of the file.
	 * 
	 * @see #sendContent(IProject, IPath)
	 */
	public void sendContent(final IProject project, final String filename,
			final byte[] content) {
		ContentChange change = new ContentChange(filename, content,
				projectName, nickname);

		sendChange(change);
	}

	/**
	 * Send an added resource to the server.
	 * 
	 * @param project
	 *            The project the resource belongs to.
	 * @param resourcePath
	 *            The added resource, path must be relative to the project.
	 * @param type
	 *            The type of resource added.
	 * 
	 * @see IResource#getType()
	 */
	public void sendAddedResource(final IProject project,
			final IPath resourcePath, final int type) {
		if (shouldSend(project, resourcePath)) {
			String resourceName = resourcePath.toPortableString();

			AddedResourceChange change = new AddedResourceChange(resourceName,
					type, projectName, nickname);

			sendChange(change);
		}
	}

	/**
	 * Send an removed resource to the server.
	 * 
	 * @param project
	 *            The project the resource belongs to.
	 * @param resourcePath
	 *            The added resource, path must be relative to the project.
	 */
	public void sendRemovedResource(final IProject project,
			final IPath resourcePath) {
		if (shouldSend(project, resourcePath)) {
			String resourceName = resourcePath.toPortableString();

			RemovedResourceChange change = new RemovedResourceChange(
					resourceName, projectName, nickname);

			sendChange(change);
		}
	}

	/**
	 * Request and apply textual changes made to this resource.
	 * 
	 * @param resourcePath
	 */
	public void requestTextualChanges(final IPath resourcePath) {
		String resource = resourcePath.toPortableString();

		Activator.LOGGER.log(Level.INFO, "Requesting textual changes for: "
				+ resource + ".");

		List<AbstractChange> changes = server.requestTextualChanges(
				projectName, resource);
		applyChanges(projectName, changes);
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
	 * Returns if the client is currently paused.
	 * 
	 * @return <code>true</code> when client is paused, <code>false</code>
	 *         otherwise.
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Set the client in the paused state. No changes will be applied.
	 */
	public void pause() {
		paused = true;
	}

	/**
	 * Resume from a paused state. Applies all pending changes.
	 */
	public void resume() {
		if (paused) {
			paused = false;
			applyChanges(projectName, pauseList);
			pauseList.clear();
		}
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

	/**
	 * Determines whether a change should be send.
	 * 
	 * @param project
	 *            The project the change originated from.
	 * @return <code>true</code> if change should be send to server,
	 *         <code>false</code> otherwise.
	 */
	private boolean shouldSend(final IProject project, final IPath resourcePath) {
		boolean send = inSession() && project.getName().equals(projectName);

		// If should send, check if resource should be ignored.
		if (send) {
			send = !isIgnored(resourcePath);
		}

		return send;
	}

	/**
	 * Determines whether the given resource is ignored in this session.
	 * 
	 * @param resourcePath
	 *            Project relative path of resource.
	 * @return <code>true</code> if resource is ignored, <code>false</code>
	 *         otherwise.
	 */
	public boolean isIgnored(final IPath resourcePath) {
		boolean ignored = false;

		for (IPath toIgnore : ignorePathList) {
			if (toIgnore.isPrefixOf(resourcePath)) {
				ignored = true;
				break;
			}
		}

		return ignored;
	}

	/**
	 * Determines whether a change should be received.
	 * 
	 * This is when client is in a session, the current project is the same as
	 * remote project, and the nickname is not the same as this client.
	 * 
	 * @param remoteProjectName
	 *            The project the change originated from.
	 * @param nickname
	 *            The nickname of the client the change originated from.
	 * @return <code>true</code> if change should be received,
	 *         <code>false</code> otherwise.
	 */
	private boolean shouldReceive(final String remoteProjectName) {
		return inSession() && remoteProjectName.equals(projectName);
	}

	/**
	 * Apply a list of changes.
	 * 
	 * @param projectName
	 *            The project the change originated from.
	 * @param changes
	 *            Changes that should be applied.
	 */
	private void applyChanges(final String projectName,
			final List<AbstractChange> changes) {
		for (AbstractChange abstractChange : changes) {
			applyChange(projectName, abstractChange);
		}
	}

	/**
	 * Apply a change. Change is added to pauselist when in pause.
	 * 
	 * Change is only applied if it should be received.
	 * 
	 * @param projectName
	 *            The project the change originated from.
	 * @param abstractChange
	 *            Change that should be applied.
	 */
	public void applyChange(final String projectName,
			final AbstractChange abstractChange) {
		if (paused) {
			pauseList.add(abstractChange);
		} else if (shouldReceive(projectName)) {
			Job job = null;

			// Remove listeners
			removeResourceChangeListener();

			if (abstractChange instanceof AddedResourceChange) {
				AddedResourceChange change = (AddedResourceChange) abstractChange;
				job = new ResourceAddedResourceJob(change);
			} else if (abstractChange instanceof ContentChange) {
				ContentChange change = (ContentChange) abstractChange;
				job = new ResourceReceiveContentJob(change);
			} else if (abstractChange instanceof MoveChange) {
				MoveChange change = (MoveChange) abstractChange;
				job = new ResourceMoveJob(change);
			} else if (abstractChange instanceof RemovedResourceChange) {
				RemovedResourceChange change = (RemovedResourceChange) abstractChange;
				job = new ResourceRemovedResourceJob(change);
			} else if (abstractChange instanceof TextualChange) {
				TextualChange change = (TextualChange) abstractChange;

				if (Display.getCurrent() == null) {
					// Not running in a UIThread, so create a UIJob.
					job = new DocumentReplacerJob(change);
				} else {
					// Already running in a UIThread, so apply change directly.
					DocumentReplacerJob.replace(change);
				}

				// Also update whosWhere
				whosWhere.change(nickname, change.getFilename());
			}
			try {
				job.join();
			} catch (InterruptedException e) {
				Activator.LOGGER.log(Level.SEVERE, "Error rejoining job", e);
				// TODO What to do?
			} catch (NullPointerException e) {
				// TextualChange may not create a job.
			} finally {
				// Add listeners
				addResourceChangeListener();
			}
		}
	}

	public void sendChat(final String message) {
		Activator.LOGGER.log(Level.INFO, "Sending chat.");

		server.sendChat(nickname, message);
	}

	public void receiveChat(final String nickname, final String message) {
		Activator.LOGGER.log(Level.INFO, "Receiving chat from " + nickname
				+ ".");

		ChatMessage chatMessage = new ChatMessage(nickname, message);
		chat.newMessage(chatMessage);
	}

}
