package nl.jeldertpol.xtc.client.changes.resource.jobs;

import java.io.InputStream;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Add a resource.
 * 
 * @author Jeldert Pol
 */
public class ResourceAddedResourceJob extends ResourceJob {

	private IResource resource;
	private int type;

	/**
	 * Move a resource to a new location.
	 * 
	 * @param resource
	 *            The resource to move.
	 * @param moveTo
	 *            The new location of the resource.
	 */
	public ResourceAddedResourceJob(IResource resource, int type) {
		super(resource.toString());

		this.resource = resource;
		this.type = type;
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
			if (type == IResource.FILE) {
				IFile file = (IFile) resource;
				InputStream source = null;
				boolean force = false;

				// Resource is not local...
				file.create(source, force, monitor);
				
				boolean local = true;
				file.setLocal(local, IFile.DEPTH_ZERO, monitor);
			} else if (type == IResource.FOLDER) {
				IFolder folder = (IFolder) resource;
				boolean force = false;
				boolean local = true;

				folder.create(force, local, monitor);
			}
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