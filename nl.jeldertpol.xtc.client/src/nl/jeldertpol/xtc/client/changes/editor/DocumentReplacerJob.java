package nl.jeldertpol.xtc.client.changes.editor;

import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.common.changes.TextualChange;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * An {@link UIJob} that can safely replace text in a document. Needs to be safe
 * to prevent invalid thread access exception from SWT.
 * 
 * @author Jeldert Pol
 */
public class DocumentReplacerJob extends UIJob {

	private final TextualChange change;

	/**
	 * Replace some text inside an editor. Schedules itself to be run. Will can
	 * {@link #replace(TextualChange)} when it runs.
	 * 
	 * @param change
	 *            The change to apply.
	 */
	public DocumentReplacerJob(final TextualChange change) {
		super(DocumentReplacerJob.class.getName());

		this.change = change;

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
		return DocumentReplacerJob.replace(change);
	}

	/**
	 * Call this when already running in a UIThread. Otherwise, use
	 * {@link DocumentReplacerJob}.
	 * 
	 * @param change
	 *            The change to apply.
	 * @return Status of replace.
	 */
	public static IStatus replace(final TextualChange change) {
		IStatus status;

		Activator.getLogger().log(Level.INFO, "Replacing text in resource "
				+ change.getFilename());

		IProject project = Activator.COMMON_ACTIONS.getProject(change
				.getProjectName());

		IResource resource = project.findMember(change.getFilename());
		ITextEditor editor = Activator.COMMON_ACTIONS.findEditor(resource);

		if (editor == null) {
			// There is no editor open with this resource
			// Ignoring change, will be requested when opening editor
			Activator.getLogger().log(Level.FINE,
					"Ignoring textual change, no editor for resource "
							+ resource.getName() + " found.");

			status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"TextualChange ignored.");
		} else {
			IDocumentProvider documentProvider = editor.getDocumentProvider();
			IDocument document = documentProvider.getDocument(editor
					.getEditorInput());
			try {
				synchronized (Activator.documentListener) {
					document.removeDocumentListener(Activator.documentListener);

					int offset = change.getOffset();
					int length = change.getLength();
					String text = change.getText();

					document.replace(offset, length, text);
				}

				status = new Status(IStatus.OK, Activator.PLUGIN_ID,
						"TextualChange applied successfully.");
			} catch (BadLocationException e) {
				Activator.getLogger().log(Level.SEVERE, e);

				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"TextualChange could not be applied.");

				// Rejoin. Already in a UIThread, so can call directly
				RevertToSavedJob.revertToSaved(resource);
				Activator.SESSION.requestTextualChanges(resource
						.getProjectRelativePath());
			} finally {
				document.addDocumentListener(Activator.documentListener);
			}
		}

		return status;
	}

}
