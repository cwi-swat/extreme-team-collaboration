package nl.jeldertpol.xtc.client.projectpopup;

import java.util.List;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.exceptions.XTCException;
import nl.jeldertpol.xtc.common.Session.SimpleSession;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Jeldert Pol
 */
public class StartJoinAction implements IObjectActionDelegate {

	private ISelection selection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.
	 * action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		if (selection instanceof TreeSelection) {
			ITreeSelection treeSelection = (ITreeSelection) selection;
			IProject project = (IProject) treeSelection.getFirstElement();
			startJoin(project);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	private void startJoin(IProject project) {
		try {
			List<SimpleSession> sessions = Activator.session.getSessions();
			String projectName = project.getName();
			
			boolean present = false;
			for (SimpleSession simpleSession : sessions) {
				if (simpleSession.getProjectName().equals(projectName)) {
					Activator.session.joinSession(project);
					present = true;
					break;
				}
			}
			
			if (!present) {
				Activator.session.startSession(project);
			}
		} catch (XTCException e) {
			e.printStackTrace();
			MessageDialog.openError(null, "XTC Start/Join", e.getMessage());
		}
	}

}
