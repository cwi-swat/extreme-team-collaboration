package nl.jeldertpol.xtc.client.changes;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Listens to changes in the presentation of {@link IWorkbenchWindow}.
 * 
 * @author Jeldert Pol
 */
public class WindowListener implements IWindowListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui.
	 * IWorkbenchWindow)
	 */
	@Override
	public void windowActivated(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		System.out.println("windowActivated");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.IWorkbenchWindow
	 * )
	 */
	@Override
	public void windowClosed(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		System.out.println("windowClosed");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui.
	 * IWorkbenchWindow)
	 */
	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		System.out.println("windowDeactivated");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.IWorkbenchWindow
	 * )
	 */
	@Override
	public void windowOpened(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		System.out.println("windowOpened");
	}

	/**
	 * Registers a {@link PartListener} to the current {@link IWorkbenchPage}.
	 */
	public void updatePartListener() {
		System.out.println("updateDocumentListeners");
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();

		IPartListener2 partListener = new PartListener();
		workbenchPage.addPartListener(partListener);
	}
}
