package nl.jeldertpol.xtc.client.session;

import java.util.ArrayList;
import java.util.List;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.NicknameAlreadyTakenException;
import nl.jeldertpol.xtc.client.exceptions.ProjectNotPresentException;
import nl.jeldertpol.xtc.client.exceptions.UnableToConnectException;
import nl.jeldertpol.xtc.client.exceptions.WrongRevisionException;
import nl.jeldertpol.xtc.client.preferences.connection.PreferenceConstants;
import nl.jeldertpol.xtc.client.session.infoExtractor.InfoExtractor;
import nl.jeldertpol.xtc.client.session.infoExtractor.SubclipseInfoExtractor;
import nl.jeldertpol.xtc.common.Session.SimpleSession;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.MessageDialog;

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
		server = new Server();
	}

	/**
	 * Connect to the server.
	 * 
	 * This is private, because it needs only be called when there is no
	 * connection. Now the client does not need to worry about having a
	 * connection or not.
	 */
	private void connect() throws UnableToConnectException {
		Preferences preferences = Activator.getDefault().getPluginPreferences();
		String host = preferences.getString(PreferenceConstants.P_HOST);
		String port = preferences.getString(PreferenceConstants.P_PORT);

		try {
			server.connect(host, port);
			connected = true;
		} catch (UnableToConnectException e) {
			e.printStackTrace();
			MessageDialog.openError(null, "XTC Connect",
					"Connecting to the server failed: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Disconnect from the server. Sets connected to false.
	 * 
	 * This one is public, so it can be called when the plug-in is de-activated.
	 */
	public void disconnect() {
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

			return server.getSessions();
		}

		// Impossible to reach this place??? returns above, or throws error
		// above.
		return null;
	}

	/**
	 * Start a new session, or join an existing one. First validates the
	 * project: only versioned and unmodified projects can be used.
	 * 
	 * @param project
	 *            the {@link IProject} that is to be used in this session.
	 * @throws UnableToConnectException
	 */
	// public void startJoin(IProject project) {
	// boolean valid = versionedProject(project) && unmodifiedProject(project);
	//
	// if (valid && !connected) {
	// // Get all needed settings
	// Preferences preferences = Activator.getDefault()
	// .getPluginPreferences();
	// String host = preferences.getString(PreferenceConstants.P_HOST);
	// String port = preferences.getString(PreferenceConstants.P_PORT);
	// String nickname = preferences
	// .getString(PreferenceConstants.P_NICKNAME);
	//
	// try {
	// // Connect to server
	// server.connect(host, port, nickname);
	// connected = true;
	//
	// Long localRevision = getRevision(project);
	//
	// try {
	// Long serverRevision = server.getRevision(project.getName());
	//
	// if (serverRevision == localRevision) {
	// // Join session
	// } else {
	// throw new WrongRevisionException(localRevision,
	// serverRevision);
	// }
	//
	// } catch (ProjectNotPresentException e) {
	// // Start new session
	// List<IResource> iResources = infoExtractor
	// .getResources(project);
	// List<String> resources = new ArrayList<String>(iResources
	// .size());
	// for (IResource resource : iResources) {
	// resources.add(resource.toString());
	// }
	//
	// server.startSession(project.getName(), localRevision, resources,
	// nickname);
	// }
	//
	// } catch (UnableToConnectException e) {
	// disconnect();
	// e.printStackTrace();
	// MessageDialog.openError(null, "XTC Start / Join",
	// "Unable to connect to XTC server.");
	// return;
	// } catch (NicknameAlreadyTakenException e) {
	// e.printStackTrace();
	// MessageDialog.openError(null, "XTC Start / Join", e
	// .getMessage());
	// return;
	// } catch (WrongRevisionException e) {
	// e.printStackTrace();
	// MessageDialog.openError(null, "XTC Start / Join", e
	// .getMessage());
	// return;
	// // } catch (ProjectNotPresentException e) {
	// // e.printStackTrace();
	// // MessageDialog.openError(null, "XTC Start / Join", e
	// // .getMessage());
	// // return;
	// }
	// } else {
	// if (connected) {
	// MessageDialog.openError(null, "XTC Start / Join",
	// "You are already connected to a session.");
	// }
	// }
	// }

	/**
	 * Checks if the project is versioned. Shows an error message if it's not.
	 * 
	 * @param project
	 *            The project to check.
	 * @return <code>true</code> if project is versioned, <code>false</code>
	 *         otherwise.
	 */
	private boolean versionedProject(IProject project) {
		Long revision = infoExtractor.getRevision(project);

		if (revision != null) {
			return true;
		} else {
			MessageDialog
					.openError(
							null,
							"XTC Start / Join",
							"The selected project is not under version control. Only versioned projects can be used.");
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see InfoExtractor#getRevision(IProject)
	 */
	private Long getRevision(IProject project) {
		return infoExtractor.getRevision(project);
	}

	/**
	 * Checks if the project is unmodified. Shows an error message if it's not.
	 * 
	 * @param project
	 *            The project to check.
	 * @return <code>true</code> if project is unmodified, <code>false</code>
	 *         otherwise.
	 */
	private boolean unmodifiedProject(IProject project) {
		List<IResource> modifiedFiles = infoExtractor.modifiedFiles(project);

		return modifiedFiles.isEmpty();
		// if (modifiedFiles.isEmpty()) {
		// return true;
		// } else {
		// MessageDialog
		// .openError(
		// null,
		// "XTC Start / Join",
		// "The selected project has modified files. Only unmodified projects can be used."
		// );
		// return false;
		// }
	}
}
