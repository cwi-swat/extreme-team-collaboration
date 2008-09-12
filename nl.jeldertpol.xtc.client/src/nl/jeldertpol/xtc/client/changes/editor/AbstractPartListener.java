package nl.jeldertpol.xtc.client.changes.editor;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.WorkbenchPart;

/**
 * Listens to changes of {@link WorkbenchPart}, related to their presentation on
 * screen. Hides all non-used methods.
 * 
 * @author Jeldert Pol
 */
public abstract class AbstractPartListener implements IPartListener2 {

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public abstract void partActivated(IWorkbenchPartReference partRef);

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public final void partBroughtToTop(IWorkbenchPartReference partRef) {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public final void partClosed(IWorkbenchPartReference partRef) {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public final void partDeactivated(IWorkbenchPartReference partRef) {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public final void partHidden(IWorkbenchPartReference partRef) {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public final void partInputChanged(IWorkbenchPartReference partRef) {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public abstract void partOpened(IWorkbenchPartReference partRef);

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public final void partVisible(IWorkbenchPartReference partRef) {
		// Nothing to do
	}

}
