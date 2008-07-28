package nl.jeldertpol.xtc.client.session;

import java.util.List;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.UnableToConnectException;
import nl.jeldertpol.xtc.client.preferences.connection.PreferenceConstants;
import nl.jeldertpol.xtc.client.session.infoExtractor.SubclipseInfoExtractor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * @author Jeldert Pol
 */
public class Session {
	private SubclipseInfoExtractor infoExtractor;

	private boolean connected;

	private Toolbus toolbus;

	public Session() {
		super();

		infoExtractor = new SubclipseInfoExtractor();
		connected = false;
		toolbus = new Toolbus();
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
			// Contact server
			String toolname = Activator.PLUGIN_ID;
			Preferences preferences = Activator.getDefault()
					.getPluginPreferences();
			String host = preferences.getString(PreferenceConstants.P_HOST);
			String port = preferences.getString(PreferenceConstants.P_PORT);

			try {
				toolbus.connect(toolname, host, port);
			} catch (UnableToConnectException e) {
				e.printStackTrace();
				MessageDialog.openError(null, "XTC Start / Join",
						"Unable to connect to XTC server.");
				return;
			}

			// Look if project is already started
			
			// Match revision
			// Join
			// Error

			// Start new session

		} else {
			if (connected) {
				MessageDialog.openError(null, "XTC Start / Join",
						"You are already connected to a session.");
			}
		}
	}

	public void disconnect() {

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
