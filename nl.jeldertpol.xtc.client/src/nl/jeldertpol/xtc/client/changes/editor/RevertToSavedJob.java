package nl.jeldertpol.xtc.client.changes.editor;

import nl.jeldertpol.xtc.client.Activator;

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

	/**
	 * Reverts the editors input to its last save state. Schedules itself to be
	 * run.
	 * 
	 * @param editor
	 *            The editor to revert.
	 */
	public RevertToSavedJob(final ITextEditor editor) {
		super(RevertToSavedJob.class.getName() + ": "
				+ editor.getTitleToolTip());

		this.editor = editor;

		setPriority(INTERACTIVE);

		schedule();
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		IStatus status;

		editor.doRevertToSaved();

		status = new Status(Status.OK, Activator.PLUGIN_ID,
				"Reverted document successfully.");

		return status;
	}

}
