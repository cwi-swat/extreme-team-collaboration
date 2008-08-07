package nl.jeldertpol.xtc.client.changes.resource;

import java.io.InputStream;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Move a resource.
 * 
 * @author Jeldert Pol
 */
public class ResourceSetContentJob extends ResourceJob {

	private IFile file;
	InputStream content;

	/**
	 * Move a resource to a new location.
	 * 
	 * @param resource
	 *            The resource to move.
	 * @param moveTo
	 *            The new location of the resource.
	 */
	public ResourceSetContentJob(IFile file, InputStream content) {
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
			file.setContents(content, false, false, null);
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
