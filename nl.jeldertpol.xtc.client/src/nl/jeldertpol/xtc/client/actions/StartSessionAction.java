package nl.jeldertpol.xtc.client.actions;

import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.actions.projects.ShowProjects;
import nl.jeldertpol.xtc.client.exceptions.XtcException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * @author Jeldert Pol
 * 
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
			try {
				Activator.SESSION.startJoinSession(project);
			} catch (XtcException e) {
				Activator.LOGGER.log(Level.WARNING, e);
				MessageDialog.openError(null, "XTC Start/Join", e.getMessage());
			}
		}

		return null;
	}

}
