package nl.jeldertpol.xtc.client.workspace;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.client.changes.editor.RevertToSavedJob;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Common actions related to the workspace.
 * 
 * @author Jeldert Pol
 */
public class CommonActions {

	public IProject getProject(String projectName) {
		// TODO replace statements by call to this method.
		return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
	}

	public IProject[] getProjects() {
		return ResourcesPlugin.getWorkspace().getRoot().getProjects();
	}

	/**
	 * Find an opened editor for the given resource, or <code>null</code>.
	 * 
	 * @param resource
	 *            The resource to find an opened editor for.
	 * @return The opened editor, or <code>null</code>.
	 */
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

	/**
	 * Reverts the editors input to its last save state. Does nothing when
	 * resource is not opened by an editor.
	 * 
	 * Should only be called from within an {@link UIJob}. Otherwise use
	 * {@link RevertToSavedJob}.
	 * 
	 * TODO javadoc
	 */
	public void revertToSaved(IResource resource) {
		ITextEditor editor = findEditor(resource);
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
				// Reload file from file system.
				editor.doRevertToSaved();
			}
		}

	}
}
