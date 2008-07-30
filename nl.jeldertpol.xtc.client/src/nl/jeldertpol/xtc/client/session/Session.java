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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * @author Jeldert Pol
 */
public class Session {
	private InfoExtractor infoExtractor;

	private boolean connected;

	private Server server;

	public Session() {
		super();

		infoExtractor = new SubclipseInfoExtractor();
		connected = false;
		server = new Server();
	}

	/**
	 * Start a new session, or join an existing one. First validates the
	 * project: only versioned and unmodified projects can be used.
	 * 
	 * @param project
	 *            the {@link IProject} that is to be used in this session.
	 */
	public void startJoin(IProject project) {
		boolean valid = versionedProject(project) && unmodifiedProject(project);

		if (valid && !connected) {
			// Get all needed settings
			Preferences preferences = Activator.getDefault()
					.getPluginPreferences();
			String host = preferences.getString(PreferenceConstants.P_HOST);
			String port = preferences.getString(PreferenceConstants.P_PORT);
			String nickname = preferences
					.getString(PreferenceConstants.P_NICKNAME);

			try {
				// Connect to server
				server.connect(host, port, nickname);
				connected = true;

				Long localRevision = getRevision(project);
				
				try {
					Long serverRevision = server.getRevision(project.getName());

					if (serverRevision == localRevision) {
						// Join session
					} else {
						throw new WrongRevisionException(localRevision,
								serverRevision);
					}

				} catch (ProjectNotPresentException e) {
					// Start new session
					List<IResource> iResources = infoExtractor.getResources(project);
					List<String> resources = new ArrayList<String>(iResources.size());
					for (IResource resource : iResources) {
						resources.add(resource.toString());
					}
					
					server.start(project.getName(), localRevision, resources);
				}

			} catch (UnableToConnectException e) {
				server.disconnect();
				connected = false;
				e.printStackTrace();
				MessageDialog.openError(null, "XTC Start / Join",
						"Unable to connect to XTC server.");
				return;
			} catch (NicknameAlreadyTakenException e) {
				e.printStackTrace();
				MessageDialog.openError(null, "XTC Start / Join", e
						.getMessage());
				return;
			} catch (WrongRevisionException e) {
				e.printStackTrace();
				MessageDialog.openError(null, "XTC Start / Join", e
						.getMessage());
				return;
				// } catch (ProjectNotPresentException e) {
				// e.printStackTrace();
				// MessageDialog.openError(null, "XTC Start / Join", e
				// .getMessage());
				// return;
			}
		} else {
			if (connected) {
				MessageDialog.openError(null, "XTC Start / Join",
						"You are already connected to a session.");
			}
		}
	}

	public void disconnect() {
		server.disconnect();
		connected = false;
	}

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

	/**
	 * Get the revision of the project, or null.
	 * 
	 * @param project
	 *            the project to get the revision from.
	 * @return the revision of the project, or null.
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

		if (modifiedFiles.isEmpty()) {
			return true;
		} else {
			MessageDialog
					.openError(
							null,
							"XTC Start / Join",
							"The selected project has modified files. Only unmodified projects can be used.");
			return false;
		}
	}
}
