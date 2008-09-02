package nl.jeldertpol.xtc.client.changes.editor;

import java.util.logging.Level;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
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
public class PartListener implements IPartListener2 {

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
			// TODO rewrite?
			// Start Cola
			try {
				IEditorInput editorInput = editorPart.getEditorInput();
				IDocumentProvider dp = DocumentProviderRegistry.getDefault()
						.getDocumentProvider(editorInput);
				IDocument document = dp
						.getDocument(editorPart.getEditorInput());

				if (document != null) {
					return document;
				} else {
					if (dp instanceof TextFileDocumentProvider) {
						((TextFileDocumentProvider) dp).connect(editorPart
								.getEditorInput());
						document = ((TextFileDocumentProvider) dp)
								.getDocument(editorPart.getEditorInput());

						if (document != null) {
							return document;
						} else {
							// Activator.getDefault().getLog().log(new
							// Status(IStatus.ERROR, Activator.PLUGIN_ID, 0,
							// "Unable to get reference to editor's document.  Shared session not created."
							// , null));
						}
					}

					// Activator.getDefault().getLog().log(new
					// Status(IStatus.ERROR,
					// Activator.PLUGIN_ID, 0,
					// "Unable to get reference to editor's document.  Shared session not created."
					// , null));
				}
			} catch (CoreException e) {
				// Activator.getDefault().getLog().log(new Status(IStatus.ERROR,
				// Activator.PLUGIN_ID, 0, e.getLocalizedMessage(), e)); }
			}
			// End Cola
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
	 * @seeorg.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partActivated(final IWorkbenchPartReference partRef) {
		// Document afluisteren, linken met IResource
		// Getting document
		IDocument document = getDocument(partRef);
		if (document != null) {
			// Listen to document changes.
			addDocumentListener(document);

			// Get the IResource of the document.
			String documentName = partRef.getPage().getActiveEditor()
					.getEditorInput().getToolTipText();
			IResource resouce = ResourcesPlugin.getWorkspace().getRoot()
					.findMember(documentName);
			Activator.documentListener.setResource(resouce);

			Activator.LOGGER.log(Level.INFO, "PartListener listens to "
					+ resouce.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partBroughtToTop(final IWorkbenchPartReference partRef) {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partClosed(final IWorkbenchPartReference partRef) {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partDeactivated(final IWorkbenchPartReference partRef) {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partHidden(final IWorkbenchPartReference partRef) {
		// Nothing to do
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partInputChanged(final IWorkbenchPartReference partRef) {
		// Nothing to do
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
		String documentName = partRef.getTitleToolTip();
		IResource resource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(documentName);
		IPath resourcePath = resource.getProjectRelativePath();

		Activator.SESSION.requestTextualChanges(resourcePath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partVisible(final IWorkbenchPartReference partRef) {
		// Nothing to do
	}

}
