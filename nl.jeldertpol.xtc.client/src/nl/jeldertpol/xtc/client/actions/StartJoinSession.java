package nl.jeldertpol.xtc.client.actions;

import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.ProjectModifiedException;
import nl.jeldertpol.xtc.client.exceptions.ProjectUnmanagedFilesException;
import nl.jeldertpol.xtc.common.exceptions.XtcException;

import org.eclipse.core.resources.IProject;
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
		// Don't ignore unmanaged files.
		boolean ignoreUnmanagedFiles = false;
		// Don't send modified files.
		boolean sendModifiedFiles = false;

		startJoinSession(project, ignoreUnmanagedFiles, sendModifiedFiles);
	}

	public static void startJoinSession(final IProject project,
			final boolean ignoreUnmanagedFiles, final boolean sendModifiedFiles) {
		try {
			Activator.SESSION.startJoinSession(project, ignoreUnmanagedFiles,
					sendModifiedFiles);
		} catch (ProjectUnmanagedFilesException e) {
			String question = e.getMessage()
					+ "\n\n"
					+ "You can now send them to the server. If you choose not to do so, you can not not join the session. To prevent this question, you can delete the files, or add them to version control."
					+ "\n\n" + "Send these files to the server?";

			boolean userSendNewFiles = MessageDialog.openQuestion(null,
					"XTC Start/Join", question);
			if (userSendNewFiles) {
				StartJoinSession.startJoinSession(project, userSendNewFiles,
						sendModifiedFiles);
			}
		} catch (ProjectModifiedException e) {
			String question = e.getMessage()
					+ "\n\n"
					+ "If this project is already present on the server, it will send these modifications to all other clients, overwriting their files!"
					+ "\n\n" + "Send content?";

			boolean userSendFiles = MessageDialog.openQuestion(null,
					"XTC Start/Join", question);
			if (userSendFiles) {
				StartJoinSession.startJoinSession(project,
						ignoreUnmanagedFiles, userSendFiles);
			}
		} catch (XtcException e) {
			Activator.getLogger().log(Level.WARNING, e);
			MessageDialog.openError(null, "XTC Start/Join", e.getMessage());
		}
	}

}
