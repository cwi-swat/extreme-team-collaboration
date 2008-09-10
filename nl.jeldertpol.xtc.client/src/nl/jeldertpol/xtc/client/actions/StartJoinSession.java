package nl.jeldertpol.xtc.client.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.ProjectUnmanagedFilesException;
import nl.jeldertpol.xtc.client.exceptions.XtcException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Start or join a session.
 * 
 * @author Jeldert Pol
 */
public class StartJoinSession {

	/**
	 * Private constructor, so this class cannot be initiated.
	 */
	private StartJoinSession() {
		// Nothing to do
	}

	/**
	 * Tries to start or join a session on the server. In case of an error, a
	 * message is shown.
	 * 
	 * @param project
	 *            The project for the session.
	 */
	public static void startJoinSession(final IProject project) {
		// No resources to ignore.
		List<IPath> ignoredResources = new ArrayList<IPath>(0);
		startJoinSession(project, ignoredResources);
	}

	public static void startJoinSession(final IProject project,
			List<IPath> ignoredResources) {
		try {
			Activator.SESSION.startJoinSession(project, ignoredResources);
		} catch (ProjectUnmanagedFilesException e) {
			String question = e.getMessage()
					+ "\n\n"
					+ "If you ignore them, changes will not be send to the server. To prevent this question, you can delete the files, or add them to version control."
					+ "\n\n" + "Ignore them?";

			boolean ignoreFiles = MessageDialog.openQuestion(null,
					"XTC Start/Join", question);
			if (ignoreFiles) {
				StartJoinSession
						.startJoinSession(project, e.getModifiedFiles());
			}
		} catch (XtcException e) {
			Activator.LOGGER.log(Level.WARNING, e);
			MessageDialog.openError(null, "XTC Start/Join", e.getMessage());
		}
	}

}
