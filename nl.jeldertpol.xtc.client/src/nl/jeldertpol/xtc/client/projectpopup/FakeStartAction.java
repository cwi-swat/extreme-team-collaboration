package nl.jeldertpol.xtc.client.projectpopup;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Does nothing, used to activate the plug-in.
 * 
 * TODO remove this action.
 * 
 * @author Jeldert Pol
 */
public class FakeStartAction implements IObjectActionDelegate {

//	private ISelection selection;

	/**
	 * 
	 */
	public FakeStartAction() {
		Activator.SESSION.addResourceChangeListener();
		Activator.fakeStart = true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
//		if (selection instanceof TreeSelection) {
//			ITreeSelection treeSelection = (ITreeSelection) selection;
//			IProject project = (IProject) treeSelection.getFirstElement();
//
//			//new StartJoin().startJoin(project);
//		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
//		this.selection = selection;
	}

}
