package nl.jeldertpol.xtc.client.actions;

import nl.jeldertpol.xtc.client.actions.projects.ShowProjects;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;

/**
 * Shows the projects in the workspace. When one is selected, it tries to start
 * a session.
 * 
 * @author Jeldert Pol
 */
public class StartSessionAction extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
	 * .ExecutionEvent)
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ShowProjects showProjects = new ShowProjects();
		IProject project = showProjects.showProjects("Projects in workspace.");

		if (project != null) {
			StartJoinSession.startJoinSession(project);
		}

		return null;
	}

}
