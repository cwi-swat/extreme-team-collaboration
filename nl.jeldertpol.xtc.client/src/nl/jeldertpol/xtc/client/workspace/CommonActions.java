package nl.jeldertpol.xtc.client.workspace;

import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Common actions related to the workspace.
 * 
 * @author Jeldert Pol
 */
public class CommonActions {

	public IProject getProject(final String projectName) {
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
	 * Open a new editor for the specified resource. Tries to open the default
	 * editor. If none is found, it uses a default text editor.
	 * 
	 * @param resource
	 *            The resource to open in an editor.
	 */
	public void openEditor(final IResource resource) {
		// Find default editor for resource
		IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry()
				.getDefaultEditor(resource.getName());

		// Default text editor
		String editorID;

		if (desc != null) {
			// Default editor found, use that one
			editorID = desc.getId();
		} else {
			// No default editor, use default text editor
			editorID = EditorsUI.DEFAULT_TEXT_EDITOR_ID;
		}

		// Required variables
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IFile file = (IFile) resource;

		try {
			// Open new editor
			page.openEditor(new FileEditorInput(file), editorID);
		} catch (PartInitException e) {
			Activator.getLogger().log(Level.SEVERE,
					"Editor could not be opened.", e);
		}
	}

}
