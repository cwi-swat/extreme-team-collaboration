package nl.jeldertpol.xtc.client.changes.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorDescriptor;
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

	public ITextEditor findEditor(final IResource resource) {
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
						ITextEditor anEditor = (ITextEditor) part;

						String filename = anEditor.getEditorInput()
								.getToolTipText();
						IResource editorResouce = ResourcesPlugin
								.getWorkspace().getRoot().findMember(filename);
						if (resource.equals(editorResouce)) {
							editor = anEditor;
							break mainloop; // Break out of all loops
						}
					}
				}
			}
		}

		return editor;
	}

	public ITextEditor findXtcEditor(final IResource resource) {
		return null;

	}

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
		ITextEditor editor = findEditor(resource);

		// There is no editor open with this resource
		if (editor == null) {
			IFile file = (IFile) resource;
			IEditorDescriptor desc = PlatformUI.getWorkbench()
					.getEditorRegistry().getDefaultEditor("foo.txt");

			// Try to create an editor
			new ClosedDocumentReplacerJob(length, offset, text, file, desc
					.getId());
		} else {
			new OpenedDocumentReplacerJob(editor, length, offset, text, false);
		}
	}
}
