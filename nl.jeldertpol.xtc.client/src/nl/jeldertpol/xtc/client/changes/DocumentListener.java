package nl.jeldertpol.xtc.client.changes;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;

/**
 * Listens to changes to documents (inside an active editor). 
 * 
 * @author Jeldert Pol
 */
public class DocumentListener implements IDocumentListener {

	public DocumentListener() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	@Override
	public void documentAboutToBeChanged(DocumentEvent event) {
		// TODO Auto-generated method stub
		System.out.println("documentAboutToBeChanged");
		System.out.println(event.toString());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
	 */
	@Override
	public void documentChanged(DocumentEvent event) {
		// TODO Auto-generated method stub
		System.out.println("documentChanged");
		System.out.println(event.toString());
	}

}
