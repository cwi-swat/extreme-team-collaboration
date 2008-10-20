package nl.jeldertpol.xtc.client.changes.editor;

import java.util.logging.Level;

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
	public void documentAboutToBeChanged(final DocumentEvent event) {
		IProject project = resource.getProject();
		IPath file = resource.getProjectRelativePath();

		// Length of the replaced document text
		int length = event.getLength();

		// The document offset.
		int offset = event.getOffset();

		// Text inserted into the document.
		String text = event.getText();

		Activator.getLogger().log(Level.FINEST, event.toString());

		Activator.SESSION
				.sendTextualChange(project, file, length, offset, text);
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
	 * Get the resource associated with the document.
	 * 
	 * @return The resource.
	 */
	public IResource getResource() {
		return resource;
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
