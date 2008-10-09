package nl.jeldertpol.xtc.client.changes.editor;

import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * Listens to changes of {@link WorkbenchPart}, related to their presentation on
 * screen. Especially listens to parts made active.
 * 
 * @author Jeldert Pol
 */
public class PartListener extends AbstractPartListener {

	private IDocument currentDocument;

	/**
	 * Initializes a new {@link PartListener}.
	 */
	public PartListener() {
		currentDocument = null;
	}

	/**
	 * Get the {@link IDocument} of to the editor that belongs to the
	 * {@link IWorkbenchPage} of the {@link IWorkbenchPartReference}.
	 * 
	 * @param partRef
	 *            a reference in which the {@link IDocument} will be looked for.
	 * @return the {@link IDocument}, or <code>null</code> when no
	 *         {@link IDocument} is found.
	 */
	private IDocument getDocument(final IWorkbenchPartReference partRef) {
		IWorkbenchPage workbenchPage = partRef.getPage();
		IEditorPart editorPart = workbenchPage.getActiveEditor();

		if (editorPart != null) {
			IEditorInput editorInput = editorPart.getEditorInput();

			IDocumentProvider documentProvider = DocumentProviderRegistry
					.getDefault().getDocumentProvider(editorInput);
			IDocument document = documentProvider.getDocument(editorInput);

			if (document != null) {
				return document;
			} else {
				// This sometimes happens. Not sure why, but this works.
				if (documentProvider instanceof TextFileDocumentProvider) {
					try {
						TextFileDocumentProvider textFileDocumentProvider = (TextFileDocumentProvider) documentProvider;
						textFileDocumentProvider.connect(editorInput);

						document = textFileDocumentProvider
								.getDocument(editorInput);

						if (document != null) {
							return document;
						} else {
							Activator.LOGGER.log(Level.SEVERE,
									"No document for editor "
											+ editorInput.getToolTipText());
						}
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				Activator.LOGGER.log(Level.SEVERE, "No document for editor "
						+ editorInput.getToolTipText());
			}
		}
		return null;
	}

	/**
	 * Add a {@link DocumentListener} to the {@link IDocument}. Does nothing if
	 * the {@link IDocument} already has a listener.
	 * 
	 * @param document
	 *            the {@link IDocument} the listener should be added.
	 */
	private void addDocumentListener(final IDocument document) {
		// Listen to another document than the current one.
		if (!document.equals(currentDocument)) {
			if (currentDocument != null) {
				currentDocument
						.removeDocumentListener(Activator.documentListener);
			}
			currentDocument = document;
			currentDocument.addDocumentListener(Activator.documentListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.jeldertpol.xtc.client.changes.editor.AbstractPartListener#partActivated
	 * (org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partActivated(final IWorkbenchPartReference partRef) {
		// Getting document
		IDocument document = getDocument(partRef);
		if (document != null) {
			// Listen to document changes.
			addDocumentListener(document);

			// Get the IResource of the document.
			String documentName = partRef.getPage().getActiveEditor()
					.getEditorInput().getToolTipText();
			IResource resource = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(documentName);
			Activator.documentListener.setResource(resource);

			Activator.LOGGER.log(Level.INFO, "PartListener listens to "
					+ resource.toString());

			// Send WhosWhere information
			IProject project = resource.getProject();
			Activator.SESSION.sendWhosWhere(project, resource
					.getProjectRelativePath());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.jeldertpol.xtc.client.changes.editor.AbstractPartListener#partInputChanged
	 * (org.eclipse.ui.IWorkbenchPartReference)
	 */
	@Override
	public void partInputChanged(final IWorkbenchPartReference partRef) {
		// The input of this editor changed, for instance when a file is
		// renamed. This makes sure we still listen to changes.
		partActivated(partRef);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partOpened(final IWorkbenchPartReference partRef) {
		// Request changes from server
		IDocument document = getDocument(partRef);
		if (document != null) {
			String documentName = partRef.getTitleToolTip();
			IResource resource = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(documentName);
			// Only request changes when a resource exists.
			if (resource.exists()) {
				IPath resourcePath = resource.getProjectRelativePath();

				Activator.SESSION.requestTextualChanges(resourcePath);
			}
		}
	}

}
