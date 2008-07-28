package nl.jeldertpol.xtc.client.changes;

import org.eclipse.core.runtime.CoreException;
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

	private DocumentListener documentListener;
	private IDocument currentDocument;

	/**
	 * Initializes a new {@link PartListener}.
	 */
	public PartListener() {
		System.out.println("New PartListener()");
		documentListener = new DocumentListener();
		currentDocument = null;
	}

	/**
	 * Get the {@link IDocument} of to the editor that belongs to the
	 * {@link IWorkbenchPage} of the {@link IWorkbenchPartReference}.
	 * 
	 * @param partRef
	 *            a reference in which the {@link IDocument} will be looked for.
	 * @return the {@link IDocument}.
	 */
	private IDocument getDocument(IWorkbenchPartReference partRef) {
		IWorkbenchPage workbenchPage = partRef.getPage();
		IEditorPart editorPart = workbenchPage.getActiveEditor();

		if (editorPart != null) {
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
	private void addDocumentListener(IDocument document) {
		if (document != null) {
			if (!document.equals(currentDocument)) {
				if (currentDocument != null) {
					currentDocument.removeDocumentListener(documentListener);
				}
				currentDocument = document;
				currentDocument.addDocumentListener(documentListener);
			}
		} else {
			currentDocument = null;
			System.out.println("null");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		System.out.println("partActivated " + partRef.getTitle());
		addDocumentListener(getDocument(partRef));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		System.out.println("partBroughtToTop " + partRef.getTitle());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		System.out.println("partClosed " + partRef.getTitle());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		System.out.println("partDeactivated " + partRef.getTitle());
		// removeDocumentListener(getDocument(partRef));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		System.out.println("partHidden " + partRef.getTitle());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		System.out.println("partInputChanged " + partRef.getTitle());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		System.out.println("partOpened " + partRef.getTitle());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub
		System.out.println("partVisible " + partRef.getTitle());
	}

}
