package nl.jeldertpol.xtc.client.actions;

import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.actions.sessions.ShowSessions;
import nl.jeldertpol.xtc.client.exceptions.ProjectNotOnClientException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Shows the sessions on the server. When one is selected, it tries to join that
 * session.
 * 
 * @author Jeldert Pol
 */
public class ShowSessionsAction extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		// Only show the sessions
		ShowSessions showSessions = new ShowSessions();
		String projectName = showSessions
				.showSessions("Sessions currently on the server. Press OK to join.");

		if (projectName != null) {
			IProject[] projects = Activator.COMMON_ACTIONS.getProjects();

			boolean found = false;
			for (IProject project : projects) {
				if (project.getName().equals(projectName)) {
					found = true;

					StartJoinSession.startJoinSession(project);

					break;
				}
			}

			if (!found) {
				try {
					throw new ProjectNotOnClientException(projectName);
				} catch (ProjectNotOnClientException e) {
					Activator.getLogger().log(Level.WARNING, e);
					MessageDialog.openError(null, "XTC Start/Join", e
							.getMessage());
				}
			}
		}

		return null;
	}

}
