package nl.jeldertpol.xtc.client.changes.resource.jobs;

import java.io.InputStream;
import java.util.Vector;

import nl.jeldertpol.xtc.client.Activator;
import nl.jeldertpol.xtc.common.conversion.Conversion;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Set content of a file.
 * 
 * @author Jeldert Pol
 */
public class ResourceSetContentJob extends HighPriorityJob {

	private IFile file;
	private byte[] content;

	/**
	 * Set the content of a file.
	 * 
	 * @param file
	 *            The file the content should be set of.
	 * @param content
	 *            The actual content for the file.
	 */
	public ResourceSetContentJob(IFile file, byte[] content) {
		super(file.toString());

		this.file = file;
		this.content = content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		IStatus status;

		try {
//			InputStream inputStream = file.getContents();
//			setBytesInInputStream(inputStream);
//			
//			file.setContents(inputStream, false, false, null);
			// Gaat niet goed?

			Vector<InputStream> list = (Vector<InputStream>) Conversion.byteToObject(content);
			InputStream inputStream = list.get(0);
			
//			InputStream inputStream = new ByteArrayInputStream(content);
			file.setContents(inputStream, false, false, monitor);
			
			
			
			status = new Status(Status.OK, Activator.PLUGIN_ID,
					"Resource content set successfully.");
		} catch (CoreException e) {
			e.printStackTrace();

			status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					"Resource content could not be set.");
			// TODO revert, and re-apply all changes?
		}

		return status;
	}
}
