package nl.jeldertpol.xtc.client.changes.editor;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
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
public class DocumentReplacer {

	public static ITextEditor findEditor(final IResource resource) {
		ITextEditor editor = null;

		// Look for an editor that has resource open.
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
		// Loop through windows
		mainloop: for (int i = 0; i < windows.length; i++) {
			IWorkbenchWindow window = windows[i];
			IWorkbenchPage[] pages = window.getPages();
			// Loop through pages in window
			for (int j = 0; j < pages.length; j++) {
				IWorkbenchPage page = pages[j];
				IEditorReference[] editorReferences = page
						.getEditorReferences();
				// Loop through editors in page
				for (int k = 0; k < editorReferences.length; k++) {
					IEditorReference editorReference = editorReferences[k];
					IEditorPart part = editorReference.getEditor(false);

					if (part instanceof AbstractTextEditor) {
						editor = (ITextEditor) part;

						String filename = editor.getEditorInput()
								.getToolTipText();
						IResource editorResouce = ResourcesPlugin
								.getWorkspace().getRoot().findMember(filename);
						if (resource.equals(editorResouce)) {
							break mainloop; // Break out of all loops
						}
					}
				}
			}
		}

		return editor;
	}

	/**
	 * TODO javadoc
	 * 
	 * @param resource
	 * @param length
	 * @param offset
	 * @param text
	 */
	public static void replace(final IResource resource, final int length,
			final int offset, final String text) {
		ITextEditor editor = findEditor(resource);

		IDocumentProvider documentProvider = editor.getDocumentProvider();
		IDocument document = documentProvider.getDocument(editor
				.getEditorInput());

		new DocumentReplacerJob(document, length, offset, text);
	}

}
