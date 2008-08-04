package nl.jeldertpol.xtc.client.actions.projects;

import java.util.ArrayList;
import java.util.List;

import nl.jeldertpol.xtc.client.exceptions.UnrevisionedProjectException;
import nl.jeldertpol.xtc.client.session.infoExtractor.InfoExtractor;
import nl.jeldertpol.xtc.client.session.infoExtractor.SubclipseInfoExtractor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * Shows the project in the workspace.
 * 
 * @author Jeldert Pol
 */
public class ShowProjects {

	/**
	 * Shows a list of projects in the workspace. Only projects under version
	 * control are shown. A user is allowed to select one of these projects. The
	 * selected project is returned.
	 * 
	 * @param message
	 *            The message to display in the dialog.
	 * 
	 * @return The selected project, or <code>null</code> when no project was
	 *         selected, or cancel was pressed.
	 */
	public IProject showProjects(String message) {
		IProject project = null;

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();

		Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();

		ElementListSelectionDialog projectsDialog = new ElementListSelectionDialog(
				parent, new ProjectLabelProvider());
		IProject[] revisionedProjects = revisionedProjects(projects);
		projectsDialog.setElements(revisionedProjects);

		projectsDialog.setTitle("Projects");
		projectsDialog.setMessage(message);
		projectsDialog.setEmptyListMessage("No projects in workspace.");
		projectsDialog.setMultipleSelection(false);
		projectsDialog.setBlockOnOpen(true);
		projectsDialog.setHelpAvailable(false);

		int returnCode = projectsDialog.open();

		if (returnCode == ElementListSelectionDialog.OK) {
			Object selection = projectsDialog.getFirstResult();
			project = (IProject) selection;
		}

		return project;
	}

	/**
	 * Get only revisioned projects from a list of projects.
	 * 
	 * @param projects
	 *            The projects.
	 * @return The revisioned projects.
	 */
	private IProject[] revisionedProjects(IProject[] projects) {
		List<IProject> revisionedList = new ArrayList<IProject>();

		InfoExtractor infoExtractor = new SubclipseInfoExtractor();

		for (int i = 0; i < projects.length; i++) {
			try {
				infoExtractor.getRevision(projects[i]);
				revisionedList.add(projects[i]);
			} catch (UnrevisionedProjectException e) {
				// Project is unrevisioned. Do nothing.
			}
		}

		IProject[] revisioned = new IProject[revisionedList.size()];
		return revisionedList.toArray(revisioned);
	}

}
