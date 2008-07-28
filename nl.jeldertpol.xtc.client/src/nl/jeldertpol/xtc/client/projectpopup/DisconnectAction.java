package nl.jeldertpol.xtc.client.projectpopup;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Jeldert Pol
 */
public class DisconnectAction implements IObjectActionDelegate {

//	private ISelection selection;

	/**
	 * 
	 */
	public DisconnectAction() {
		// TODO Auto-generated constructor stub
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
