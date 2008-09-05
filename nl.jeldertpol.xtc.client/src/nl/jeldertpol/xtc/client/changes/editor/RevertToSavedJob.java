package nl.jeldertpol.xtc.client.changes.editor;

import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Reverts the editors input to its last save state.
 * 
 * @author Jeldert Pol
 */
public class RevertToSavedJob extends UIJob {

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
				+ resource.getName());

		revertToSaved(resource);

		status = new Status(IStatus.OK, Activator.PLUGIN_ID,
				"Reverted document successfully.");

		return status;
	}

	/**
	 * Reverts the editors input to its last save state. Does nothing when
	 * resource is not opened by an editor.
	 * 
	 * Should only be called from within an {@link UIJob}. Otherwise use
	 * {@link RevertToSavedJob}.
	 * 
	 * TODO javadoc
	 */
	public static void revertToSaved(final IResource resource) {
		ITextEditor editor = Activator.COMMON_ACTIONS.findEditor(resource);

		if (editor != null) {
			// If resource is reverted, and being listened to, this revert
			// change is also caught by the listener (and thus send to the
			// server). We don't want this.
			if (resource == Activator.documentListener.getResource()) {
				IDocumentProvider documentProvider = editor
						.getDocumentProvider();
				IDocument document = documentProvider.getDocument(editor
						.getEditorInput());
				document.removeDocumentListener(Activator.documentListener);

				// Reload file from file system.
				editor.doRevertToSaved();

				document.addDocumentListener(Activator.documentListener);
			} else {
				// Not listening to this resource, so can revert.
				// Reload file from file system.
				editor.doRevertToSaved();
			}
		}

	}

}
