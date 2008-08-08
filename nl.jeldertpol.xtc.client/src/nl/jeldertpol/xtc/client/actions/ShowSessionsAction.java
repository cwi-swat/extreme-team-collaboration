package nl.jeldertpol.xtc.client.actions;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.actions.sessions.ShowSessions;
import nl.jeldertpol.xtc.client.exceptions.ProjectNotOnClientException;
import nl.jeldertpol.xtc.client.exceptions.XtcException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Shows the projects in the workspace.
 * 
 * @author Jeldert Pol
 */
public class ShowSessionsAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Only show the sessions
		ShowSessions showSessions = new ShowSessions();
		String projectName = showSessions
				.showSessions("Sessions currently on the server. Press OK to join.");

		if (projectName != null) {
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
					.getProjects();
			boolean found = false;
			for (int i = 0; i < projects.length; i++) {
				if (projects[i].getName().equals(projectName)) {
					found = true;
					try {
						Activator.SESSION.joinSession(projects[i]);
					} catch (XtcException e) {
						e.printStackTrace();
						MessageDialog.openError(null, "XTC Start/Join", e
								.getMessage());
					}
					break;
				}
			}

			if (!found) {
				try {
					throw new ProjectNotOnClientException(projectName);
				} catch (ProjectNotOnClientException e) {
					e.printStackTrace();
					MessageDialog.openError(null, "XTC Start/Join", e.getMessage());
				}
			}
		}

		return null;
	}

}
