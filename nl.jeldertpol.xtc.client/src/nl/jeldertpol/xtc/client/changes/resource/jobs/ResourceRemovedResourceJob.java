package nl.jeldertpol.xtc.client.changes.resource.jobs;

import nl.jeldertpol.xtc.client.Activator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Remove a resource.
 * 
 * @author Jeldert Pol
 */
public class ResourceRemovedResourceJob extends ResourceJob {

	private IResource resource;

	/**
	 * Move a resource to a new location.
	 * 
	 * @param resource
	 *            The resource to move.
	 * @param moveTo
	 *            The new location of the resource.
	 */
	public ResourceRemovedResourceJob(IResource resource) {
		super(resource.toString());

		this.resource = resource;
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
			boolean force = true;
			resource.delete(force, monitor);

			status = new Status(Status.OK, Activator.PLUGIN_ID,
					"Resource content set successfully.");
		} catch (CoreException e) {
			e.printStackTrace();

			status = new Status(Status.ERROR, Activator.PLUGIN_ID,
					"Resource content could not be set.");
		}

		return status;
	}
}