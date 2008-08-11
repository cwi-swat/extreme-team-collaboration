package nl.jeldertpol.xtc.client.changes.editor;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Apply changes to documents.
 * 
 * @author Jeldert Pol
 */
public class DocumentReplacer implements IJobChangeListener {

	private IDocument document;

	/**
	 * TODO javadoc
	 * 
	 * @param resource
	 * @param length
	 * @param offset
	 * @param text
	 */
	public void replace(final IResource resource, final int length,
			final int offset, final String text) {
		// Look for an editor that has resource open.
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			IWorkbenchWindow window = windows[i];
			IWorkbenchPage[] pages = window.getPages();
			for (int j = 0; j < pages.length; j++) {
				IWorkbenchPage page = pages[j];
				IEditorReference[] editorReferences = page
						.getEditorReferences();
				for (int k = 0; k < editorReferences.length; k++) {
					IEditorReference editorReference = editorReferences[k];
					IEditorPart part = editorReference.getEditor(false);

					if (!(part instanceof AbstractTextEditor)) {
						return;
					}
					ITextEditor editor = (ITextEditor) part;

					String filename = editor.getEditorInput().getToolTipText();
					IResource editorResouce = ResourcesPlugin.getWorkspace()
							.getRoot().findMember(filename);
					if (resource.equals(editorResouce)) {
						// Editor with resource found.

						IDocumentProvider documentProvider = editor
								.getDocumentProvider();
						document = documentProvider.getDocument(editor
								.getEditorInput());

						// int offset = doc.getLineOffset(doc.getNumberOfLines()
						// -
						// 4);
						// doc.replace(offset, 0, pasteText + "\n");

						DocumentReplacerJob job = new DocumentReplacerJob(
								document, length, offset, text);
						job.addJobChangeListener(this);
						job.schedule();
					}
				}
			}
		}
	}

	@Override
	public void aboutToRun(final IJobChangeEvent event) {
		// Temporarily remove listener
		document.removeDocumentListener(Activator.documentListener);
	}

	@Override
	public void awake(final IJobChangeEvent event) {
		// Nothing to do
	}

	@Override
	public void done(final IJobChangeEvent event) {
		// Add listener again
		document.addDocumentListener(Activator.documentListener);
	}

	@Override
	public void running(final IJobChangeEvent event) {
		// Nothing to do
	}

	@Override
	public void scheduled(final IJobChangeEvent event) {
		// Nothing to do
	}

	@Override
	public void sleeping(final IJobChangeEvent event) {
		// Nothing to do
	}
}
