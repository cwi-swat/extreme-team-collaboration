package nl.jeldertpol.xtc.client.changes.editor;

import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Reverts the editors input to its last save state.
 * 
 * @author Jeldert Pol
 */
public class RevertToSavedJob extends UIJob {

	private final ITextEditor editor;

	private final IResource resource;

	/**
	 * Reverts the editors input to its last save state. Schedules itself to be
	 * run.
	 * 
	 * @param editor
	 *            The editor to revert.
	 */
	public RevertToSavedJob(final IResource resource) {
		super(RevertToSavedJob.class.getName() + ": " + resource.getName());

		this.resource = resource;
		this.editor = Activator.COMMON_ACTIONS.findEditor(resource);

		setPriority(INTERACTIVE);

		schedule();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public IStatus runInUIThread(final IProgressMonitor monitor) {
		IStatus status;

		Activator.LOGGER.log(Level.INFO, "Reverting to saved input "
				+ editor.getTitleToolTip());

		Activator.COMMON_ACTIONS.revertToSaved(resource);

		status = new Status(IStatus.OK, Activator.PLUGIN_ID,
				"Reverted document successfully.");

		return status;
	}

}
