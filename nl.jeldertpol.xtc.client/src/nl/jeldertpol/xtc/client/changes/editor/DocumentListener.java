package nl.jeldertpol.xtc.client.changes.editor;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

/**
 * Listens to changes to documents (inside an active editor).
 * 
 * @author Jeldert Pol
 */
public class DocumentListener implements IDocumentListener {
	
	private IResource resource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org
	 * .eclipse.jface.text.DocumentEvent)
	 */
	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		// TODO Auto-generated method stub
		System.out.println("documentAboutToBeChanged");
		System.out.println(event.toString());

		IProject project = resource.getProject();
		IPath file = resource.getProjectRelativePath();

		// Length of the replaced document text
		int length = event.getLength();

		// The document offset.
		int offset = event.getOffset();

		// Text inserted into the document.
		String text = event.getText();

		Activator.SESSION.sendChange(project, file, length, offset, text);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.
	 * jface.text.DocumentEvent)
	 */
	@Override
	public void documentChanged(final DocumentEvent event) {
		// Nothing to do
	}

	/**
	 * Set the resource associated with the document.
	 * 
	 * @param resource
	 *            The resource associated with the document.
	 */
	public void setResource(final IResource resource) {
		this.resource = resource;
	}

}
