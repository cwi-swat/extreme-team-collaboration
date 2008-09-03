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
 * Apply changes to documents.
 * 
 * @author Jeldert Pol
 */
public class DocumentReplacerJob extends UIJob {

	private TextualChange change;

	public DocumentReplacerJob(TextualChange change) {
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
	public IStatus runInUIThread(IProgressMonitor monitor) {
		return DocumentReplacerJob.replace(change);
	}

	public static IStatus replace(TextualChange change) {
		IStatus status;

		Activator.LOGGER.log(Level.INFO, "Replacing text in resource "
				+ change.getFilename());

		IProject project = Activator.COMMON_ACTIONS.getProject(change
				.getProjectName());

		IResource resource = project.findMember(change.getFilename());
		ITextEditor editor = Activator.COMMON_ACTIONS.findEditor(resource);

		if (editor == null) {
			// There is no editor open with this resource
			// Ignoring change, will be requested when opening editor
			Activator.LOGGER.log(Level.FINE,
					"Ignoring textual change, no editor for resource "
							+ resource.getName() + " found.");

			status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"TextualChange ignored.");
		} else {
			status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"Change applied successfully.");
			try {
				synchronized (Activator.documentListener) {
					IDocumentProvider documentProvider = editor
							.getDocumentProvider();
					IDocument document = documentProvider.getDocument(editor
							.getEditorInput());

					document.removeDocumentListener(Activator.documentListener);

					int offset = change.getOffset();
					int length = change.getLength();
					String text = change.getText();

					document.replace(offset, length, text);

					document.addDocumentListener(Activator.documentListener);
				}

				status = new Status(IStatus.OK, Activator.PLUGIN_ID,
						"TextualChange applied successfully.");
			} catch (BadLocationException e) {
				Activator.LOGGER.log(Level.SEVERE, e);

				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"TextualChange could not be applied.");
				// TODO revert, and re-apply all changes?
			}
		}

		return status;
	}

}
