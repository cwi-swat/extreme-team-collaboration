package nl.jeldertpol.xtc.client.changes.editor;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * An {@link UIJob} that can safely replaces text in an {@link IDocument}. Needs
 * to be safe to prevent invalid thread access exception from SWT.
 * 
 * @author Jeldert Pol
 */
public class OpenedDocumentReplacerJob extends UIJob {

	private final ITextEditor editor;

	private final int length;

	private final int offset;

	private final String text;

	private final boolean save;

	/**
	 * TODO javadoc Constructor.
	 * 
	 * @param document
	 * @param length
	 * @param offset
	 * @param text
	 */
	public OpenedDocumentReplacerJob(final ITextEditor editor,
			final int length, final int offset, final String text,
			final boolean save) {
		super(editor.toString());

		this.editor = editor;
		this.length = length;
		this.offset = offset;
		this.text = text;
		this.save = save;

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
		System.out.println("Replacing text");
		IStatus status;

		try {
			synchronized (Activator.documentListener) {
				IDocumentProvider documentProvider = editor
						.getDocumentProvider();
				IDocument document = documentProvider.getDocument(editor
						.getEditorInput());

				document.removeDocumentListener(Activator.documentListener);
				document.replace(offset, length, text);
				if (save) {
					editor.doSave(monitor);
				}
				document.addDocumentListener(Activator.documentListener);
			}

			status = new Status(Status.OK, Activator.PLUGIN_ID,
					"Change applied successfully.");
		} catch (BadLocationException e) {
			e.printStackTrace();

			status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					"Change could not be applied.");
			// TODO revert, and re-apply all changes?
		}

		return status;
	}

}