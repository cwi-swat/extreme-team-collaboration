package nl.jeldertpol.xtc.client.changes;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.progress.UIJob;

/**
 * An {@link UIJob} that can safely replaces text in an {@link IDocument}. Needs
 * to be safe to prevent invalid thread access exception from SWT.
 * 
 * @author Jeldert Pol
 */
public class DocumentReplacerJob extends UIJob {

	private IDocument document;

	private int length;

	private int offset;

	private String text;

	/**
	 * Constructor.
	 * 
	 * @param document
	 * @param length
	 * @param offset
	 * @param text
	 */
	public DocumentReplacerJob(IDocument document, int length, int offset,
			String text) {
		super(document.toString());

		this.document = document;
		this.length = length;
		this.offset = offset;
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		System.out.println("Que?");
		IStatus status;
		
		try {
			document.replace(offset, length, text);
			status = new Status(Status.OK, Activator.PLUGIN_ID, "Change applied successfully.");
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			status = new Status(Status.ERROR, Activator.PLUGIN_ID, "Change could not be applied.");
		}

		return status;
	}

}
